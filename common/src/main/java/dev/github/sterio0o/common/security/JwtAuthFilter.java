package dev.github.sterio0o.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException
    {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7); // Отрезаем "Bearer "

            try {
                // Достать данные из токена
                String userId = jwtService.extractUserId(token);
                List<String> roles = jwtService.extractRoles(token);

                // Превращение ролей в формат Spring Security
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                        .toList();

                // Создание объекта аутентификации
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);

                // Добавление аутентификации в SecurityContext
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (Exception e) {
                // Невалидный токен - вернет ошибку 401 Unauthorized
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        /*
            Запрос идет дальше по цепочке в контроллеры,
            в любом месте кода можно получить userId из токена, а не из БД
         */
        filterChain.doFilter(request, response);
    }
}
