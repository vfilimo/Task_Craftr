package project.demo.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateCommentDto(
        @NotNull
        @Positive
        Long taskId,
        @NotBlank
        String text
) {
}
