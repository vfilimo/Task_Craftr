package project.service.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import project.dto.attachment.AttachmentDownloadDto;
import project.dto.attachment.AttachmentDto;
import project.dto.attachment.AttachmentSaveDto;
import project.exception.EntityNotFoundException;
import project.external.file.sharing.FileSharingService;
import project.mapper.AttachmentMapper;
import project.model.Attachment;
import project.model.Task;
import project.model.User;
import project.repository.AttachmentRepository;
import project.repository.TaskRepository;
import project.service.AttachmentService;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService {
    private final FileSharingService fileSharingService;
    private final AttachmentRepository attachmentRepository;
    private final TaskRepository taskRepository;
    private final AttachmentMapper attachmentMapper;

    @Override
    public AttachmentDto saveAttachment(User user, AttachmentSaveDto attachmentSaveDto) {
        Task task = taskRepository.findTaskByIdAndAssigneeId(attachmentSaveDto.taskId(),
                user.getId()).orElseThrow(
                    () -> new EntityNotFoundException(String.format(
                        "User with username: %s doesn't have task with id: %d",
                            user.getUsername(), attachmentSaveDto.taskId())));
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
    public AttachmentDto saveAttachmentForManager(AttachmentSaveDto attachmentSaveDto) {
        Task task = taskRepository.findTaskById(attachmentSaveDto.taskId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find task with id: "
                        + attachmentSaveDto.taskId()));
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
    public List<AttachmentDto> findAttachmentsForTask(User user, Long taskId,
                                                      Pageable pageable) {
        Page<Attachment> attachments = attachmentRepository
                .findAllByTaskIdAndAssigneeId(taskId, user.getId(), pageable);
        return attachmentMapper.toDto(attachments);
    }

    @Override
    public List<AttachmentDto> findAttachmentsForTaskForManager(Long taskId, Pageable pageable) {
        Page<Attachment> attachments = attachmentRepository
                .findAllByTaskId(taskId, pageable);
        return attachmentMapper.toDto(attachments);
    }

    @Override
    public AttachmentDownloadDto downloadAttachmentById(User user, Long attachmentId) {
        Attachment attachment = attachmentRepository.findAttachmentByIdAndAssigneeId(attachmentId,
                        user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Can't find attachment with id: %d for user: %s",
                                attachmentId, user.getUsername())));
        Path filePath = fileSharingService.downloadFile(attachment.getDropboxFileId());
        boolean isExists = Files.exists(filePath);
        return new AttachmentDownloadDto(isExists ? "DOWNLOADED" : "NOT EXISTS",
                isExists ? filePath.toString() : "NOT EXISTS");
    }

    @Override
    public AttachmentDownloadDto downloadAttachmentByIdForManager(Long attachmentId) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Can't find attachment with id: %d", attachmentId)));
        Path filePath = fileSharingService.downloadFile(attachment.getDropboxFileId());
        boolean isExists = Files.exists(filePath);
        return new AttachmentDownloadDto(isExists ? "DOWNLOADED" : "NOT EXISTS",
                isExists ? filePath.toString() : "NOT EXISTS");
    }
}
