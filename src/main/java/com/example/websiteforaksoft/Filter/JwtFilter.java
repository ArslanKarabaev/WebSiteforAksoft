package com.example.websiteforaksoft.Filter;

import com.example.websiteforaksoft.Service.JwtService;
import com.example.websiteforaksoft.Service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    // OncePerRequestFilter - гарантирует что фильтр выполнится ОДИН раз на запрос

    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Шаг 1: Берём заголовок Authorization из запроса
        final String authHeader = request.getHeader("Authorization");

        // Шаг 2: Если заголовка нет или он не начинается с "Bearer:" - пропускаем
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Шаг 3: Вырезаем токен из заголовка (убираем "Bearer ")
        final String token = authHeader.substring(7);

        // Шаг 4: Извлекаем имя пользователя из токена
        final String username = jwtService.extractUsername(token);

        // Шаг 5: Если пользователь найден и ещё не авторизован в текущей сессии
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userService.loadUserByUsername(username);

            // Шаг 6: Проверяем валидность токена
            if (jwtService.isTokenValid(token, userDetails)) {

                // Шаг 7: Создаём объект аутентификации и помещаем в SecurityContext
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Шаг 8: Передаём запрос дальше по цепочке фильтров
        filterChain.doFilter(request, response);
    }
}