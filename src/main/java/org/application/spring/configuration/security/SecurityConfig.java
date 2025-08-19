package org.application.spring.configuration.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.application.spring.configuration.Properties;
import org.application.spring.configuration.exception.ErrorResponse;
import org.application.spring.ddd.service.UserService;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
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
import org.springframework.web.servlet.LocaleResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean("userDetailsService")
    public UserDetailsService userDetailsService(UserService userService) {
        return (username) -> {
            // In a real application, you would load the user from database
            // This is just an example

            org.application.spring.ddd.model.entity.User user = userService.findByUserName(username);

            if (user == null) {
                throw new UsernameNotFoundException("User not found with username: " + username);
            }

            return new User(user.getUsername(), user.getPassword(), user.getAuthorities());

/*            if ("admin".equals(username)) {
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
            }*/


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
        config.setAllowedOrigins(List.of(Properties.getCorsAllowedOrigins()));
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
    public AuthenticationEntryPoint authenticationEntryPoint(
            MessageSource messageSource,
            LocaleResolver localeResolver
    ) {

        return (request, response, authException) -> {

            Locale locale = localeResolver.resolveLocale(request);
            String accept = request.getHeader("Accept-Response");
            if (accept == null) {
                accept = "";
            }
            if (accept.contains("application/json")) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);


                ErrorResponse error = new ErrorResponse();
                error.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                Map<String, String> map = new HashMap<>();
                map.put("error", messageSource.getMessage("error.authentication", new Object[]{}, locale));
                error.setErrors(map);

                response.getWriter().write(error.toString());
            } else {
                response.sendRedirect("/spring/unauthorized");
            }
        };

    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(
            MessageSource messageSource,
            LocaleResolver localeResolver
    ) {

        return (request, response, accessDeniedException) -> {

            Locale locale = localeResolver.resolveLocale(request);
            String accept = request.getHeader("Accept-Response");
            if (accept == null) {
                accept = "";
            }
            if (accept.contains("application/json")) {
                response.setContentType("application/json;charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                ErrorResponse error = new ErrorResponse();
                error.setStatus(HttpServletResponse.SC_FORBIDDEN);
                Map<String, String> map = new HashMap<>();
                map.put("error", messageSource.getMessage("error.authorization", new Object[]{}, locale));
                error.setErrors(map);

                response.getWriter().write(error.toString());
            } else {
                response.sendRedirect("/spring/forbidden");
            }
        };
    }


    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthFilter jwtAuthFilter,
            CorsConfigurationSource corsConfigurationSource,
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler
    ) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/spring/login",
                                "/spring/signup",
                                "/spring/signup/**",
                                "/spring/unauthorized",
                                "/spring/forbidden",
                                "/spring/validate/**",
                                "/error",

                                "/spring/swagger-ui/**",
                                "/spring/v3/api-docs/**",
                                "/spring/api-docs/**",
                                "/spring/webjars/**"


                        )
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/spring/xml/bean/sample", "/make/mybean")
                        .access((authentication, context) -> {
                            // مثال ساده: فقط کاربران با نقش ADMIN اجازه دارند
                            return authentication.get().getAuthorities().stream()
                                    .anyMatch(granted -> {
                                        return granted.getAuthority().equals("ADMIN");
                                    }) ?
                                    new AuthorizationDecision(true) : new AuthorizationDecision(false);
                        })
                        .anyRequest().authenticated()
                )
                .cors(corsSpec -> {
                    corsSpec.configurationSource(corsConfigurationSource);
                })
                .exceptionHandling(exceptionHandlingConfigurer -> {
                    exceptionHandlingConfigurer
                            .authenticationEntryPoint(authenticationEntryPoint)
                            .accessDeniedHandler(accessDeniedHandler);
                })
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
