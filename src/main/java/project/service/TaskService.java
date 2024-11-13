package project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project.dto.task.AssigneeTaskCreateDto;
import project.dto.task.TaskCreateDto;
import project.dto.task.TaskDto;
import project.dto.task.TaskUpdateDto;
import project.model.User;

public interface TaskService {
    TaskDto createNewTask(TaskCreateDto createTaskDto);

    Page<TaskDto> findUserTasksForProject(User user, Long projectId, Pageable pageable);

    Page<TaskDto> findAllTasksInUserProject(User user, Long projectId, Pageable pageable);

    TaskDto findTaskDetails(User user, Long taskId);

    TaskDto updateTask(User user, Long taskId, TaskUpdateDto taskUpdateDto);

    void deleteTask(Long taskId);

    TaskDto createNewTaskForAssignee(User user, AssigneeTaskCreateDto assigneeTaskCreateDto);

    Page<TaskDto> findAllTasksForProject(Long projectId, Pageable pageable);

    TaskDto findAnyTaskDetails(Long taskId);

    TaskDto updateTaskForManager(Long taskId, TaskUpdateDto taskUpdateDto);
}
