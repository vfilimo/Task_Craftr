package project.demo.service.impl;

import java.awt.print.Pageable;
import java.util.List;
import org.springframework.stereotype.Service;
import project.demo.dto.label.LabelSaveDto;
import project.demo.model.Label;
import project.demo.service.LabelService;

@Service
public class LabelServiceImpl implements LabelService {
    @Override
    public Label saveLabel(LabelSaveDto labelSaveDto) {
        return null;
    }

    @Override
    public List<Label> findAllLabels(Pageable pageable) {
        return null;
    }

    @Override
    public Label updateLabel(Long labelId, LabelSaveDto labelSaveDto) {
        return null;
    }

    @Override
    public void deleteLabel(Long labelId) {

    }
}
