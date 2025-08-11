package org.application.spring.configuration.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean("userDetailsService")
    public UserDetailsService userDetailsService() {
        return (username) -> {
            // In a real application, you would load the user from database
            // This is just an example

            if ("admin".equals(username)) {
                return new User(
                        "admin",
                        "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6", // password
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );
            } else if ("user".equals(username)) {
                return new User(
                        "user",
                        "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6", // password
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                );
            } else {
                throw new UsernameNotFoundException("User not found with username: " + username);
            }


        };
    }

    @Bean("authenticationManager")
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        return authentication -> {
            String username = authentication.getName();
            String password = authentication.getCredentials().toString();

            UserDetails user = userDetailsService.loadUserByUsername(username);

            if (!passwordEncoder.matches(password, user.getPassword())) {
                throw new BadCredentialsException("Invalid credentials");
            }

            return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
        };
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "https://example.com"));
        config.setAllowedMethods(List.of("GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE"));
        config.setAllowedHeaders(List.of("*"));
        //config.setAllowedHeaders(Arrays.asList("Content-Type, api_key, Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {

        return new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                if (request.getHeader("Accept").contains("application/json")) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\": \"شما احراز هویت نشده‌اید.\"}");
                } else {
                    response.sendRedirect("/unauthorized.html");
                }

            }
        };

    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {

        return new AccessDeniedHandler() {

            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {

                if (request.getHeader("Accept").contains("application/json")) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"error\": \"شما مجوز لازم را ندارید.\"}");
                } else {
                    response.sendRedirect("/forbidden.html");
                }

            }
        };

    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            @Qualifier("jwtAuthFilter") JwtAuthFilter jwtAuthFilter,
            CorsConfigurationSource corsConfigurationSource,
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler
    ) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(corsSpec -> {
                    corsSpec.configurationSource(corsConfigurationSource);
                })
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/spring/api/auth/**",
                                "/api/auth/**",

                                "/spring/swagger-ui/**",
                                "/spring/v3/api-docs/**",
                                "/spring/api-docs/**",
                                "/spring/webjars/**",

                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/api-docs/**",
                                "/webjars/**"
                        )
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/public/**", "/spring/xml/bean/sample")
                        .access((authentication, context) -> {
                            // مثال ساده: فقط کاربران با نقش ADMIN اجازه دارند
                            return authentication.get().getAuthorities().stream()
                                    .anyMatch(granted -> {
                                        return granted.getAuthority().equals("ROLE_ADMIN");
                                    }) ?
                                    new AuthorizationDecision(true) : new AuthorizationDecision(false);
                        })
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
