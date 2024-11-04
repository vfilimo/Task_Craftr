package project.dto.task;

import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import project.model.Task;

@Getter
@Setter
public class TaskDto {
    private Long id;
    private String name;
    private String description;
    private Task.Priority priority;
    private Task.Status status;
    private LocalDate dueDate;
    private String projectName;
    private String assigneeUsername;
    private List<Long> labelsId;
}
