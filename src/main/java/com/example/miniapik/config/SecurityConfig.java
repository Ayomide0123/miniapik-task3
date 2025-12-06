package com.example.miniapik.config;

import com.example.miniapik.filter.ApiKeyAuthFilter;
import com.example.miniapik.service.ApiKeyService;
import com.example.miniapik.service.JwtService;
import com.example.miniapik.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtService jwtService;
    private final ApiKeyService apiKeyService;

    public SecurityConfig(JwtService jwtService, ApiKeyService apiKeyService) {
        this.jwtService = jwtService;
        this.apiKeyService = apiKeyService;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Custom JWT filter
        OncePerRequestFilter jwtFilter = new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
                String header = request.getHeader("Authorization");
                if (header != null && header.startsWith("Bearer ")) {
                    String token = header.substring(7);
                    try {
                        Jws<Claims> claims = jwtService.parseToken(token);
                        String username = claims.getBody().getSubject();
                        String role = (String) claims.getBody().get("role");
                        if (username != null) {
                            UsernamePasswordAuthenticationToken auth =
                                    new UsernamePasswordAuthenticationToken(username, null,
                                            List.of(new SimpleGrantedAuthority(role)));
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    } catch (Exception ex) {
                        // invalid token - do nothing
                    }
                }
                chain.doFilter(request, response);
            }
        };

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/h2-console/**").permitAll()
                        .requestMatchers("/keys/create").hasRole("USER")   // users create API keys
                        .requestMatchers("/service/**").hasRole("SERVICE") // only services (API key) can access
                        .requestMatchers("/user/**").hasRole("USER")       // only users (JWT) can access
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new ApiKeyAuthFilter(apiKeyService), BasicAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, ApiKeyAuthFilter.class)
                .httpBasic(Customizer.withDefaults());

        // allow frames for H2 console
        http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
