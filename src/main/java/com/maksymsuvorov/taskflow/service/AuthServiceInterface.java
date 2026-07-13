package com.maksymsuvorov.taskflow.service;

import com.maksymsuvorov.taskflow.controller.dto.AuthResponse;
import com.maksymsuvorov.taskflow.controller.dto.LoginRequest;
import com.maksymsuvorov.taskflow.controller.dto.RefreshRequest;
import com.maksymsuvorov.taskflow.controller.dto.UserCreateRequest;

public interface AuthServiceInterface {
    AuthResponse register(UserCreateRequest request) throws IllegalArgumentException;

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshRequest request);
}
