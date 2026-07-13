package com.maksymsuvorov.taskflow.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Note: no ownerId. The owner is always the authenticated caller; accepting
 * an owner id from the client would let anyone create projects on behalf of
 * other users.
 */
public record ProjectCreateRequest(
        @NotBlank @Size(max = 100) String name,
        @Size(max = 1023) String description
) {}
