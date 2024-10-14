package project.demo.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import project.demo.dto.project.RequestCreateProjectDto;
import project.demo.dto.project.ResponseProjectDto;
import project.demo.mapper.ProjectMapper;
import project.demo.model.Project;
import project.demo.model.User;
import project.demo.repository.ProjectRepository;
import project.demo.service.ProjectService;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    @Override
    public ResponseProjectDto createNewProject(RequestCreateProjectDto createProjectDto) {
        Project project = projectMapper.toEntity(createProjectDto);
        return projectMapper.toDto(projectRepository.save(project));
    }

    @Override
    public List<ResponseProjectDto> findUsersProjects(User user, Pageable pageable) {
        return null;
    }

    @Override
    public ResponseProjectDto findProjectDetails(User user, Long projectId) {
        return null;
    }

    @Override
    public ResponseProjectDto updateProject(Long projectId,
                                            RequestCreateProjectDto createProjectDto) {
        return null;
    }

    @Override
    public void deleteProject(Long projectId) {

    }
}
