package project.demo.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.demo.dto.attachment.AttachmentDownloadDto;
import project.demo.dto.attachment.AttachmentDto;
import project.demo.dto.attachment.AttachmentSaveDto;
import project.demo.model.User;
import project.demo.service.AttachmentService;

@RestController
@RequestMapping("/attachments")
@RequiredArgsConstructor
public class AttachmentController {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final String DEFAULT_SORT_PARAMETER = "id";
    private final AttachmentService attachmentService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public AttachmentDto saveAttachment(@RequestBody AttachmentSaveDto attachmentSaveDto) {
        User user = getUserFromContext();
        return attachmentService.saveAttachment(user, attachmentSaveDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<AttachmentDto> getAttachmentsForTask(
            @RequestParam Long taskId,
            @PageableDefault(size = DEFAULT_PAGE_SIZE,
                    page = DEFAULT_PAGE, sort = DEFAULT_SORT_PARAMETER) Pageable pageable) {
        User user = getUserFromContext();
        return attachmentService.findAttachmentsForTask(user, taskId, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public AttachmentDownloadDto downloadAttachmentById(@PathVariable Long id) {
        User user = getUserFromContext();
        return attachmentService.downloadAttachmentById(user, id);
    }

    private User getUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
