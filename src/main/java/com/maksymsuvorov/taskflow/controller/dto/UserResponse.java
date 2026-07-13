package com.maksymsuvorov.taskflow.controller.dto;

import com.maksymsuvorov.taskflow.model.Role;

import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        String name,
        Role role,
        LocalDateTime createdAt
) {}
