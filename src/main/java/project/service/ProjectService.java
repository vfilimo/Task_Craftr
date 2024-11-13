package project.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import project.dto.project.ProjectRequestCreateDto;
import project.dto.project.ProjectResponseDto;
import project.model.User;

public interface ProjectService {
    ProjectResponseDto createNewProject(ProjectRequestCreateDto createProjectDto);

    Page<ProjectResponseDto> findUsersProjects(User user, Pageable pageable);

    ProjectResponseDto findProjectDetails(User user, Long projectId);

    ProjectResponseDto findAnyProjectDetails(Long projectId);

    ProjectResponseDto updateProject(Long projectId, ProjectRequestCreateDto createProjectDto);

    void deleteProject(Long projectId);

    Page<ProjectResponseDto> findAllProject(Pageable pageable);
}
