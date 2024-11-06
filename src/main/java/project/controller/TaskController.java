package project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import project.dto.task.AssigneeTaskCreateDto;
import project.dto.task.TaskCreateDto;
import project.dto.task.TaskDto;
import project.dto.task.TaskUpdateDto;
import project.model.User;
import project.service.TaskService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
@Tag(name = "Task management.", description = "Endpoints for task management.")
public class TaskController {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final String DEFAULT_SORT_PARAMETER = "id";
    private final TaskService taskService;

    @PostMapping("/manager")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new task for role manager.",
            description = "Create new task.")
    public TaskDto createNewTaskForManager(@RequestBody @Valid TaskCreateDto createTaskDto) {
        return taskService.createNewTask(createTaskDto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new task for role user.",
            description = "Create new task where assignee is login user.")
    public TaskDto createNewTaskForAssignee(
            @RequestBody @Valid AssigneeTaskCreateDto assigneeTaskCreateDto) {
        User user = getUserFromContext();
        return taskService.createNewTaskForAssignee(user, assigneeTaskCreateDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Find all tasks by project id for role user.",
            description = "Find all tasks by project id where assignee is login user.")
    public List<TaskDto> retrieveTasksForProject(
            @RequestParam Long projectId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE,
                    page = DEFAULT_PAGE, sort = DEFAULT_SORT_PARAMETER) Pageable pageable) {
        User user = getUserFromContext();
        return taskService.findTasksForProject(user, projectId, pageable);
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Find all tasks for role manager.",
            description = "Find all tasks by project id.")
    public List<TaskDto> retrieveAllTasksForProject(
            @RequestParam Long projectId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE,
                    page = DEFAULT_PAGE, sort = DEFAULT_SORT_PARAMETER) Pageable pageable) {
        return taskService.findAllTasksForProject(projectId, pageable);
    }

    @GetMapping("/{taskId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Find task for role user.",
            description = "Find task by id where login user is assignee.")
    public TaskDto retrieveTaskDetails(@PathVariable Long taskId) {
        User user = getUserFromContext();
        return taskService.findTaskDetails(user, taskId);
    }

    @GetMapping("/manager/{taskId}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Find task for role manager.",
            description = "Find any task by id.")
    public TaskDto retrieveAnyTaskDetails(@PathVariable Long taskId) {
        return taskService.findAnyTaskDetails(taskId);
    }

    @PutMapping("/{taskId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Update task for role manager.",
            description = "Update task where login user is assignee by id.")
    public TaskDto updateTask(@PathVariable Long taskId, @RequestBody TaskUpdateDto taskUpdateDto) {
        User user = getUserFromContext();
        return taskService.updateTask(user, taskId, taskUpdateDto);
    }

    @PutMapping("/manager/{taskId}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Update task for role manager.",
            description = "Update any task by id.")
    public TaskDto updateTaskForManager(@PathVariable Long taskId,
                                        @RequestBody TaskUpdateDto taskUpdateDto) {
        return taskService.updateTaskForManager(taskId, taskUpdateDto);
    }

    @DeleteMapping("/manager/{taskId}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete task for role manager.",
            description = "Delete any task by id.")
    public void deleteTask(@PathVariable Long taskId) {
        taskService.deleteTask(taskId);
    }

    private User getUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
