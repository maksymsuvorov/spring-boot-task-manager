package com.maksymsuvorov.taskflow.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record ProjectCreateRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 1023) String description,
        @NotNull @Positive Long ownerId
) {}
