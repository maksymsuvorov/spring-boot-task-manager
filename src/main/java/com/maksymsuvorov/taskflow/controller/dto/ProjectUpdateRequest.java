package com.maksymsuvorov.taskflow.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectUpdateRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 1023) String description
) {}
