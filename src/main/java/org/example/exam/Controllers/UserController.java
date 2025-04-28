package org.example.exam.Controllers;

import lombok.RequiredArgsConstructor;
import org.example.exam.DTOS.UserDTO;
import org.example.exam.entity.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    private final JavaMailSender mailSender;

    public UserController(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }



        @PostMapping("/register")
        public String processRegistration(@ModelAttribute("user") UserDTO userDTO, Model model) {
            if (!userDTO.getPassword().equals(userDTO.getConfirmPassword())) {
                model.addAttribute("error", "Passwords do not match!");
                return "register";
            }
            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setPassword(userDTO.getPassword());
            user.setEmail(userDTO.getEmail());
            Integer verificationCode = (int) ((Math.random() * 9000) + 1000);
            Thread thread = new Thread(() -> {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(userDTO.getEmail());
                message.setSubject("Your Verification Code");
                message.setText("Your verification code is: " + verificationCode);
                mailSender.send(message);
            });
            thread.start();
            user.setVerified_code(Integer.toString(verificationCode));
            model.addAttribute("user", user);
            return "email";
        }
    }

