package com.maksymsuvorov.taskflow.service;

import com.maksymsuvorov.taskflow.controller.dto.UserCreateRequest;
import com.maksymsuvorov.taskflow.controller.dto.UserUpdateRequest;
import com.maksymsuvorov.taskflow.model.User;
import com.maksymsuvorov.taskflow.repository.ProjectRepository;
import com.maksymsuvorov.taskflow.repository.TaskRepository;
import com.maksymsuvorov.taskflow.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImplementation implements UserServiceInterface {

    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return this.userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long userId) throws EntityNotFoundException {
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " was not found."));
    }

    @Override
    public User createUser(UserCreateRequest request) throws IllegalArgumentException {
        if (this.userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("User with email " + request.email() + " already exists.");
        }

        User user = new User();
        user.setEmail(request.email());
        user.setName(request.name());
        user.setPassword(this.passwordEncoder.encode(request.password()));

        return this.userRepository.save(user);
    }

    @Override
    public User updateUserById(Long userId, UserUpdateRequest request) throws EntityNotFoundException {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " was not found."));

        user.setName(request.name());
        return user;
    }

    @Override
    public void deleteUserById(Long userId) throws EntityNotFoundException, IllegalStateException {
        User user = this.userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " was not found."));

        if (this.projectRepository.existsByOwnerId(userId)) {
            throw new IllegalStateException(
                    "User cannot be deleted because they own one or more projects."
            );
        }

        if (this.taskRepository.existsByAssigneeId(userId)) {
            throw new IllegalStateException(
                    "User cannot be deleted because they are assigned to one or more tasks."
            );
        }

        this.userRepository.delete(user);
    }

}
