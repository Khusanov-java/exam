package org.example.exam.Controllers;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.example.exam.Service.UserService;
import org.example.exam.entity.Attachment;
import org.example.exam.entity.Status;
import org.example.exam.entity.Task;
import org.example.exam.entity.User;
import org.example.exam.repository.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class TaskController {
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TaskRepository taskRepository;
    private final StatusRepository statusRepository;
    private final UserRepository userRepository;
    private final AttachmentRepository attachmentRepository;

    @GetMapping("/task/addTaskPage")
    public String addTaskPage(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("statuses", statusRepository.findAll());
        return "add-task";
    }

    @SneakyThrows
    @PostMapping("/save/task")
    public String saveTask(HttpServletRequest request, @RequestParam MultipartFile file) {
        Task task = new Task();
        task.setTitle(request.getParameter("title"));
        task.setUser(userRepository.findById(Integer.parseInt(request.getParameter("user"))).get());
        task.setStatus(statusRepository.findById(Integer.parseInt(request.getParameter("statusId"))).get());
        Attachment attachment = new Attachment();
        attachmentRepository.save(attachment);
        attachment.setContent(file.getBytes());
        task.setAttachment(attachment);
        taskRepository.save(task);
        return "redirect:/home";
    }

    @SneakyThrows
    @GetMapping("/photo/{taskId}")
    public void photo(@PathVariable int taskId, HttpServletResponse resp) {
        Task task = taskRepository.findById(taskId).get();
        Attachment attachment = attachmentRepository.findById(task.getAttachment().getId()).get();
        resp.getOutputStream().write(attachment.getContent());
    }

    @PostMapping("/task/moveLeft/{taskId}")
    public String moveLeft(@PathVariable int taskId, HttpSession session) {
        List<Status> statusList = (List<Status>)session.getAttribute("statusList");
        Task task = taskRepository.findById(taskId).get();

        for (int i = 0; i < statusList.size(); i++) {
            if (statusList.get(i).getId() == task.getStatus().getId()) {
                task.setStatus(statusList.get(i-1));
                break;
            }
        }
        taskRepository.save(task);
        return "redirect:/home";
    }

    @PostMapping("/task/moveRight/{taskId}")
    public String moveRight(@PathVariable int taskId, HttpSession session) {
        List<Status> statusList = (List<Status>)session.getAttribute("statusList");
        Task task = taskRepository.findById(taskId).get();

        for (int i = 0; i < statusList.size(); i++) {
            if (statusList.get(i).getId() == task.getStatus().getId()) {
                task.setStatus(statusList.get(i+1));
                break;
            }
        }
        taskRepository.save(task);
        return "redirect:/home";
    }

}