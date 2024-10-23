package project.demo.dto.label;

import jakarta.validation.constraints.NotBlank;

public record LabelSaveDto(
        @NotBlank
        String name,
        @NotBlank
        String color
) {
}
