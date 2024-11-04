package project.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import project.dto.label.LabelSaveDto;
import project.exception.EntityNotFoundException;
import project.mapper.LabelMapper;
import project.model.Label;
import project.repository.LabelRepository;
import project.service.LabelService;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Override
    public Label saveLabel(LabelSaveDto labelSaveDto) {
        Label label = labelMapper.toEntity(labelSaveDto);
        return labelRepository.save(label);
    }

    @Override
    public List<Label> findAllLabels(Pageable pageable) {
        return labelRepository.findAll(pageable).stream()
                .toList();
    }

    @Override
    public Label updateLabel(Long labelId, LabelSaveDto labelSaveDto) {
        Label label = labelRepository.findById(labelId).orElseThrow(
                () -> new EntityNotFoundException("Can't find label with id: " + labelId));
        labelMapper.updateLabel(label, labelSaveDto);
        return labelRepository.save(label);
    }

    @Override
    public void deleteLabel(Long labelId) {
        labelRepository.deleteById(labelId);
    }
}
