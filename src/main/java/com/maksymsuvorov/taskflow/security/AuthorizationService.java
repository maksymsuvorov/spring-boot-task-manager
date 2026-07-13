package com.maksymsuvorov.taskflow.security;

import com.maksymsuvorov.taskflow.model.Project;
import com.maksymsuvorov.taskflow.model.Task;
import com.maksymsuvorov.taskflow.model.User;
import com.maksymsuvorov.taskflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("authz")
@RequiredArgsConstructor
public class AuthorizationService {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final UserRepository userRepository;

    public String currentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getName() == null) {
            throw new AccessDeniedException("No authenticated user in the security context.");
        }

        return authentication.getName();
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> ROLE_ADMIN.equals(authority.getAuthority()));
    }

    // For @PreAuthorize: is the target user the caller themselves?
    public boolean isSelf(Long userId) {
        return this.userRepository.findById(userId)
                .map(user -> user.getEmail().equals(this.currentEmail()))
                .orElse(false);
    }

    // Loads the User entity of the caller (needed e.g. to set project ownership).
    public User requireCurrentUser() {
        return this.userRepository.findByEmail(this.currentEmail())
                .orElseThrow(() -> new AccessDeniedException("Authenticated user no longer exists."));
    }

    public void requireProjectOwnerOrAdmin(Project project) {
        if (!this.isAdmin() && !project.getOwner().getEmail().equals(this.currentEmail())) {
            throw new AccessDeniedException("Only the project owner can perform this action.");
        }
    }

    // Modify access to a task: project owner, current assignee, or admin.
    public void requireTaskModifyAccess(Task task) {
        if (this.isAdmin()) {
            return;
        }

        String email = this.currentEmail();
        boolean isProjectOwner = task.getProject().getOwner().getEmail().equals(email);
        boolean isAssignee = task.getAssignee() != null && task.getAssignee().getEmail().equals(email);

        if (!isProjectOwner && !isAssignee) {
            throw new AccessDeniedException("Only the project owner or the task assignee can perform this action.");
        }
    }

}
