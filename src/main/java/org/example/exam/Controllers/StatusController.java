package org.example.exam.Controllers;

import lombok.RequiredArgsConstructor;
import org.example.exam.entity.Status;
import org.example.exam.entity.StatusListWrapper;
import org.example.exam.repository.StatusRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @PostMapping("/saveStatuses")
    public String saveStatuses(@ModelAttribute StatusListWrapper statusListWrapper, Model model) {
        List<Status> statuses = statusListWrapper.getStatuses();


        Set<Integer> uniquePositions = new HashSet<>();
        List<Integer> duplicatePositions = new ArrayList<>();

        for (Status status : statuses) {
            if (!uniquePositions.add(status.getPositionNumber())) {
                duplicatePositions.add(status.getPositionNumber());
            }
        }

        if (!duplicatePositions.isEmpty()) {
            model.addAttribute("errorMessage", "Position raqamlari takrorlanmasligi kerak. Takrorlangan raqam(lar): " + duplicatePositions);
            model.addAttribute("statusListWrapper", statusListWrapper);
            model.addAttribute("allStatus", statuses);
            model.addAttribute("count", statuses.size());
            return "manage-orders";
        }

        statusRepository.saveAll(statuses);
        return "redirect:/home";
    }


}
