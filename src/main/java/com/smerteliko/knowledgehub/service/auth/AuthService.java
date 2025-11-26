package com.smerteliko.knowledgehub.service.auth;

import com.smerteliko.knowledgehub.dto.auth.JwtResponse;
import com.smerteliko.knowledgehub.dto.auth.LoginRequest;
import com.smerteliko.knowledgehub.dto.auth.RegisterRequest;
import com.smerteliko.knowledgehub.entity.enums.Role;
import com.smerteliko.knowledgehub.entity.User;
import com.smerteliko.knowledgehub.repository.UserRepository;
import com.smerteliko.knowledgehub.service.JWT.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public JwtResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email is already in use.");
        }

        var user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);

        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return new JwtResponse(jwtToken, user.getUsernameField(), user.getEmail(), user.getId());
    }

    public JwtResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        var user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found after successful authentication."));

        var jwtToken = jwtService.generateToken(user);
        return new JwtResponse(jwtToken, user.getUsernameField(), user.getEmail(), user.getId());
    }
}
