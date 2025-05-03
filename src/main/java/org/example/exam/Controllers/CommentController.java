package org.example.exam.Controllers;

import jakarta.servlet.http.HttpSession;
import org.example.exam.entity.Comment;
import org.example.exam.entity.Task;
import org.example.exam.entity.User;
import org.example.exam.repository.CommentRepository;
import org.example.exam.repository.TaskRepository;
import org.example.exam.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class CommentController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public CommentController(TaskRepository taskRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/comments/{taskId}/{userId}")
    public String comments(@PathVariable("taskId") Integer taskId, Model model, @PathVariable("userId") Integer userId) {

        Optional<Task> byId = taskRepository.findById(taskId);
        System.out.println(byId);
        Optional<User> byId1 = userRepository.findById(userId);
        if (byId.isPresent() && byId1.isPresent()) {
            Task task = byId.get();
            User user = byId1.get();
            List<Comment> comments = task.getComments();
            model.addAttribute("task", task);
            model.addAttribute("comments", comments);
            model.addAttribute("user", user);
            model.addAttribute("newComment", new Comment());
            return "comments";
        } else {
            return "home";
        }
    }

    @PostMapping("/task/{taskId}/comments/{userId}/save")
    public String saveComment(@PathVariable Integer taskId,
                              @ModelAttribute Comment newComment,
                              @PathVariable Integer userId) {
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User currentUser = userOptional.get();
            if (taskOptional.isPresent()) {
                Task task = taskOptional.get();
                newComment.setUser(currentUser);
                newComment.setCreatedAt(LocalDateTime.now());
                commentRepository.save(newComment);
                List<Comment> comments = task.getComments();
                comments.add(newComment);
                task.setComments(comments);
                taskRepository.save(task);
            }
            return "redirect:/comments/" + taskId + "/" + userId;
        }
        return "home";
    }


}

