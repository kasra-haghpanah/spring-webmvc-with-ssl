package org.application.spring.ddd.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.application.spring.configuration.security.AuthRequest;
import org.application.spring.configuration.security.AuthResponse;
import org.application.spring.configuration.security.JwtService;
import org.application.spring.ddd.model.entity.User;
import org.application.spring.ddd.service.UserService;
import org.hibernate.validator.constraints.Length;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Controller
//@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserDetailsService userDetailsService, UserService userService, PasswordEncoder passwordEncoder, MessageSource messageSource, LocaleResolver localeResolver) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public AuthResponse login(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        final UserDetails user = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwt = jwtService.generateToken(userService.findByUserName(request.getUsername()));
        return new AuthResponse(jwt);
    }

    @RequestMapping(value = "/unauthorized", method = RequestMethod.GET)
    public String unauthorized(Model model, HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);

        model.addAttribute("content", messageSource.getMessage("error.authentication", new Object[]{}, locale));
        return "unauthorized"; // فایل unauthorized.html در مسیر templates
    }

    @RequestMapping(value = "/forbidden", method = RequestMethod.GET)
    public String forbidden(Model model, HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);
        model.addAttribute("content", messageSource.getMessage("error.authorization", new Object[]{}, locale));
        return "forbidden"; // فایل forbidden.html در مسیر templates
    }

    @RequestMapping(
            value = "/signup",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    @ResponseBody
    public User signup(
            @RequestParam @Valid @Email(message = "field.email") String email,
            @RequestParam @Valid @Length(min = 3, max = 30, message = "field.password") String password,
            @RequestParam @Valid @Length(min = 2, max = 100, message = "field.name") String firstName,
            @RequestParam @Valid @Length(min = 2, max = 100, message = "field.name") String lastName,
            @RequestParam @Valid @Pattern(regexp = "[0-9]{11,13}", message = "field.phone") String phoneNumber
    ) {

        User user = new User();
        user.setUserName(email);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPhoneNumber(phoneNumber);
        user = userService.save(user);
        return user; // فایل forbidden.html در مسیر templates
    }


}

