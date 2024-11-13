package project.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import project.config.MapperConfig;
import project.dto.project.ProjectRequestCreateDto;
import project.dto.project.ProjectResponseDto;
import project.model.Project;

@Mapper(config = MapperConfig.class)
public interface ProjectMapper {
    Project toEntity(ProjectRequestCreateDto createProjectDto);

    ProjectResponseDto toDto(Project project);

    default Page<ProjectResponseDto> toDto(Page<Project> projectPage) {
        return new PageImpl<>(
                projectPage.stream().map(this::toDto).toList(),
                projectPage.getPageable(),
                projectPage.getTotalElements());

    }

    @Mapping(target = "id", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProject(@MappingTarget Project project,
                       ProjectRequestCreateDto projectRequestCreateDto);
}
