package org.example.exam.Controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.exam.DTOS.AddUserDTO;
import org.example.exam.DTOS.UpdateUserDTO;
import org.example.exam.DTOS.UserDTO;
import org.example.exam.Service.UserService;
import org.example.exam.entity.Role;
import org.example.exam.entity.Roles;
import org.example.exam.entity.User;
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


    @PostMapping("/register")
    public String processRegistration(Model model, HttpSession session,@RequestParam("email") String email, @RequestParam("password") String password,@RequestParam String username) {
        UserDTO userDTO = new UserDTO(email,password,username);
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
    public String updateUser(@ModelAttribute UpdateUserDTO userDTO, HttpSession session) {
        Optional<User> optionalUser = userRepository.findById(userDTO.getId());
        User user = optionalUser.get();
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        User savedUser = userRepository.save(user);
        session.setAttribute("user",savedUser);
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


}

