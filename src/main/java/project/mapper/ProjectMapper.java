package project.mapper;

import java.util.List;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;
import project.config.MapperConfig;
import project.dto.project.ProjectRequestCreateDto;
import project.dto.project.ProjectResponseDto;
import project.model.Project;

@Mapper(config = MapperConfig.class)
public interface ProjectMapper {
    Project toEntity(ProjectRequestCreateDto createProjectDto);

    ProjectResponseDto toDto(Project project);

    List<ProjectResponseDto> toDto(Page<Project> projectPage);

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProject(@MappingTarget Project project,
                       ProjectRequestCreateDto projectRequestCreateDto);
}
