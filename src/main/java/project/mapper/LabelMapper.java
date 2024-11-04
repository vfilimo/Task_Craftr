package project.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import project.config.MapperConfig;
import project.dto.label.LabelSaveDto;
import project.model.Label;

@Mapper(config = MapperConfig.class)
public interface LabelMapper {
    Label toEntity(LabelSaveDto labelSaveDto);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateLabel(@MappingTarget Label label, LabelSaveDto labelSaveDto);

    @Named("toLabelsList")
    default Set<Label> toLabelsList(List<Long> labelsId) {
        return labelsId.stream()
                .map(Label::new)
                .collect(Collectors.toSet());
    }
}
