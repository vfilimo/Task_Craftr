package project.demo.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.bind.annotation.RestController;
import project.demo.dto.task.TaskCreateDto;
import project.demo.dto.task.TaskDto;
import project.demo.dto.task.TaskUpdateDto;
import project.demo.model.User;
import project.demo.service.TaskService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class TaskController {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final String DEFAULT_SORT_PARAMETER = "id";
    private final TaskService taskService;

    @PostMapping
    public TaskDto createNewTask(TaskCreateDto createTaskDto) {
        return taskService.createNewTask(createTaskDto);
    }

    @GetMapping
    public List<TaskDto> retrieveTasksForProject(@RequestParam Long projectId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE,
            page = DEFAULT_PAGE, sort = DEFAULT_SORT_PARAMETER) Pageable pageable) {
        User user = getUserFromContext();
        return taskService.findTasksForProject(user, projectId, pageable);
    }

    @GetMapping("/{id}")
    public TaskDto retrieveTaskDetails(@PathVariable Long id) {
        User user = getUserFromContext();
        return taskService.findTaskDetails(user, id);
    }

    @PutMapping("/{id}")
    public TaskDto updateTask(@PathVariable Long id, @RequestBody TaskUpdateDto taskUpdateDto) {
        User user = getUserFromContext();
        return taskService.updateTask(user, id, taskUpdateDto);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    private User getUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
//        POST: /api/tasks - Create a new task
//        GET: /api/tasks - Retrieve tasks for a project
//        GET: /api/tasks/{id} - Retrieve task details
//        PUT: /api/tasks/{id} - Update task
//        DELETE: /api/tasks/{id} - Delete task
