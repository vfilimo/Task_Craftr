package project.demo.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import project.demo.dto.task.AssigneeTaskCreateDto;
import project.demo.dto.task.TaskCreateDto;
import project.demo.dto.task.TaskDto;
import project.demo.dto.task.TaskUpdateDto;
import project.demo.model.User;

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
