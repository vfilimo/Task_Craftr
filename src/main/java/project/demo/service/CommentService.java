package project.demo.service;

import java.util.List;
import project.demo.dto.comment.CommentDto;
import project.demo.dto.comment.CreateCommentDto;
import project.demo.model.User;

public interface CommentService {
    CommentDto createNewComment(User user, CreateCommentDto createCommentDto);

    List<CommentDto> findCommentsForTask(Long taskId);
}
