package project.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import project.dto.task.AssigneeTaskCreateDto;
import project.dto.task.TaskCreateDto;
import project.dto.task.TaskDto;
import project.dto.task.TaskUpdateDto;
import project.model.User;

public interface TaskService {
    TaskDto createNewTask(TaskCreateDto createTaskDto);

    List<TaskDto> findTasksForProject(User user, Long projectId, Pageable pageable);

    TaskDto findTaskDetails(User user, Long taskId);

    TaskDto updateTask(User user, Long taskId, TaskUpdateDto taskUpdateDto);

    void deleteTask(Long taskId);

    TaskDto createNewTaskForAssignee(User user, AssigneeTaskCreateDto assigneeTaskCreateDto);

    List<TaskDto> findAllTasksForProject(Long projectId, Pageable pageable);

    TaskDto findAnyTaskDetails(Long taskId);

    TaskDto updateTaskForManager(Long taskId, TaskUpdateDto taskUpdateDto);
}
