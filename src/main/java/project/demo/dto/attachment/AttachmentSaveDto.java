package project.demo.dto.attachment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AttachmentSaveDto(
        @NotBlank
        String path,
        @NotNull
        @Positive
        Long taskId
) {
}
