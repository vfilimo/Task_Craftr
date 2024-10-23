package project.demo.dto.project;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import project.demo.model.Project;

public record ProjectRequestCreateDto(
        @NotBlank
        String name,
        @NotBlank
        String description,
        @FutureOrPresent
        LocalDate startDate,
        @FutureOrPresent
        LocalDate endDate,
        @NotNull
        Project.Status status
) {
}
