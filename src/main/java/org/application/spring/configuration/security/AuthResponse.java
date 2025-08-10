package org.application.spring.configuration.security;

public class AuthResponse {
    private final String token;
    public AuthResponse(String token) { this.token = token; }
    // getter

    public String getToken() {
        return token;
    }
}
