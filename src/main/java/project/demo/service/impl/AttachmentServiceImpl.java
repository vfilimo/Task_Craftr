package project.demo.service.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import project.demo.dto.attachment.AttachmentDownloadDto;
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
