package com.maksymsuvorov.taskflow.controller.dto;

import com.maksymsuvorov.taskflow.model.TaskPriority;
import com.maksymsuvorov.taskflow.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskUpdateRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 1023) String description,
        @NotNull TaskStatus status,
        @NotNull TaskPriority priority,
        @Positive Long assigneeId,
        LocalDate dueDate
) {}
