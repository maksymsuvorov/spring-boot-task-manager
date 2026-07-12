package com.maksymsuvorov.taskflow.controller.dto;

import com.maksymsuvorov.taskflow.model.TaskPriority;
import com.maksymsuvorov.taskflow.model.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record TaskResponse(
        Long id,
        String title,
        String description,
        TaskStatus status,
        TaskPriority priority,
        Long projectId,
        Long assigneeId,
        LocalDate dueDate,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
