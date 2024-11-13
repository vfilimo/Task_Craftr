package project.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import project.dto.label.LabelSaveDto;
import project.model.Label;
import project.service.LabelService;

@RestController
@RequestMapping("/labels")
@RequiredArgsConstructor
@Tag(name = "Label management.", description = "Endpoints for label management.")
public class LabelController {
    private final LabelService labelService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Save new label.",
            description = "Save new label. Available for roles user and manager")
    public Label createNewLabel(@RequestBody @Valid LabelSaveDto labelSaveDto) {
        return labelService.saveLabel(labelSaveDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_MANAGER')")
    @Operation(summary = "Find all labels.",
            description = "Find all labels. Available for roles user and manager")
    public Page<Label> getAllLabels(Pageable pageable) {
        return labelService.findAllLabels(pageable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_MANAGER')")
    @Operation(summary = "Update label.",
            description = "Update label. Available for roles user and manager")
    public Label updateLabel(@PathVariable Long id,
                             @RequestBody @Valid LabelSaveDto labelSaveDto) {
        return labelService.updateLabel(id, labelSaveDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @Operation(summary = "Delete label.",
            description = "Delete label. Available for role manager")
    public void deleteLabel(@PathVariable Long id) {
        labelService.deleteLabel(id);
    }
}
