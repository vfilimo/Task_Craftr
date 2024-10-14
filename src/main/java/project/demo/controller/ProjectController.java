package project.demo.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import project.demo.dto.project.ProjectRequestCreateDto;
import project.demo.dto.project.ProjectResponseDto;
import project.demo.model.User;
import project.demo.service.ProjectService;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
public class ProjectController {
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 5;
    private static final String DEFAULT_SORT_PARAMETER = "id";
    private final ProjectService projectService;

    @PostMapping
    public ProjectResponseDto createProject(
            @RequestBody ProjectRequestCreateDto createProjectDto) {
        return projectService.createNewProject(createProjectDto);
    }

    //has two function 1. For Manager(available all projects), 2. For User
    @GetMapping
    public List<ProjectResponseDto> retrieveUsersProjects(
            @PageableDefault(size = DEFAULT_PAGE_SIZE, page = DEFAULT_PAGE,
                    sort = DEFAULT_SORT_PARAMETER) Pageable pageable) {
        User user = getUserFromContext();
        return projectService.findUsersProjects(user, pageable);
    }

    @GetMapping("/{id}")
    public ProjectResponseDto retrieveProjectDetails(@PathVariable Long id) {
        User user = getUserFromContext();
        return projectService.findProjectDetails(user, id);
    }

    // available only for manager
    @PutMapping("/{id}")
    public ProjectResponseDto updateProject(@PathVariable Long id,
                                            ProjectRequestCreateDto createProjectDto) {
        return projectService.updateProject(id, createProjectDto);

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }

    private User getUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
//        POST: /api/projects - Create a new project
//        GET: /api/projects - Retrieve user's projects
//        GET: /api/projects/{id} - Retrieve project details
//        PUT: /api/projects/{id} - Update project
//        DELETE: /api/projects/{id} - Delete project
