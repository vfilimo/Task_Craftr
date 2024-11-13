package project.dto.project;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import org.hibernate.validator.constraints.Length;
import project.model.Project;
import project.validation.end.after.start.EndDateAfterStartDate;

@EndDateAfterStartDate
public record ProjectRequestCreateDto(
        @NotBlank
        @Length(max = 255)
        String name,
        @Length(max = 255)
        String description,
        LocalDate startDate,
        @FutureOrPresent
        LocalDate endDate,
        @NotNull
        Project.Status status
) {
}
