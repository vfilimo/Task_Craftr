package project.demo.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import project.demo.dto.task.TaskCreateDto;
import project.demo.dto.task.TaskDto;
import project.demo.dto.task.TaskUpdateDto;
import project.demo.exception.EntityNotFoundException;
import project.demo.mapper.TaskMapper;
import project.demo.model.Task;
import project.demo.model.User;
import project.demo.repository.TaskRepository;
import project.demo.service.TaskService;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskDto createNewTask(TaskCreateDto createTaskDto) {
        Task task = taskMapper.toEntity(createTaskDto);
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public List<TaskDto> findTasksForProject(User user, Long projectId, Pageable pageable) {
        return taskMapper.toDto(
                taskRepository.findTaskByProjectIdAndAssigneeId(user.getId(), projectId, pageable));
    }

    @Override
    public TaskDto findTaskDetails(User user, Long taskId) {
        Task task = findTaskByIdAndAssigneeId(user, taskId);
        return taskMapper.toDto(task);
    }

    @Override
    public TaskDto updateTask(User user, Long taskId, TaskUpdateDto taskUpdateDto) {
        Task task = findTaskByIdAndAssigneeId(user, taskId);
        taskMapper.updateTask(task, taskUpdateDto);
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    private Task findTaskByIdAndAssigneeId(User user, Long taskId) {
        return taskRepository.findTaskByIdAndAssigneeId(taskId, user.getId()).orElseThrow(
                () -> new EntityNotFoundException(String.format(
                        "User with username: %s doesn't have task with id: %d",
                        user.getUsername(), taskId)));
    }
}
