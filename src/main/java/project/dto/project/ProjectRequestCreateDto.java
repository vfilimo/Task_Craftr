package project.dto.project;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import project.model.Project;
import project.validation.end.after.start.EndDateAfterStartDate;

@EndDateAfterStartDate
public record ProjectRequestCreateDto(
        @NotBlank
        String name,
        String description,
        LocalDate startDate,
        @FutureOrPresent
        LocalDate endDate,
        @NotNull
        Project.Status status
) {
}