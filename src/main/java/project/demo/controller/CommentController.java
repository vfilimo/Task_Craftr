package project.demo.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.demo.dto.comment.CommentDto;
import project.demo.dto.comment.CreateCommentDto;
import project.demo.model.User;
import project.demo.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/manager")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public CommentDto createNewComment(@RequestBody CreateCommentDto createCommentDto) {
        User user = getUserFromContext();
        return commentService.createNewComment(user, createCommentDto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public CommentDto createNewCommentForAssignee(@RequestBody CreateCommentDto createCommentDto) {
        User user = getUserFromContext();
        return commentService.createNewCommentForAssignee(user, createCommentDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<CommentDto> getAllCommentsForTask(@RequestParam Long taskId) {
        User user = getUserFromContext();
        return commentService.findCommentsForAssigneeTask(user, taskId);
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public List<CommentDto> getAllCommentsForAnyTask(@RequestParam Long taskId) {
        return commentService.findCommentsForTask(taskId);
    }

    private User getUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
