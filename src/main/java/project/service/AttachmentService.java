package project.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import project.dto.attachment.AttachmentDownloadDto;
import project.dto.attachment.AttachmentDto;
import project.dto.attachment.AttachmentSaveDto;
import project.model.User;

public interface AttachmentService {
    AttachmentDto saveAttachment(User assignee, AttachmentSaveDto attachmentSaveDto);

    AttachmentDto saveAttachmentForManager(AttachmentSaveDto attachmentSaveDto);

    List<AttachmentDto> findAttachmentsForTask(User assignee, Long taskId, Pageable pageable);

    List<AttachmentDto> findAttachmentsForTaskForManager(Long taskId, Pageable pageable);

    AttachmentDownloadDto downloadAttachmentById(User assignee, Long attachmentId);

    AttachmentDownloadDto downloadAttachmentByIdForManager(Long attachmentId);
}
