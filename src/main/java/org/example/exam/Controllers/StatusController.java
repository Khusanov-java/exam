package org.example.exam.Controllers;

import lombok.RequiredArgsConstructor;
import org.example.exam.entity.Status;
import org.example.exam.repository.StatusRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/status")
@RequiredArgsConstructor
public class StatusController {
    private final StatusRepository statusRepository;

    @GetMapping("/addStatus")
    public String addStatus(Model model) {
        model.addAttribute("status", new Status());
        return "addStatus";
    }

    @PostMapping("/add")
    public String saveStatus(@ModelAttribute Status status) {
        long count = statusRepository.count();
        status.setActive(true);
        status.setPositionNumber((int) (count+1));
        statusRepository.save(status);
        return "redirect:/home";
    }
}
