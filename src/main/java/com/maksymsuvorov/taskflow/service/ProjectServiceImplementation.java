package com.maksymsuvorov.taskflow.service;

import com.maksymsuvorov.taskflow.controller.dto.ProjectCreateRequest;
import com.maksymsuvorov.taskflow.controller.dto.ProjectUpdateRequest;
import com.maksymsuvorov.taskflow.model.Project;
import com.maksymsuvorov.taskflow.repository.ProjectRepository;
import com.maksymsuvorov.taskflow.repository.TaskRepository;
import com.maksymsuvorov.taskflow.security.AuthorizationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectServiceImplementation implements ProjectServiceInterface {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final AuthorizationService authorizationService;

    @Override
    @Transactional(readOnly = true)
    public List<Project> getAllProjects() {
        return this.projectRepository.findAll();
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
