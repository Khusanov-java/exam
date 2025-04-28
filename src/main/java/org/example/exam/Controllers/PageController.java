package org.example.exam.Controllers;

import org.example.exam.DTOS.UserDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user",new UserDTO());
        return "register";
    }

}
