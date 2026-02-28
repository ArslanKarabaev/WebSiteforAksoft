package com.example.websiteforaksoft.Config;

import com.example.websiteforaksoft.Filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/aboutUs").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/contacts", "/api/contacts/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/mainBanner/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/news/published").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/portfolio/published", "/api/portfolio/{id}").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/services/published", "/api/services/{id}").permitAll()

                        .requestMatchers("/api/users/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/aboutUs/").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/aboutUs/").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/contacts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/contacts/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/contacts/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/mainBanner").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/mainBanner/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/mainBanner/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/mainBanner/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/news", "/api/news/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/news/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/news/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/news/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/portfolio").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/portfolio/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/portfolio/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/portfolio/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/services").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/services/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/services/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/services/**").hasRole("ADMIN")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}