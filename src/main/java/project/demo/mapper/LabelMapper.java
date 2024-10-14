package project.demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import project.demo.config.MapperConfig;
import project.demo.dto.label.LabelSaveDto;
import project.demo.model.Label;

@Mapper(config = MapperConfig.class)
public interface LabelMapper {
    Label toEntity(LabelSaveDto labelSaveDto);

    @Mapping(target = "id", ignore = true)
    void updateLabel(@MappingTarget Label label, LabelSaveDto labelSaveDto);
}
