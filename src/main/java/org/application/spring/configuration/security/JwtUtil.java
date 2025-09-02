package org.application.spring.configuration.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.application.spring.configuration.properties.Properties;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public class JwtUtil {

    public static String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public static <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public static String generateToken(UserDetails user) {

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getAuthorities())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 3600000 * Properties.getAppJwtExpirationHours()))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String generateToken(UserDetails user, String ip) {

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getAuthorities())
                .claim("ip", ip)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 3600000 * Properties.getAppJwtExpirationHours()))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static String generateToken(UserDetails user, Map<String, Object> map) {

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getAuthorities())
                .addClaims(map)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 3600000 * Properties.getAppJwtExpirationHours()))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public static boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private static boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public static Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private static Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(Properties.getAppJwtSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

