package com.maksymsuvorov.taskflow.controller.dto.filter;

import com.maksymsuvorov.taskflow.model.Role;

public record UserFilter(
        String name,
        String email,
        Role role
) {}
