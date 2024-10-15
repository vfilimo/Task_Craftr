package project.demo.service.impl;

import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import project.demo.dto.attachment.AttachmentDto;
import project.demo.dto.attachment.AttachmentSaveDto;
import project.demo.exception.EntityNotFoundException;
import project.demo.mapper.AttachmentMapper;
import project.demo.model.Attachment;
import project.demo.model.Task;
import project.demo.model.User;
import project.demo.repository.AttachmentRepository;
import project.demo.repository.TaskRepository;
import project.demo.service.AttachmentService;
import project.demo.thirdparty.file.sharing.FileSharingService;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final FileSharingService fileSharingService;
    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final AttachmentMapper attachmentMapper;

    @Override
    public AttachmentDto saveAttachment(User assignee, AttachmentSaveDto attachmentSaveDto) {
        Task task = taskRepository.findTaskByIdAndAssigneeId(attachmentSaveDto.taskId(),
                assignee.getId()).orElseThrow(
                    () -> new EntityNotFoundException(String.format(
                        "User with username: %s doesn't have task with id: %d",
                        assignee.getUsername(), attachmentSaveDto.taskId())));
        String dropboxFiletId = fileSharingService.uploadAttachment(attachmentSaveDto.path());
        Attachment attachment = new Attachment();
        attachment.setDropboxFileId(dropboxFiletId);
        attachment.setTask(task);
        attachment.setUploadTime(LocalDateTime.now());
        String filename = Paths.get(attachmentSaveDto.path()).getFileName().toString();
        attachment.setFilename(filename);
        return attachmentMapper.toDto(attachmentRepository.save(attachment));
    }

    @Override
    public List<AttachmentDto> findAttachmentsForTask(User assignee, Long taskId) {
        return null;
    }
}
