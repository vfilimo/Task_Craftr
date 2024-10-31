package project.demo.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponseDto createProject(
            @RequestBody @Valid ProjectRequestCreateDto createProjectDto) {
        return projectService.createNewProject(createProjectDto);
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public List<ProjectResponseDto> retrieveManagerProjects(
            @PageableDefault(size = DEFAULT_PAGE_SIZE, page = DEFAULT_PAGE,
            sort = DEFAULT_SORT_PARAMETER) Pageable pageable) {
        return projectService.findAllProject(pageable);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public List<ProjectResponseDto> retrieveUserProjects(
            @PageableDefault(size = DEFAULT_PAGE_SIZE, page = DEFAULT_PAGE,
                    sort = DEFAULT_SORT_PARAMETER) Pageable pageable) {
        User user = getUserFromContext();
        return projectService.findUsersProjects(user, pageable);
    }

    @GetMapping("/{projectId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ProjectResponseDto retrieveProjectDetails(@PathVariable Long projectId) {
        User user = getUserFromContext();
        return projectService.findProjectDetails(user, projectId);
    }

    @GetMapping("/manager/{projectId}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ProjectResponseDto retrieveManagerProjectDetails(@PathVariable Long projectId) {
        return projectService.findAnyProjectDetails(projectId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    public ProjectResponseDto updateProject(
            @PathVariable Long id,
            @RequestBody @Valid ProjectRequestCreateDto createProjectDto) {
        return projectService.updateProject(id, createProjectDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }

    private User getUserFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }
}
