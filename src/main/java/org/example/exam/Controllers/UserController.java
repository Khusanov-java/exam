package org.example.exam.Controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class UserController {
    private final JavaMailSender mailSender;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;




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
        session.setAttribute("user",userDTO);
        return "email";
    }


    @PostMapping("/verify/process")
    private String processVerification( HttpSession session, @RequestParam("verification_code") String code) {
        System.out.println("Saving");
        UserDTO user = (UserDTO)session.getAttribute("user");
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
}

