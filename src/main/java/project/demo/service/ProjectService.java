package project.demo.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import project.demo.dto.project.ProjectRequestCreateDto;
import project.demo.dto.project.ProjectResponseDto;
import project.demo.model.User;

public interface ProjectService {
    ProjectResponseDto createNewProject(ProjectRequestCreateDto createProjectDto);

    List<ProjectResponseDto> findUsersProjects(User user, Pageable pageable);

    ProjectResponseDto findProjectDetails(User user, Long projectId);
    ProjectResponseDto findAnyProjectDetails(Long projectId);

    ProjectResponseDto updateProject(Long projectId, ProjectRequestCreateDto createProjectDto);

    void deleteProject(Long projectId);

    List<ProjectResponseDto> findAllProject(Pageable pageable);
}
