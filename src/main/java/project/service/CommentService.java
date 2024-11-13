package project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project.dto.comment.CommentDto;
import project.dto.comment.CreateCommentDto;
import project.model.User;

public interface CommentService {
    CommentDto createNewComment(User user, CreateCommentDto createCommentDto);

    CommentDto createNewCommentForAssignee(User user, CreateCommentDto createCommentDto);

    Page<CommentDto> findCommentsForTask(Long taskId, Pageable pageable);

    Page<CommentDto> findCommentsForAssigneeTask(User user, Long taskId, Pageable pageable);
}
