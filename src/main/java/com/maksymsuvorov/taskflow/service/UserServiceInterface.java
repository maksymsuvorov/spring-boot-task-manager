package com.maksymsuvorov.taskflow.service;

import com.maksymsuvorov.taskflow.controller.dto.UserCreateRequest;
import com.maksymsuvorov.taskflow.controller.dto.UserUpdateRequest;
import com.maksymsuvorov.taskflow.model.User;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

public interface UserServiceInterface {
    List<User> getAllUsers();

    User getUserById(Long userId) throws EntityNotFoundException;

    User createUser(UserCreateRequest request) throws IllegalArgumentException;

    User updateUserById(Long userId, UserUpdateRequest request) throws EntityNotFoundException;

    void deleteUserById(Long userId) throws EntityNotFoundException, IllegalStateException;
}
