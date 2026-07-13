package com.maksymsuvorov.taskflow.service;

import com.maksymsuvorov.taskflow.controller.dto.AuthResponse;
import com.maksymsuvorov.taskflow.controller.dto.LoginRequest;
import com.maksymsuvorov.taskflow.controller.dto.RefreshRequest;
import com.maksymsuvorov.taskflow.controller.dto.UserCreateRequest;
import com.maksymsuvorov.taskflow.mapper.UserMapper;
import com.maksymsuvorov.taskflow.model.User;
import com.maksymsuvorov.taskflow.repository.UserRepository;
import com.maksymsuvorov.taskflow.security.JwtService;
import com.maksymsuvorov.taskflow.security.RefreshTokenService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImplementation implements AuthServiceInterface {

    private final UserServiceInterface userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public AuthResponse register(UserCreateRequest request) throws IllegalArgumentException {
        User user = this.userService.createUser(request);

        return this.buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // Throws BadCredentialsException on wrong email OR password
        // (user-not-found is deliberately indistinguishable from wrong password).
        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = this.userRepository.findByEmail(request.email())
                .orElseThrow(() -> new EntityNotFoundException("User with email " + request.email() + " was not found."));

        return this.buildAuthResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        // Rotation: the presented token is deleted and a new one is issued.
        User user = this.refreshTokenService.consume(request.refreshToken());

        return this.buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        return AuthResponse.bearer(
                this.jwtService.generateToken(user.getEmail()),
                this.refreshTokenService.issue(user),
                this.jwtService.getExpirationMs(),
                UserMapper.toResponse(user)
        );
    }

}
