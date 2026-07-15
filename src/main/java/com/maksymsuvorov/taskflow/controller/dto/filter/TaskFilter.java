package com.maksymsuvorov.taskflow.controller.dto.filter;

import com.maksymsuvorov.taskflow.model.TaskPriority;
import com.maksymsuvorov.taskflow.model.TaskStatus;

import java.time.LocalDate;

public record TaskFilter(
        TaskStatus status,
        TaskPriority priority,
        Long projectId,
        Long assigneeId,
        LocalDate dueBefore
) {}
