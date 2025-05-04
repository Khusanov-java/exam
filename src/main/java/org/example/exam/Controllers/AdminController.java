package org.example.exam.Controllers;

import lombok.RequiredArgsConstructor;
import org.example.exam.DTOS.AdminUpdateUserDTO;
import org.example.exam.entity.Attachment;
import org.example.exam.entity.User;
import org.example.exam.repository.AttachmentRepository;
import org.example.exam.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AttachmentRepository attachmentRepository;

    @PostMapping("/userUpdate/{userId}")
    public String updateUser(@PathVariable int userId,@ModelAttribute AdminUpdateUserDTO addUserDTO, Model model,@RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.get();
        user.setPassword(passwordEncoder.encode(addUserDTO.getPassword()));
        user.setEmail(addUserDTO.getEmail());
        user.setUsername(addUserDTO.getUsername());
        user.setRoles(addUserDTO.getRoles());

        if (file != null && !file.isEmpty()) {
            Attachment attachment = new Attachment();
            attachment.setContent(file.getBytes());
            attachmentRepository.save(attachment);
            user.setAttachment(attachment);
        }

        userRepository.save(user);
        return "redirect:/admin";
    }

}
