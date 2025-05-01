package org.example.exam.Controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.exam.DTOS.UserDTO;
import org.example.exam.entity.Task;
import org.example.exam.entity.User;
import org.example.exam.repository.TaskRepository;
import org.example.exam.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class PageController {
    private final PasswordEncoder passwordEncoder;


    private final TaskRepository taskRepository;
    private final UserRepository userRepository;


    @GetMapping("/registerPage")
    public String registerPage(Model model) {
        model.addAttribute("user",new UserDTO());
        return "register";
    }

    @GetMapping("/login")
    public String loginPage(Model model, HttpSession session) {
        return "login";
    }

    @PostMapping("/user/login")
    public String login(Model model,
                        HttpSession session,
                        @RequestParam String email,
                        @RequestParam String password) {
        Optional<User> byEmail = userRepository.findByEmail(email);

        if (byEmail.isPresent()) {
            User user = byEmail.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                session.setAttribute("user",user);
                return "redirect:/home";
            } else {
                model.addAttribute("error", "Incorrect password");
            }
        } else {
            model.addAttribute("error", "Email not found");
        }

        return "redirect:/login"; // show login page with error message
    }

    @GetMapping("/home")
    public String home(Model model,HttpSession session) {
        Object user = session.getAttribute("user");
        User userDTO = (User) user;
        List<Task> tasksByUser = taskRepository.findTasksByUser(userDTO);
        model.addAttribute("tasks", tasksByUser);
        return "home";
    }

}
