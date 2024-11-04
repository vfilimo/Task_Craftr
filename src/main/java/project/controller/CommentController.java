package project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import project.dto.comment.CommentDto;
import project.dto.comment.CreateCommentDto;
import project.model.User;
import project.service.CommentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
@Tag(name = "Comment management.", description = "Endpoints for comment management.")
public class CommentController {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final String DEFAULT_SORT_PARAMETER = "id";
    private final CommentService commentService;

    @PostMapping("/manager")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Save new comment for role manager.",
            description = "Save new comment for any task.")
    public CommentDto createNewComment(@RequestBody @Valid CreateCommentDto createCommentDto) {
        User user = getUserFromContext();
        return commentService.createNewComment(user, createCommentDto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Save new comment for role user.",
            description = "Save new comment for task where login user is assignee.")
    public CommentDto createNewCommentForAssignee(
            @RequestBody @Valid CreateCommentDto createCommentDto) {
        User user = getUserFromContext();
        return commentService.createNewCommentForAssignee(user, createCommentDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Find all comments for role user.",
            description = "Find all comments for task where login user is assignee.")
    public List<CommentDto> getAllCommentsForTask(
            @RequestParam Long taskId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, page = DEFAULT_PAGE,
                    sort = DEFAULT_SORT_PARAMETER) Pageable pageable) {
        User user = getUserFromContext();
        return commentService.findCommentsForAssigneeTask(user, taskId, pageable);
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Find all comments for role manager.",
            description = "Find all comments for any task.")
    public List<CommentDto> getAllCommentsForAnyTask(
            @RequestParam Long taskId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE, page = DEFAULT_PAGE,
                    sort = DEFAULT_SORT_PARAMETER) Pageable pageable) {
        return commentService.findCommentsForTask(taskId, pageable);
    }

    private User getUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
