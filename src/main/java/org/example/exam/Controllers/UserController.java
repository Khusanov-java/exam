package org.example.exam.Controllers;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.example.exam.DTOS.AddUserDTO;
import org.example.exam.DTOS.UpdateUserDTO;
import org.example.exam.DTOS.UserDTO;
import org.example.exam.Service.UserService;
import org.example.exam.entity.*;
import org.example.exam.repository.AttachmentRepository;
import org.example.exam.repository.RoleRepository;
import org.example.exam.repository.TaskRepository;
import org.example.exam.repository.UserRepository;
import org.hibernate.Session;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final JavaMailSender mailSender;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AttachmentRepository attachmentRepository;


    @SneakyThrows
    @PostMapping("/register")
    public String processRegistration(Model model,
                                      HttpSession session,
                                      @RequestParam("email") String email,
                                      @RequestParam("password") String password,
                                      @RequestParam String username,
                                      @RequestParam MultipartFile file
                                      ) {
        Attachment attachment = new Attachment();
        attachment.setContent(file.getBytes());
        attachmentRepository.save(attachment);
        UserDTO userDTO = new UserDTO(email,password,username,attachment);
        Integer verificationCode = (int) ((Math.random() * 9000) + 1000);
        Thread thread = new Thread(() -> {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(userDTO.getEmail());
            message.setSubject("Your Verification Code");
            message.setText("Your verification code is: " + verificationCode);
            mailSender.send(message);
            System.out.println("Email sent");
        });
        thread.start();
        userDTO.setVerifiedPassword(verificationCode);
        session.setAttribute("userDTO",userDTO);
        return "email";
    }


    @PostMapping("/verify/process")
    public String processVerification( HttpSession session, @RequestParam("verification_code") String code) {
        System.out.println("Saving");
        UserDTO user = (UserDTO)session.getAttribute("userDTO");
        Integer i = Integer.parseInt(code);
        if (i.equals(user.getVerifiedPassword())) {
            User user1= new User();
            user1.setEmail(user.getEmail());
            user1.setPassword(passwordEncoder.encode(user.getPassword()));
            user1.setUsername(user.getUsername());
            user1.setAttachment(user.getAttachment());
            Role role=roleRepository.findRoleByRole(Roles.PROGRAMMER.name());
            List<Role> roles=new ArrayList<>();
            roles.add(role);
            user1.setRoles(roles);
            userService.save(user1);
            System.out.println("saved");
        }
        return "redirect:/login";
    }

    @PostMapping("/user/update")
    public String updateUser(@ModelAttribute UpdateUserDTO userDTO,
                             @RequestParam(value = "file", required = false) MultipartFile file,
                             HttpSession session) throws IOException {

        Optional<User> optionalUser = userRepository.findById(userDTO.getId());
        if (optionalUser.isEmpty()) {
            return "redirect:/error"; // обработка ошибки
        }

        User user = optionalUser.get();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());

        if (file != null && !file.isEmpty()) {
            Attachment attachment = new Attachment();
            attachment.setContent(file.getBytes());
            attachmentRepository.save(attachment);
            user.setAttachment(attachment);
        }

        User savedUser = userRepository.save(user);
        session.setAttribute("user", savedUser);
        return "redirect:/home";
    }



    @PostMapping("/user/add")
    public String addUser(@ModelAttribute AddUserDTO addUserDTO) {
        User user = new User();
        user.setUsername(addUserDTO.getUsername());
        user.setEmail(addUserDTO.getEmail());
        user.setPassword(passwordEncoder.encode(addUserDTO.getPassword()));
        user.setRoles(addUserDTO.getRoles());
        userRepository.save(user);
        return "redirect:/admin";
    }

    @PostMapping("/user/delete/{userId}")
    public String deleteUser(@PathVariable Integer userId) {
        userRepository.deleteById(userId);
        return "redirect:/admin";
    }

    @GetMapping("/photo/user/{id}")
    public void photo(@PathVariable int id, HttpServletResponse resp) throws IOException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        User user = optionalUser.get();
        if (user.getAttachment() == null) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        Optional<Attachment> optionalAttachment = attachmentRepository.findById(user.getAttachment().getId());
        if (optionalAttachment.isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            return;
        }

        Attachment attachment = optionalAttachment.get();

        resp.setContentType("image/jpeg"); // or detect type dynamically if you store it
        resp.getOutputStream().write(attachment.getContent());
        resp.getOutputStream().flush();
    }

}

