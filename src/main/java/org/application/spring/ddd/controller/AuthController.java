package org.application.spring.ddd.controller;

import org.application.spring.configuration.security.AuthRequest;
import org.application.spring.configuration.security.AuthResponse;
import org.application.spring.configuration.security.JwtService;
import org.application.spring.ddd.model.User;
import org.application.spring.ddd.service.UserService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
//@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserDetailsService userDetailsService, UserService userService, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        final UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwt = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    @RequestMapping(value = "/unauthorized", method = RequestMethod.GET)
    public String unauthorized(Model model) {
        model.addAttribute("content", "شما احراز هویت نشده‌اید.");
        return "unauthorized"; // فایل unauthorized.html در مسیر templates
    }

    @RequestMapping(value = "/forbidden", method = RequestMethod.GET)
    public String forbidden(Model model) {
        model.addAttribute("content", "شما مجوز لازم را ندارید.");
        return "forbidden"; // فایل forbidden.html در مسیر templates
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    @ResponseBody
    public User signup(
            @RequestParam String email,
            @RequestParam String password
    ) {

        User user = new User();
        user.setUserName(email);
        user.setPassword(password);
        userService.save(user);
        return user; // فایل forbidden.html در مسیر templates
    }


}

