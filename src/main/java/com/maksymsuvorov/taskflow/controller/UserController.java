package com.maksymsuvorov.taskflow.controller;

import com.maksymsuvorov.taskflow.controller.dto.PageResponse;
import com.maksymsuvorov.taskflow.controller.dto.UserCreateRequest;
import com.maksymsuvorov.taskflow.controller.dto.UserResponse;
import com.maksymsuvorov.taskflow.controller.dto.UserUpdateRequest;
import com.maksymsuvorov.taskflow.controller.dto.filter.UserFilter;
import com.maksymsuvorov.taskflow.mapper.UserMapper;
import com.maksymsuvorov.taskflow.model.User;
import com.maksymsuvorov.taskflow.service.UserServiceInterface;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

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
    public PageResponse<UserResponse> getUsers(@ModelAttribute UserFilter filter,
                                               @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return PageResponse.from(
                this.userService.getUsers(filter, pageable)
                        .map(UserMapper::toResponse)
        );
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
