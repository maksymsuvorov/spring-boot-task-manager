package com.maksymsuvorov.taskflow.controller;

import com.maksymsuvorov.taskflow.controller.dto.PageResponse;
import com.maksymsuvorov.taskflow.controller.dto.ProjectCreateRequest;
import com.maksymsuvorov.taskflow.controller.dto.ProjectResponse;
import com.maksymsuvorov.taskflow.controller.dto.ProjectUpdateRequest;
import com.maksymsuvorov.taskflow.controller.dto.filter.ProjectFilter;
import com.maksymsuvorov.taskflow.mapper.ProjectMapper;
import com.maksymsuvorov.taskflow.model.Project;
import com.maksymsuvorov.taskflow.service.ProjectServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectServiceInterface projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectCreateRequest request) {
        Project project = this.projectService.createProject(request);

        return ResponseEntity
                .created(URI.create("/api/projects/" + project.getId()))
                .body(ProjectMapper.toResponse(project));
    }

    @GetMapping
    public PageResponse<ProjectResponse> getProjects(@ModelAttribute ProjectFilter filter,
                                                     @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return PageResponse.from(
                this.projectService.getProjects(filter, pageable)
                        .map(ProjectMapper::toResponse)
        );
    }

    @GetMapping("/{id}")
    public ProjectResponse getProjectById(@PathVariable Long id) {
        return ProjectMapper.toResponse(this.projectService.getProjectById(id));
    }

    @PutMapping("/{id}")
    public ProjectResponse updateProjectById(@PathVariable Long id,
                                             @Valid @RequestBody ProjectUpdateRequest request) {
        return ProjectMapper.toResponse(this.projectService.updateProjectById(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProjectById(@PathVariable Long id) {
        this.projectService.deleteProjectById(id);
        return ResponseEntity.noContent().build();
    }

}
