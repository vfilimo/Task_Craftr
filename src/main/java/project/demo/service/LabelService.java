package project.demo.service;

import java.awt.print.Pageable;
import java.util.List;
import project.demo.dto.label.LabelSaveDto;
import project.demo.model.Label;

public interface LabelService {
    Label saveLabel(LabelSaveDto labelSaveDto);

    List<Label> findAllLabels(Pageable pageable);

    Label updateLabel(Long labelId, LabelSaveDto labelSaveDto);

    void deleteLabel(Long labelId);
}
