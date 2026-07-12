package com.maksymsuvorov.taskflow.controller.dto;

import com.maksymsuvorov.taskflow.model.TaskPriority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record TaskCreateRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 1023) String description,
        TaskPriority priority,
        @Positive Long assigneeId,
        LocalDate dueDate
) {}
