package com.alex.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.alex.security.auth.JwtAuthenticationEntryPoint;

import lombok.RequiredArgsConstructor;

// estas 2 @ deben ir juntas en Spring Boot 3.0
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    // son final para q en aut los inyecte lombok x el @RequiredArgsConstructor
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf().disable()
            .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .and()
            .authorizeHttpRequests().requestMatchers("/api/v1/auth/**").permitAll() //white list
            .anyRequest().authenticated()
            .and()
                .sessionManagement()    // eliminar las sessiones para q con c/request se valide el auth
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .authenticationProvider(authenticationProvider) // indicar a spring q auth Provider debe usar
                // se ejecuta before UsernamePasswordAuth... filter xq necesitamos verificar el jwt antes de actualizar el SecurityContextHolder
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

}
