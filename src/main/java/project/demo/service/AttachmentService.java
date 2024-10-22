package project.demo.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import project.demo.dto.attachment.AttachmentDownloadDto;
import project.demo.dto.attachment.AttachmentDto;
import project.demo.dto.attachment.AttachmentSaveDto;
import project.demo.model.User;

public interface AttachmentService {
    AttachmentDto saveAttachment(User assignee, AttachmentSaveDto attachmentSaveDto);

    List<AttachmentDto> findAttachmentsForTask(User assignee, Long taskId, Pageable pageable);

    AttachmentDownloadDto downloadAttachmentById(User assignee, Long attachmentId);
}
