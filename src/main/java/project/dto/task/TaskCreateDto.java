package project.dto.task;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;
import org.hibernate.validator.constraints.Length;
import project.model.Task;

public record TaskCreateDto(
        @NotBlank
        @Length(max = 255)
        String name,
        @NotBlank
        @Length(max = 255)
        String description,
        @NotNull
        Task.Priority priority,
        @NotNull
        Task.Status status,
        @NotNull
        @FutureOrPresent
        LocalDate dueDate,
        @NotNull
        @Positive
        Long projectId,
        @NotNull
        @Positive
        Long assigneeId
) {
}
