package project.demo.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
import project.demo.dto.label.LabelSaveDto;
import project.demo.model.Label;
import project.demo.service.LabelService;

@RestController
@RequestMapping("/labels")
@RequiredArgsConstructor
public class LabelController {
    private final LabelService labelService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    public Label createNewLabel(@RequestBody @Valid LabelSaveDto labelSaveDto) {
        return labelService.saveLabel(labelSaveDto);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_MANAGER')")
    public List<Label> getAllLabels(Pageable pageable) {
        return labelService.findAllLabels(pageable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER') OR hasRole('ROLE_MANAGER')")
    public Label updateLabel(@PathVariable Long id,
                             @RequestBody @Valid LabelSaveDto labelSaveDto) {
        return labelService.updateLabel(id, labelSaveDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public void deleteLabel(@PathVariable Long id) {
        labelService.deleteLabel(id);
    }
}
