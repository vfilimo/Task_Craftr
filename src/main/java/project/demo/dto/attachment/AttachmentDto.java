package project.demo.dto.attachment;

import java.time.LocalDateTime;

public record AttachmentDto(
        Long id,
        String taskName,
        String dropboxFileId,
        String filename,
        LocalDateTime uploadTime
) {
}
