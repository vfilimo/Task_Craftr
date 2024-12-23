package project.dto.task;

import java.time.LocalDate;
import java.util.List;
import project.model.Task;

public record TaskUpdateDto(
        String name,
        String description,
        Task.Priority priority,
        Task.Status status,
        LocalDate dueDate,
        Long assigneeId,
        List<Long> labelsIds
) {
}
