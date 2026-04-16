package com.basiccrud.service;

import com.basiccrud.dto.AuthResponse;
import com.basiccrud.dto.LoginRequest;
import com.basiccrud.dto.RegisterRequest;
import com.basiccrud.model.AppUser;
import com.basiccrud.model.Role;
import com.basiccrud.repository.AppUserRepository;
import com.basiccrud.security.AppUserDetailsService;
import com.basiccrud.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AppUserDetailsService appUserDetailsService;

    public AuthService(
            AppUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            AppUserDetailsService appUserDetailsService) {
        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.appUserDetailsService = appUserDetailsService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String username = request.username().trim();
        if (appUserRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken");
        }
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        appUserRepository.save(user);
        UserDetails details = appUserDetailsService.loadUserByUsername(username);
        return AuthResponse.of(jwtService.generateToken(details), jwtService.getExpirationSeconds());
    }

    public AuthResponse login(LoginRequest request) {
        String username = request.username().trim();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, request.password()));
        UserDetails details = appUserDetailsService.loadUserByUsername(username);
        return AuthResponse.of(jwtService.generateToken(details), jwtService.getExpirationSeconds());
    }
}
