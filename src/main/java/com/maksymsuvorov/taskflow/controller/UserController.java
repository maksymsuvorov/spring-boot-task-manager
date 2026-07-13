package com.maksymsuvorov.taskflow.controller;

import com.maksymsuvorov.taskflow.controller.dto.UserCreateRequest;
import com.maksymsuvorov.taskflow.controller.dto.UserResponse;
import com.maksymsuvorov.taskflow.controller.dto.UserUpdateRequest;
import com.maksymsuvorov.taskflow.mapper.UserMapper;
import com.maksymsuvorov.taskflow.model.User;
import com.maksymsuvorov.taskflow.service.UserServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserServiceInterface userService;

    // Self-registration lives at /api/auth/register; this is an admin operation.
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        User user = this.userService.createUser(request);

        return ResponseEntity
                .created(URI.create("/api/users/" + user.getId()))
                .body(UserMapper.toResponse(user));
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return this.userService.getAllUsers().stream()
                .map(UserMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable Long id) {
        return UserMapper.toResponse(this.userService.getUserById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @authz.isSelf(#id)")
    public UserResponse updateUserById(@PathVariable Long id,
                                       @Valid @RequestBody UserUpdateRequest request) {
        return UserMapper.toResponse(this.userService.updateUserById(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        this.userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }

}
