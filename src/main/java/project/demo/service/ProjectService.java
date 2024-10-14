package project.demo.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import project.demo.dto.project.RequestCreateProjectDto;
import project.demo.dto.project.ResponseProjectDto;
import project.demo.model.User;

public interface ProjectService {
    ResponseProjectDto createNewProject(RequestCreateProjectDto createProjectDto);

    List<ResponseProjectDto> findUsersProjects(User user, Pageable pageable);

    ResponseProjectDto findProjectDetails(User user, Long projectId);

    ResponseProjectDto updateProject(Long projectId, RequestCreateProjectDto createProjectDto);

    void deleteProject(Long projectId);
}
