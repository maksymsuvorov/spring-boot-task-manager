package com.maksymsuvorov.taskflow.security;

import com.maksymsuvorov.taskflow.model.Role;
import com.maksymsuvorov.taskflow.model.User;
import com.maksymsuvorov.taskflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Solves the "first admin" problem: registration always assigns USER,
 * so the initial ADMIN is seeded from configuration. Only active
 * when both ADMIN_EMAIL and ADMIN_PASSWORD are provided.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email:}")
    private String adminEmail;

    @Value("${admin.password:}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (this.adminEmail.isBlank() || this.adminPassword.isBlank()) {
            return;
        }

        this.userRepository.findByEmail(this.adminEmail).ifPresentOrElse(
                existing -> {
                    if (existing.getRole() != Role.ADMIN) {
                        existing.setRole(Role.ADMIN);
                        log.info("Promoted existing user {} to ADMIN.", this.adminEmail);
                    }
                },
                () -> {
                    User admin = new User();
                    admin.setEmail(this.adminEmail);
                    admin.setName("Administrator");
                    admin.setPassword(this.passwordEncoder.encode(this.adminPassword));
                    admin.setRole(Role.ADMIN);
                    this.userRepository.save(admin);
                    log.info("Created ADMIN user {}.", this.adminEmail);
                }
        );
    }

}
