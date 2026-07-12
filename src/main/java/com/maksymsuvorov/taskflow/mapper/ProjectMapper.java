package com.maksymsuvorov.taskflow.mapper;

import com.maksymsuvorov.taskflow.controller.dto.ProjectResponse;
import com.maksymsuvorov.taskflow.model.Project;

public final class ProjectMapper {

    private ProjectMapper() {
    }

    public static ProjectResponse toResponse(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getOwner().getId(),
                project.getCreatedAt(),
                project.getUpdatedAt()
        );
    }
}
