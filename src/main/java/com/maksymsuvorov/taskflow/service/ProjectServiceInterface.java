package com.maksymsuvorov.taskflow.service;

import com.maksymsuvorov.taskflow.controller.dto.ProjectCreateRequest;
import com.maksymsuvorov.taskflow.controller.dto.ProjectUpdateRequest;
import com.maksymsuvorov.taskflow.model.Project;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

public interface ProjectServiceInterface {
    List<Project> getAllProjects();

    Project getProjectById(Long projectId) throws EntityNotFoundException;

    Project createProject(ProjectCreateRequest request) throws EntityNotFoundException;

    Project updateProjectById(Long projectId, ProjectUpdateRequest request) throws EntityNotFoundException;

    void deleteProjectById(Long projectId) throws EntityNotFoundException, IllegalStateException;
}
