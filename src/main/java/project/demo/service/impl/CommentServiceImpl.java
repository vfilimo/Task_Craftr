package project.demo.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.demo.dto.comment.CommentDto;
import project.demo.dto.comment.CreateCommentDto;
import project.demo.exception.EntityNotFoundException;
import project.demo.mapper.CommentMapper;
import project.demo.model.Comment;
import project.demo.model.Task;
import project.demo.model.User;
import project.demo.repository.CommentRepository;
import project.demo.repository.TaskRepository;
import project.demo.service.CommentService;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto createNewComment(User user, CreateCommentDto createCommentDto) {
        Task task = taskRepository.findById(createCommentDto.taskId()).orElseThrow(
                () -> new EntityNotFoundException(String.format(
                        "Task wit id: %d is not exist", createCommentDto.taskId())));
        Comment comment = new Comment();
        comment.setTask(task);
        comment.setText(createCommentDto.text());
        comment.setUser(user);
        comment.setTimestamp(LocalDateTime.now());
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> findCommentsForTask(Long taskId) {
        List<Comment> commentByTaskId = commentRepository.findCommentByTaskId(taskId);
        return commentMapper.toDto(commentByTaskId);
    }
}
