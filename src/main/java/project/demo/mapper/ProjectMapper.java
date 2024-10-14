package project.demo.mapper;

import org.mapstruct.Mapper;
import project.demo.config.MapperConfig;
import project.demo.dto.project.RequestCreateProjectDto;
import project.demo.dto.project.ResponseProjectDto;
import project.demo.model.Project;

@Mapper(config = MapperConfig.class)
public interface ProjectMapper {
    Project toEntity(RequestCreateProjectDto createProjectDto);

    ResponseProjectDto toDto(Project project);
}
