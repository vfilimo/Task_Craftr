package project.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record CreateCommentDto(
        @NotNull
        @Positive
        Long taskId,
        @NotBlank
        @Length(max = 1000)
        String text
) {
}
