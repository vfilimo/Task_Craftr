package project.demo.mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import project.demo.config.MapperConfig;
import project.demo.dto.label.LabelSaveDto;
import project.demo.model.Label;

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
