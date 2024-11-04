package project.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import project.dto.comment.CommentDto;
import project.dto.comment.CreateCommentDto;
import project.model.User;

public interface CommentService {
    CommentDto createNewComment(User user, CreateCommentDto createCommentDto);

    CommentDto createNewCommentForAssignee(User user, CreateCommentDto createCommentDto);

    List<CommentDto> findCommentsForTask(Long taskId, Pageable pageable);

    List<CommentDto> findCommentsForAssigneeTask(User user, Long taskId, Pageable pageable);
}
