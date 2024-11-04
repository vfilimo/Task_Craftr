package project.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import project.dto.label.LabelSaveDto;
import project.model.Label;

public interface LabelService {
    Label saveLabel(LabelSaveDto labelSaveDto);

    List<Label> findAllLabels(Pageable pageable);

    Label updateLabel(Long labelId, LabelSaveDto labelSaveDto);

    void deleteLabel(Long labelId);
}
