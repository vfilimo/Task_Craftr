package project.service.impl;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import project.dto.comment.CommentDto;
import project.dto.comment.CreateCommentDto;
import project.exception.EntityNotFoundException;
import project.mapper.CommentMapper;
import project.model.Comment;
import project.model.Task;
import project.model.User;
import project.repository.CommentRepository;
import project.repository.TaskRepository;
import project.service.CommentService;

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
    public Page<CommentDto> findCommentsForTask(Long taskId, Pageable pageable) {
        return commentMapper.toDto(commentRepository.findCommentByTaskId(taskId, pageable));
    }

    @Override
    public Page<CommentDto> findCommentsForAssigneeTask(User user, Long taskId,
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
