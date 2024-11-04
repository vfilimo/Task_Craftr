package project.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import project.demo.dto.attachment.AttachmentDownloadDto;
import project.demo.dto.attachment.AttachmentDto;
import project.demo.dto.attachment.AttachmentSaveDto;
import project.demo.model.User;
import project.demo.service.AttachmentService;

@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
@Tag(name = "Attachment management.", description = "Endpoints for attachment management.")
public class AttachmentController {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final String DEFAULT_SORT_PARAMETER = "id";
    private final AttachmentService attachmentService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Save new attachment for role user.",
            description = "Save new attachment for task where login user is assignee.")
    public AttachmentDto saveAttachment(@RequestBody @Valid AttachmentSaveDto attachmentSaveDto) {
        User user = getUserFromContext();
        return attachmentService.saveAttachment(user, attachmentSaveDto);
    }

    @PostMapping("/manager")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Save new attachment for role manager.",
            description = "Save new attachment for any task.")
    public AttachmentDto saveAttachmentForManager(
            @RequestBody @Valid AttachmentSaveDto attachmentSaveDto) {
        return attachmentService.saveAttachmentForManager(attachmentSaveDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Get all attachments for role user.",
            description = "Get all attachments for task where login user is assignee.")
    public List<AttachmentDto> getAttachmentsForTask(
            @RequestParam Long taskId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE,
                    page = DEFAULT_PAGE, sort = DEFAULT_SORT_PARAMETER) Pageable pageable) {
        User user = getUserFromContext();
        return attachmentService.findAttachmentsForTask(user, taskId, pageable);
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Get all attachments for role manager.",
            description = "Get all attachments for any task.")
    public List<AttachmentDto> getAttachmentsForTaskForManager(
            @RequestParam Long taskId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE,
                    page = DEFAULT_PAGE, sort = DEFAULT_SORT_PARAMETER) Pageable pageable) {
        return attachmentService.findAttachmentsForTaskForManager(taskId, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Download attachment by id for role user.",
            description = "Download attachment by id for task where login user is assignee.")
    public AttachmentDownloadDto downloadAttachmentById(@PathVariable Long id) {
        User user = getUserFromContext();
        return attachmentService.downloadAttachmentById(user, id);
    }

    @GetMapping("/manager/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Download attachment by id for role manager.",
            description = "Download attachment by id for any task.")
    public AttachmentDownloadDto downloadAttachmentByIdForManager(@PathVariable Long id) {
        return attachmentService.downloadAttachmentByIdForManager(id);
    }

    private User getUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
