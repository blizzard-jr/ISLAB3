package org.example.is_lab1.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.example.is_lab1.services.JWTService;
import org.example.is_lab1.services.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JWTService jwtService;

    private final UserService userService;

    public JwtFilter(JWTService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String username = jwtService.extractUsername(token);
            log.debug("JwtFilter: Extracted username '{}' from token", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(username);
                log.debug("JwtFilter: Loaded user details for '{}'", username);

                if (jwtService.isValid(token, userDetails)) {
                    log.debug("JwtFilter: Token is valid, setting authentication for '{}'", username);
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    log.warn("JwtFilter: Token validation failed for user '{}'", username);
                }
            }
        } catch (Exception e) {
            // Если токен невалидный или произошла ошибка при валидации,
            // просто пропускаем запрос без аутентификации
            // SecurityConfig отклонит запрос с 401, если требуется аутентификация
            log.warn("JwtFilter: Exception during token validation: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);

    }
}
