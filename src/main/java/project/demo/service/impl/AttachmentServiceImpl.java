package project.demo.service.impl;

import org.springframework.stereotype.Service;
import project.demo.dto.attachment.AttachmentDto;
import project.demo.dto.attachment.AttachmentSaveDto;
import project.demo.service.AttachmentService;

@Service
public class AttachmentServiceImpl implements AttachmentService {
    @Override
    public AttachmentDto saveAttachment(AttachmentSaveDto attachmentSaveDto) {
        return null;
    }

    @Override
    public AttachmentDto findAttachmentsForTask(Long taskId) {
        return null;
    }
}
