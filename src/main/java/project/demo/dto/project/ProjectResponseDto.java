package project.demo.dto.project;

import java.time.LocalDate;
import project.demo.model.Project;

public record ProjectResponseDto(
        Long id,
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        Project.Status status
) {
}
