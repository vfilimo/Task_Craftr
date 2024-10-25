package project.demo.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.demo.dto.task.TaskCreateDto;
import project.demo.dto.task.TaskDto;
import project.demo.dto.task.TaskUpdateDto;
import project.demo.exception.EntityNotFoundException;
import project.demo.mapper.TaskMapper;
import project.demo.model.Project;
import project.demo.model.Task;
import project.demo.model.User;
import project.demo.repository.ProjectRepository;
import project.demo.repository.TaskRepository;
import project.demo.repository.UserRepository;
import project.demo.service.TaskService;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;

    @Override
    public TaskDto createNewTask(TaskCreateDto createTaskDto) {
        Task task = taskMapper.toEntity(createTaskDto);
        Project project = projectRepository.findById(createTaskDto.projectId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find project with id: "
                        + createTaskDto.projectId()));
        User assignee = userRepository.findUserById(createTaskDto.assigneeId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find user with id: "
                        + createTaskDto.projectId()));
        task.setProject(project);
        task.setAssignee(assignee);
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public List<TaskDto> findTasksForProject(User user, Long projectId, Pageable pageable) {
        Page<Task> taskByProjectIdAndAssigneeId = taskRepository.findTaskByProjectIdAndAssigneeId(
                projectId, user.getId(), pageable);
        return taskMapper.toDto(taskByProjectIdAndAssigneeId);
    }

    @Override
    public TaskDto findTaskDetails(User user, Long taskId) {
        Task task = findTaskByIdAndAssigneeId(user, taskId);
        return taskMapper.toDto(task);
    }

    @Override
    @Transactional
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