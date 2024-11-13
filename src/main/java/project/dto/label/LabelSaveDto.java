package project.dto.label;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record LabelSaveDto(
        @NotBlank
        @Length(max = 255)
        String name,
        @NotBlank
        @Length(max = 255)
        String color
) {
}
