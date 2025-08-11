package org.application.spring.ddd.controller;

import org.application.spring.configuration.security.AuthRequest;
import org.application.spring.configuration.security.AuthResponse;
import org.application.spring.configuration.security.JwtService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public AuthController(
            @Qualifier("authenticationManager") AuthenticationManager authenticationManager,
            JwtService jwtService,
            @Qualifier("userDetailsService") UserDetailsService userDetailsService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @RequestMapping(
            value = "/login",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        final UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwt = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @RequestMapping(value = "/unauthorized", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    //@ResponseBody
    public String unauthorized(Model model) {
        model.addAttribute("content", "شما احراز هویت نشده‌اید.");
        return "unauthorized"; // فایل unauthorized.html در مسیر templates
    }

    @RequestMapping(value = "/forbidden", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    //@ResponseBody
    public String forbidden(Model model) {
        model.addAttribute("content", "شما مجوز لازم را ندارید.");
        return "forbidden"; // فایل forbidden.html در مسیر templates
    }


}

