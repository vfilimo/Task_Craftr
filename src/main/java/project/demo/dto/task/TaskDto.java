package project.demo.dto.task;

import java.time.LocalDate;
import java.util.List;
import project.demo.model.Task;

public record TaskDto(
        Long id,
        String name,
        String description,
        Task.Priority priority,
        Task.Status status,
        LocalDate dueDate,
        String projectName,
        String assigneeUsername,
        List<Long> labelsId
) {
}
