package com.alex.security.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component // registra como bean para poderla inyectar
@RequiredArgsConstructor // construira constructor con cada property(final) q le creemos a la clase
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // La inyeccion se hace en auto x constructo con lombok
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain // continuara con la ejecucion de los demas filtros de la filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // check si viene el jwt
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(jwt);

        // si ya esta auth NO debo actualizar el SecurityContextHolder ni demas cosas
        // // si viene 1 email y aun no esta auth
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // userdetail xq nuesto Usuario Entity lo implementa, sino, seria Usuario mismo
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // validar si el jwt es valido
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Spring necesita ese UPAT para actualizar el SecurityContextHolder - carga el
                // user asociado al token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // actualizamos el SecurityContextHolder
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);

    }

}
