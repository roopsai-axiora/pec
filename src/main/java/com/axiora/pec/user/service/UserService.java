package com.axiora.pec.user.service;

import com.axiora.pec.common.exception.EmailAlreadyExistsException;
import com.axiora.pec.common.exception.ResourceNotFoundException;
import com.axiora.pec.user.domain.User;
import com.axiora.pec.user.auth.JwtUtil;
import com.axiora.pec.user.dto.AuthResponse;
import com.axiora.pec.user.dto.LoginRequest;
import com.axiora.pec.user.dto.RegisterRequest;
import com.axiora.pec.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = User.builder()
                .fullName(request.fullName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(request.role())
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user);

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getRole().name()
        );
    }

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found")
                );

        String token = jwtUtil.generateToken(user);

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getRole().name()
        );
    }

    public void deactivate(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found", userId)
                );
        user.setActive(false);
        userRepository.save(user);
    }
}