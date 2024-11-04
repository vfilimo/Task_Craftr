package project.service.impl;

import com.google.api.services.calendar.model.Event;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.dto.task.AssigneeTaskCreateDto;
import project.dto.task.TaskCreateDto;
import project.dto.task.TaskDto;
import project.dto.task.TaskUpdateDto;
import project.exception.EntityNotFoundException;
import project.exception.TaskDueDateException;
import project.external.google.GoogleCalendarService;
import project.mapper.TaskMapper;
import project.model.Project;
import project.model.Task;
import project.model.User;
import project.repository.ProjectRepository;
import project.repository.TaskRepository;
import project.repository.UserRepository;
import project.service.TaskService;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final GoogleCalendarService googleCalendarService;

    @Override
    public TaskDto createNewTask(TaskCreateDto createTaskDto) {
        Task task = taskMapper.toEntity(createTaskDto);
        Project project = projectRepository.findById(createTaskDto.projectId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find project with id: "
                        + createTaskDto.projectId()));
        dateCheck(project, task);
        User assignee = findUserInDb(createTaskDto.assigneeId());
        task.setProject(project);
        task.setAssignee(assignee);
        Event event = googleCalendarService.createEvent(task);

        Event insertedEvent = googleCalendarService.insertEventInToCalendar(event);
        task.setEventId(insertedEvent.getId());
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public TaskDto createNewTaskForAssignee(
            User user, AssigneeTaskCreateDto assigneeTaskCreateDto) {
        Project project = projectRepository.findProjectByUserIdAndProjectId(
                        user.getId(), assigneeTaskCreateDto.projectId())
                .orElseThrow(() -> new EntityNotFoundException(String.format(
                        "Can't find project with id: %d for user: %s",
                        assigneeTaskCreateDto.projectId(), user.getUsername())));
        Task task = taskMapper.toEntity(assigneeTaskCreateDto);
        dateCheck(project, task);
        task.setAssignee(user);
        task.setProject(project);

        Event event = googleCalendarService.createEvent(task);
        Event insertedEvent = googleCalendarService.insertEventInToCalendar(event);
        task.setEventId(insertedEvent.getId());

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
        dateCheck(task.getProject(), task);

        Event event = googleCalendarService.createEvent(task);
        googleCalendarService.updateEvent(event, task.getEventId());

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    @Transactional
    public TaskDto updateTaskForManager(Long taskId, TaskUpdateDto taskUpdateDto) {
        Task task = findTaskById(taskId);
        taskMapper.updateTask(task, taskUpdateDto);
        dateCheck(task.getProject(), task);
        if (taskUpdateDto.assigneeId() != null
                && !task.getAssignee().getId().equals(taskUpdateDto.assigneeId())) {
            User newUser = findUserInDb(taskUpdateDto.assigneeId());
            task.setAssignee(newUser);
        }

        Event event = googleCalendarService.createEvent(task);
        googleCalendarService.updateEvent(event, task.getEventId());

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    public void deleteTask(Long taskId) {
        taskRepository.deleteById(taskId);
    }

    @Override
    public List<TaskDto> findAllTasksForProject(Long projectId, Pageable pageable) {
        return taskMapper.toDto(taskRepository.findByProjectId(projectId, pageable));
    }

    @Override
    public TaskDto findAnyTaskDetails(Long taskId) {
        Task task = findTaskById(taskId);
        return taskMapper.toDto(task);
    }

    private Task findTaskByIdAndAssigneeId(User user, Long taskId) {
        return taskRepository.findTaskByIdAndAssigneeId(taskId, user.getId()).orElseThrow(
                () -> new EntityNotFoundException(String.format(
                        "User with username: %s doesn't have task with id: %d",
                        user.getUsername(), taskId)));
    }

    private Task findTaskById(Long taskId) {
        return taskRepository.findTaskById(taskId).orElseThrow(() -> new EntityNotFoundException(
                "Can't find task with id: " + taskId));
    }

    private User findUserInDb(Long id) {
        return userRepository.findUserById(id).orElseThrow(
                () -> new EntityNotFoundException("Can't find user with id: " + id));
    }

    private void dateCheck(Project project, Task task) {
        if (task.getDueDate().isAfter(project.getEndDate())) {
            throw new TaskDueDateException("The due date must be before project end date");
        } else if (task.getDueDate().isBefore(project.getStartDate())) {
            throw new TaskDueDateException("The due date must be after project start date");
        }
    }
}
