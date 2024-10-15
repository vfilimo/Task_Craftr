package project.demo.dto.attachment;

public record AttachmentSaveDto(
        String path,
        Long taskId
) {
}
