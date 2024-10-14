package project.demo.service.impl;

import java.util.List;
import org.springframework.stereotype.Service;
import project.demo.dto.comment.CommentDto;
import project.demo.dto.comment.CreateCommentDto;
import project.demo.model.User;
import project.demo.service.CommentService;

@Service
public class CommentServiceImpl implements CommentService {
    @Override
    public CommentDto createNewComment(User user, CreateCommentDto createCommentDto) {
        return null;
    }

    @Override
    public List<CommentDto> findCommentsForTask(Long taskId) {
        return null;
    }
}
