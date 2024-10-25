package project.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.demo.dto.attachment.AttachmentDto;
import project.demo.dto.attachment.AttachmentSaveDto;
import project.demo.service.AttachmentService;

@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class AttachmentController {
    private final AttachmentService attachmentService;

    @PostMapping
    public AttachmentDto saveAttachment(AttachmentSaveDto attachmentSaveDto) {
        return attachmentService.saveAttachment(attachmentSaveDto);
    }

    @GetMapping
    public AttachmentDto getAttachmentsForTask(@RequestParam Long taskId) {
        return attachmentService.findAttachmentsForTask(taskId);
    }
}
//      POST: /api/attachments - Upload an attachment to a task (File gets uploaded to Dropbox
//          and we store the Dropbox File ID in our database)
//      GET: /api/attachments?taskId={taskId} - Retrieve attachments for a task
//          (Get the Dropbox File ID from the database and retrieve the actual file from Dropbox)
