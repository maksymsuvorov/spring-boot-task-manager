package com.maksymsuvorov.taskflow.mapper;

import com.maksymsuvorov.taskflow.controller.dto.UserResponse;
import com.maksymsuvorov.taskflow.model.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
