package com.maksymsuvorov.taskflow.service;

import com.maksymsuvorov.taskflow.controller.dto.ProjectCreateRequest;
import com.maksymsuvorov.taskflow.controller.dto.filter.ProjectFilter;
import com.maksymsuvorov.taskflow.controller.dto.ProjectUpdateRequest;
import com.maksymsuvorov.taskflow.model.Project;
import com.maksymsuvorov.taskflow.repository.ProjectRepository;
import com.maksymsuvorov.taskflow.repository.TaskRepository;
import com.maksymsuvorov.taskflow.repository.specification.ProjectSpecification;
import com.maksymsuvorov.taskflow.security.AuthorizationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImplementation implements ProjectServiceInterface {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final AuthorizationService authorizationService;

    private static final Set<String> ALLOWED_SORT_PROPERTIES =
            Set.of("name", "createdAt", "updatedAt");

    @Override
    @Transactional(readOnly = true)
    public Page<Project> getProjects(ProjectFilter filter, Pageable pageable) {
        SortValidator.validate(pageable.getSort(), ALLOWED_SORT_PROPERTIES);

        Specification<Project> specification = Specification.allOf(
                this.authorizationService.isAdmin()
                        ? Specification.unrestricted()
                        : ProjectSpecification.visibleTo(this.authorizationService.currentEmail()),
                ProjectSpecification.nameContains(filter.name()),
                ProjectSpecification.hasOwner(filter.ownerId())
        );

        return this.projectRepository.findAll(specification, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Project getProjectById(Long projectId) throws EntityNotFoundException {
        return this.projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project with id " + projectId + " was not found."));
    }

    @Override
    public Project createProject(ProjectCreateRequest request) {
        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setOwner(this.authorizationService.requireCurrentUser());

        return this.projectRepository.save(project);
    }

    @Override
    public Project updateProjectById(Long projectId, ProjectUpdateRequest request) throws EntityNotFoundException {
        Project project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project with id " + projectId + " was not found."));

        this.authorizationService.requireProjectOwnerOrAdmin(project);

        project.setName(request.name());
        project.setDescription(request.description());

        return project;
    }

    @Override
    public void deleteProjectById(Long projectId) throws EntityNotFoundException, IllegalStateException {
        Project project = this.projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project with id " + projectId + " was not found."));

        this.authorizationService.requireProjectOwnerOrAdmin(project);

        if (this.taskRepository.existsByProjectId(projectId)) {
            throw new IllegalStateException(
                    "Project cannot be deleted because one or more tasks belong to it."
            );
        }

        this.projectRepository.delete(project);
    }

}
