package project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project.dto.label.LabelSaveDto;
import project.model.Label;

public interface LabelService {
    Label saveLabel(LabelSaveDto labelSaveDto);

    Page<Label> findAllLabels(Pageable pageable);

    Label updateLabel(Long labelId, LabelSaveDto labelSaveDto);

    void deleteLabel(Long labelId);
}
