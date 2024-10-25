package project.demo.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
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
        Comment comment = initializeNewComment(task, user, createCommentDto);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto createNewCommentForAssignee(User user, CreateCommentDto createCommentDto) {
        Task task = taskRepository.findTaskByIdAndAssigneeId(createCommentDto.taskId(),
                user.getId()).orElseThrow(() -> new EntityNotFoundException(String.format(
                "Can't find task with id: %d for user: %s", createCommentDto.taskId(),
                user.getUsername())));
        Comment comment = initializeNewComment(task, user, createCommentDto);
        return commentMapper.toDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> findCommentsForTask(Long taskId, Pageable pageable) {
        return commentMapper.toDto(commentRepository.findCommentByTaskId(taskId, pageable));
    }

    @Override
    public List<CommentDto> findCommentsForAssigneeTask(User user, Long taskId,
                                                        Pageable pageable) {
        return commentMapper.toDto(commentRepository
                .findCommentByTaskIdAndUserId(taskId, user.getId(), pageable));
    }

    private Comment initializeNewComment(Task task, User user, CreateCommentDto createCommentDto) {
        Comment comment = new Comment();
        comment.setTask(task);
        comment.setText(createCommentDto.text());
        comment.setUser(user);
        comment.setTimestamp(LocalDateTime.now());
        return comment;
    }
}
