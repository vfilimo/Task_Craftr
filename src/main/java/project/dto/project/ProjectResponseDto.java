package project.dto.project;

import java.time.LocalDate;
import project.model.Project;

public record ProjectResponseDto(
        Long id,
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        Project.Status status
) {
}
