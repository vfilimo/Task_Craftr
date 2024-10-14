package project.demo.mapper;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.data.domain.Page;
import project.demo.config.MapperConfig;
import project.demo.dto.project.ProjectRequestCreateDto;
import project.demo.dto.project.ProjectResponseDto;
import project.demo.model.Project;

@Mapper(config = MapperConfig.class)
public interface ProjectMapper {
    Project toEntity(ProjectRequestCreateDto createProjectDto);

    ProjectResponseDto toDto(Project project);
    List<ProjectResponseDto> toDto(Page<Project> projectPage);

    @Mapping(target = "id", ignore = true)
    void updateProject(@MappingTarget Project project,
                       ProjectRequestCreateDto projectRequestCreateDto);
}
