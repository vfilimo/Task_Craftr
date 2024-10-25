package project.demo.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import project.demo.dto.comment.CommentDto;
import project.demo.dto.comment.CreateCommentDto;
import project.demo.model.User;

public interface CommentService {
    CommentDto createNewComment(User user, CreateCommentDto createCommentDto);

    CommentDto createNewCommentForAssignee(User user, CreateCommentDto createCommentDto);

    List<CommentDto> findCommentsForTask(Long taskId, Pageable pageable);

    List<CommentDto> findCommentsForAssigneeTask(User user, Long taskId, Pageable pageable);
}
