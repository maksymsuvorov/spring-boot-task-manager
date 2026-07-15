package com.maksymsuvorov.taskflow.service;

import com.maksymsuvorov.taskflow.controller.dto.UserCreateRequest;
import com.maksymsuvorov.taskflow.controller.dto.filter.UserFilter;
import com.maksymsuvorov.taskflow.controller.dto.UserUpdateRequest;
import com.maksymsuvorov.taskflow.model.User;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserServiceInterface {
    Page<User> getUsers(UserFilter filter, Pageable pageable);

    User getUserById(Long userId) throws EntityNotFoundException;

    User createUser(UserCreateRequest request) throws IllegalArgumentException;

    User updateUserById(Long userId, UserUpdateRequest request) throws EntityNotFoundException;

    void deleteUserById(Long userId) throws EntityNotFoundException, IllegalStateException;
}
