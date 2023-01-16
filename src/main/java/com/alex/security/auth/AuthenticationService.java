package com.alex.security.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.alex.security.config.JwtService;
import com.alex.security.user.Role;
import com.alex.security.user.UserRepository;
import com.alex.security.user.Usuario;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = Usuario.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // antes de persistir se encripta pass
                .role(Role.USER) // static user <- no tiene tabla en DB
                .build();

        repository.save(user);

        var jwtToken = jwtService.generateJwt(user); // requiere el user para extraer el email

        return AuthenticationResponse.builder().token(jwtToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // requiere el UPAT: el authenticationManager hara todo el W x mi, si todo esta
        // correcto, auth al user. but no, it'll throw an exception
        authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        // // en este punto el email/pass are correct
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        var jwtToken = jwtService.generateJwt(user);

        return AuthenticationResponse.builder().token(jwtToken).build();
    }
}
