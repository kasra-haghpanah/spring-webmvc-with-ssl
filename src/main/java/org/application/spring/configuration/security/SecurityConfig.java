package org.application.spring.configuration.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.application.spring.configuration.exception.ErrorResponse;
import org.application.spring.configuration.log.RequestLoggingInterceptor;
import org.application.spring.configuration.properties.Properties;
import org.application.spring.configuration.server.ContextPathAndXssFilter;
import org.application.spring.configuration.server.InvalidTokenType;
import org.application.spring.configuration.server.ServerUtil;
import org.application.spring.ddd.model.entity.User;
import org.application.spring.ddd.model.json.type.Authority;
import org.application.spring.ddd.service.UserService;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Configuration
@EnableWebSecurity
@DependsOn({"properties"})
public class SecurityConfig {

    // مسیرهایی که نباید توکن بررسی شوند
    private static final String[] PUBLIC_PATHS = {
            "/spring/login",
            "/spring/signup/**",
            "/spring/unauthorized",
            "/spring/forbidden",
            // "/spring/validate/",
            "/spring/activate/**",
            "/error",
            "/spring/check/exception",
            "/spring/swagger-ui/**",
            "/spring/v3/api-docs/**",
            "/spring/api-docs/**",
            "/spring/webjars/**",
            "/spring/validate/signup",
            "/spring/actuator/**",
            // "/spring/resource/**",
            //"/spring/js/**"
            "/spring/images/**"
            // "/spring/actuator/prometheus/**"
    };

    @Bean
    public FilterRegistrationBean<ContextPathAndXssFilter> xssFilterRegistration() {
        FilterRegistrationBean<ContextPathAndXssFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ContextPathAndXssFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    public static String sanitize(String input) {
        return input == null ? null : Jsoup.clean(input, Safelist.none());
    }


    public static boolean isPublicPath(String path) {
        return Arrays.stream(PUBLIC_PATHS).anyMatch(path::startsWith);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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

    @Bean("userDetailsService")
    public UserDetailsService userDetailsService(UserService userService) {
        return (username) -> {
            // In a real application, you would load the user from database
            // This is just an example
            User user = userService.findByUserName(username);
            if (user == null) {
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
            //user.setIp(remoteAddress);
            return user;
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


    @Bean("rateLimitingFilter")
    public OncePerRequestFilter rateLimitingFilter(
            MessageSource messageSource,
            LocaleResolver localeResolver
    ) {

         /*
rate-limiting:
  enabled: true
  default-policy:
    capacity: 10
    refill-tokens: 10
    refill-duration: 1s
  policies:

    - path: /api/public/**
      capacity: 20
      refill-tokens: 20
      refill-duration: 1s

    - path: /api/private/**
      capacity: 5
      refill-tokens: 5
      refill-duration: 1s
    * */

        List<Map<String, Object>> list = Properties.getLimitRatingList();

        final RateLimitingProperties rateLimitingProperties = new RateLimitingProperties(
                true,
                new RateLimitingProperties.Policy(
                        "/spring/**",
                        Properties.getLimitRatingCapacity(),
                        Properties.getLimitRatingRefillTokens(),
                        Duration.ofSeconds(Properties.getLimitRatingRefillDurationInSecond()))
        );

        for (Map<String, Object> map : list) {

            String path = (String) map.get("path");
            Integer capacity = (Integer) map.get("capacity");
            Integer refillTokens = (Integer) map.get("refill-tokens");
            Integer duration = (Integer) map.get("duration");

            rateLimitingProperties.add(new RateLimitingProperties.Policy(path, capacity, refillTokens, Duration.ofSeconds(duration)));
        }

        return new OncePerRequestFilter() {


            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

                Locale locale = localeResolver.resolveLocale(request);

                if (!rateLimitingProperties.enabled) {
                    filterChain.doFilter(request, response);
                    return;
                }

                String path = request.getRequestURI();
                RateLimitingProperties.Policy policy = matchPolicy(path);
                Bucket bucket = RateLimitingProperties.buckets.computeIfAbsent(path, p -> createBucket(policy));

                if (bucket.tryConsume(1)) {
                    filterChain.doFilter(request, response);
                } else {
                    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    response.setContentType("application/json;charset=UTF-8");

                    Map<String, String> map = new HashMap<>();
                    map.put("rate.limit.exceeded", messageSource.getMessage("rate.limit.exceeded", null, locale));

                    ErrorResponse errorResponse = new ErrorResponse();
                    errorResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS);
                    errorResponse.setErrors(map);
                    response.getWriter().write(errorResponse.toString());
                }
            }

            private RateLimitingProperties.Policy matchPolicy(String path) {
                return rateLimitingProperties.policies.stream()
                        .filter(p -> {
                            return path.matches(p.path().replace("**", ".*"));
                        })
                        .findFirst()
                        .orElse(rateLimitingProperties.defaultPolicy);
            }

            private Bucket createBucket(RateLimitingProperties.Policy policy) {
                Refill refill = Refill.intervally(policy.refillTokens(), policy.refillDuration());
                Bandwidth limit = Bandwidth.classic(policy.capacity(), refill);
                return Bucket.builder().addLimit(limit).build();
            }


        };

    }

    @Bean("jwtAuthenticationFilter")
    public OncePerRequestFilter JwtAuthenticationFilter(UserDetailsService userDetailsService) {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                String authHeader = request.getHeader("Authorization");
                final String jwt;
                final String username;
                final String ip;
                final String firstName;
                final String lastName;
                final String phoneNumber;
                final String[] roles;

                if (authHeader == null || authHeader.trim().equals("")) {

                    authHeader = Optional.ofNullable(request.getCookies())
                            .map(Arrays::stream)
                            .orElseGet(Stream::empty)
                            .filter(cookie -> cookie.getName().equals("access_token") && !cookie.getValue().equals(""))
                            .findFirst()
                            .map(Cookie::getValue)
                            .map((token -> "Bearer " + token))
                            .orElse("");

                }
                // for logging
                request.setAttribute("start-time", System.nanoTime());
                //request.setAttribute("invalidTokenType", InvalidTokenType.NONE);
                request.setAttribute("tokenValue", authHeader);
                ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, 4_096);
                ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
                // for logging
                // **************************************
                // اگر مسیر عمومی بود، بدون بررسی توکن عبور کن
                if (isPublicPath(request.getRequestURI())) {
                    filterChain.doFilter(wrappedRequest, wrappedResponse);
                    request.setAttribute("request-body", ServerUtil.getRequestBody(wrappedRequest));
                    request.setAttribute("response-body", ServerUtil.getResponseBody(wrappedResponse));
                    wrappedResponse.copyBodyToResponse();
                    return;
                }
                // **************************************
                if (authHeader == null || authHeader.trim().equals("") || !authHeader.startsWith("Bearer ")) {
                    filterChain.doFilter(wrappedRequest, wrappedResponse);
                    request.setAttribute("request-body", ServerUtil.getRequestBody(wrappedRequest));
                    request.setAttribute("response-body", ServerUtil.getResponseBody(wrappedResponse));
                    wrappedResponse.copyBodyToResponse();
                    return;
                }
                jwt = authHeader.substring(7);
                // code
                try {
                    Claims claims = JwtUtil.extractAllClaims(jwt);
                    username = (String) claims.get("sub");
                    ip = (String) claims.get("ip");
                    firstName = (String) claims.get("firstName");
                    lastName = (String) claims.get("lastName");
                    phoneNumber = (String) claims.get("phoneNumber");
                    List<Map<String, String>> list = ((List<Map<String, String>>) claims.get("roles"));
                    roles = new String[list.size()];
                    int i = 0;
                    for (Map<String, String> map : list) {
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            roles[i++] = entry.getValue();
                        }
                    }

                } catch (JwtException e) {
                    // اگر JWT نامعتبر بود، ادامه نده
                    filterChain.doFilter(wrappedRequest, wrappedResponse);
                    request.setAttribute("request-body", ServerUtil.getRequestBody(wrappedRequest));
                    request.setAttribute("response-body", ServerUtil.getResponseBody(wrappedResponse));
                    wrappedResponse.copyBodyToResponse();
                    return;
                }

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    //UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    //((User) userDetails).setIp(ip);
                    User user = new User();
                    user.setIp(ip);
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setPhoneNumber(phoneNumber);
                    user.setAuthority(new Authority(roles));
                    user.setUserName(username);

                    if (JwtUtil.isTokenValid(jwt, user)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }

                filterChain.doFilter(wrappedRequest, wrappedResponse);
                request.setAttribute("request-body", ServerUtil.getRequestBody(wrappedRequest));
                request.setAttribute("response-body", ServerUtil.getResponseBody(wrappedResponse));
                wrappedResponse.copyBodyToResponse();

            }
        };
    }


    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(
            MessageSource messageSource,
            LocaleResolver localeResolver
    ) {

        return (request, response, authException) -> {

            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, 4_096);
            ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

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

                wrappedResponse.getWriter().write(error.toString());
                RequestLoggingInterceptor.setLog(wrappedRequest, wrappedResponse, null, null);
                wrappedResponse.copyBodyToResponse();
            } else {
                response.sendRedirect("/spring/unauthorized");
                RequestLoggingInterceptor.setLog(wrappedRequest, wrappedResponse, null, null);
                wrappedResponse.copyBodyToResponse();
            }
        };

    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler(
            MessageSource messageSource,
            LocaleResolver localeResolver
    ) {

        return (request, response, accessDeniedException) -> {
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, 4_096);
            ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

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
                RequestLoggingInterceptor.setLog(wrappedRequest, wrappedResponse, null, null);
                wrappedResponse.copyBodyToResponse();
            } else {
                response.sendRedirect("/spring/forbidden");
                RequestLoggingInterceptor.setLog(wrappedRequest, wrappedResponse, null, null);
                wrappedResponse.copyBodyToResponse();
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            @Qualifier("rateLimitingFilter") OncePerRequestFilter rateLimitingFilter,
            @Qualifier("jwtAuthenticationFilter") OncePerRequestFilter jwtAuthenticationFilter,
            CorsConfigurationSource corsConfigurationSource,
            AuthenticationEntryPoint authenticationEntryPoint,
            AccessDeniedHandler accessDeniedHandler
    ) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(corsSpec -> {
                    corsSpec.configurationSource(corsConfigurationSource);
                })
                .headers(headers -> headers
                        /*.httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .preload(true)
                                .maxAgeInSeconds(31536000) // یک سال
                        )*/
                        .contentTypeOptions(contentType -> contentType.disable()) // غیرفعال کردن پیش‌فرض
                        .addHeaderWriter((request, response) -> {
                            response.setHeader("X-Content-Type-Options", "nosniff");
                        })
                        // XSS (Cross-Site Scripting) to avoid injecting javascript code on a browser
                        // فیلتر ضد XSS با Jsoup در Spring Boot
                        .xssProtection(xss -> {
                            xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK);
                        })
                        .contentSecurityPolicy(csp -> {
                            // CSP => each client send or upload data just from their domain
                            csp.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self'; font-src 'self'");
                        })
                )
                .exceptionHandling(exceptionHandlingConfigurer -> {
                    exceptionHandlingConfigurer
                            .authenticationEntryPoint(authenticationEntryPoint)
                            .accessDeniedHandler(accessDeniedHandler);
                })
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_PATHS)
                        .permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/spring/xml/bean/sample",
                                "/spring/make/mybean",
                                "/spring/files/**",
                                "/spring/js/**",
                                "/spring/images/**"
                        )
                        .access((authentication, context) -> {

                            if (!isValidToken(authentication, context)) {
                                return new AuthorizationDecision(false);
                            }

                            // مثال ساده: فقط کاربران با نقش ADMIN اجازه دارند
                            boolean check = authentication.get().getAuthorities().stream()
                                    .anyMatch(granted -> {
                                        return granted.getAuthority().equals("ADMIN") || granted.getAuthority().equals("USER");
                                    });
                            if (check) {
                                return new AuthorizationDecision(true);
                            }

                            context.getRequest().setAttribute("invalidTokenType", InvalidTokenType.INVALIDROLE);
                            return new AuthorizationDecision(false);
                        })
                        .requestMatchers(HttpMethod.POST,
                                "/spring/validate/store",
                                "/spring/refresh/token",
                                "/spring/upload"
                        )
                        .access((authentication, context) -> {
                            if (!isValidToken(authentication, context)) {
                                return new AuthorizationDecision(false);
                            }
                            // مثال ساده: فقط کاربران با نقش ADMIN اجازه دارند
                            boolean check = authentication.get().getAuthorities().stream()
                                    .anyMatch(granted -> {
                                        return granted.getAuthority().equals("ADMIN") || granted.getAuthority().equals("USER");
                                    });
                            if (check) {
                                return new AuthorizationDecision(true);
                            }

                            context.getRequest().setAttribute("invalidTokenType", InvalidTokenType.INVALIDROLE);
                            //context.getRequest().setAttribute("tokenValue", ServerUtil.getAuthorization(context.getRequest()));
                            return new AuthorizationDecision(false);

                        })
                        .anyRequest().authenticated()
                )
                .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class) // اجرای قبل از JWT
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    private static boolean isValidToken(Supplier<Authentication> authentication, RequestAuthorizationContext context) {

        if (!(authentication.get().getPrincipal() instanceof User)) {
            context.getRequest().setAttribute("invalidTokenType", InvalidTokenType.INVALIDTOKEN);
            return false; // log
        }
        User user = (User) authentication.get().getPrincipal();
        String currentIp = context.getRequest().getRemoteAddr();
        if (currentIp.equals("0:0:0:0:0:0:0:1")) {
            currentIp = "127.0.0.1";
        }
        if (!user.getIp().equals(currentIp)) {
            context.getRequest().setAttribute("invalidTokenType", InvalidTokenType.INVALIDIP);
            return false; // log
        }
        return true;
    }
}
