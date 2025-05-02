package org.example.exam.Controllers;

import lombok.RequiredArgsConstructor;
import org.example.exam.DTOS.AdminUpdateUserDTO;
import org.example.exam.entity.User;
import org.example.exam.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/userUpdate/{userId}")
    public String updateUser(@PathVariable int userId,@ModelAttribute AdminUpdateUserDTO addUserDTO, Model model) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(addUserDTO.getPassword()));
        user.setEmail(addUserDTO.getEmail());
        user.setUsername(addUserDTO.getUsername());
        user.setRoles(addUserDTO.getRoles());
        userRepository.save(user);
        return "redirect:/admin";
    }

}
