package project.demo.service;

import project.demo.dto.attachment.AttachmentDto;
import project.demo.dto.attachment.AttachmentSaveDto;

public interface AttachmentService {
    AttachmentDto saveAttachment(AttachmentSaveDto attachmentSaveDto);

    AttachmentDto findAttachmentsForTask(Long taskId);
}
