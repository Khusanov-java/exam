package org.example.exam.Controllers;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.exam.DTOS.UserDTO;
import org.example.exam.entity.*;
import org.example.exam.repository.RoleRepository;
import org.example.exam.repository.StatusRepository;
import org.example.exam.repository.TaskRepository;
import org.example.exam.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final StatusRepository statusRepository;
    private final RoleRepository roleRepository;


    @GetMapping("/registerPage")
    public String registerPage(Model model) {
        model.addAttribute("user", new UserDTO());
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
                session.setAttribute("user", user); // сохраняешь в сессию
                return "redirect:/home";
            } else {
                model.addAttribute("error", "Incorrect password");
            }
        } else {
            model.addAttribute("error", "Email not found");
        }
        return "login";
    }

    @GetMapping("/home")
    public String viewBoard(Model model, @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,HttpSession session) {
        if (user == null) {
            return "redirect:/login";
        }
        User sessionUser = (User)session.getAttribute("user");

        Optional<User> optionalUser = userRepository.findByEmail(user.getUsername());
        if (optionalUser.isEmpty()) {
            return "redirect:/login";
        }
        User user1 = optionalUser.get();
        for (int i = 0; i < 10; i++) {
            System.out.println("user1 = " + i);
        }
        if (sessionUser != null) {
            user1 = sessionUser;
            System.out.println("user1 = " + user1);
        }
        List<Status> statuses = statusRepository.findAllByActiveTrueOrderByPositionNumber();
        session.setAttribute("statusList", statuses);
        model.addAttribute("statusOrdered", statuses);
        List<Task> allTasks = taskRepository.findAll();
        model.addAttribute("allTasks", allTasks);
        int minPosition = statuses.stream().mapToInt(Status::getPositionNumber).min().orElse(Integer.MAX_VALUE);
        int maxPosition = statuses.stream().mapToInt(Status::getPositionNumber).max().orElse(Integer.MIN_VALUE);
        model.addAttribute("minPosition", minPosition);
        model.addAttribute("maxPosition", maxPosition);
        model.addAttribute("user", user1);
        return "home";
    }

    @GetMapping("/user/update/{userId}")
    public String updateUser(Model model, @PathVariable Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        System.out.println("userId = " + userId);
        User user = optionalUser.get();
        System.out.println("user = " + user);
        model.addAttribute("user", user);
        return "update-user";
    }

    @GetMapping("/admin")
    public String adminPage(Model model) {
        List<Role> roles = roleRepository.findAll();
        List<User> allUsers = userRepository.findAll();
        model.addAttribute("allUsers", allUsers);
        model.addAttribute("roles", roles);
        return "user-crud";
    }

    @GetMapping("/addUser")
    public String addUser(Model model) {
        List<Role> roles = roleRepository.findAll();
        model.addAttribute("roles", roles);
        return "add-user";
    }

    @GetMapping("/admin/user/update/{userId}")
    public String adminUpdateUser(Model model, @PathVariable Integer userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User user = optionalUser.get();
        model.addAttribute("user", user);
        model.addAttribute("roles",roleRepository.findAll());
        return "admin-update-user";
    }

    @GetMapping("/manageOrders")
    public String manageOrders(Model model) {
        List<Status> all = statusRepository.findAll();
        model.addAttribute("allStatus", all);
        long count = statusRepository.count();
        model.addAttribute("count", count);
        model.addAttribute("statusListWrapper", new StatusListWrapper()); // Initialize StatusListWrapper
        return "manage-orders";
    }

}
