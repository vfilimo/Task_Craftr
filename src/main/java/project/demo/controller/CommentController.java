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

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public CommentDto createNewComment(@RequestBody CreateCommentDto createCommentDto) {
        User user = getUserFromContext();
        return commentService.createNewComment(user, createCommentDto);
    }

    @GetMapping
    public List<CommentDto> getAllCommentsForTask(@RequestParam Long taskId) {
        return commentService.findCommentsForTask(taskId);
    }

    private User getUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
//            POST: /api/comments - Add a comment to a task
//            GET: /api/comments?taskId={taskId} - Retrieve comments for a task
