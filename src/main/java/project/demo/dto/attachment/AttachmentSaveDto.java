package project.demo.dto.attachment;

public record AttachmentSaveDto(
        String path,
        String filename,
        Long taskId
) {
}
