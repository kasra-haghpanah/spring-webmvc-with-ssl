package org.application.spring.ddd.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.application.spring.configuration.security.AuthenticationRequest;
import org.application.spring.configuration.security.AuthenticationResponse;
import org.application.spring.configuration.security.JwtUtil;
import org.application.spring.ddd.model.entity.User;
import org.application.spring.ddd.service.MailService;
import org.application.spring.ddd.service.UserService;
import org.hibernate.validator.constraints.Length;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;
import java.util.UUID;

@Controller
//@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserService userService;
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    private final MailService mailService;

    public AuthController(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, UserService userService, MessageSource messageSource, LocaleResolver localeResolver, MailService mailService) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
        this.mailService = mailService;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public AuthenticationResponse login(@RequestBody AuthenticationRequest authRequest, HttpServletRequest request) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));
        final UserDetails user = userDetailsService.loadUserByUsername(authRequest.username());
        ((User) user).setIp(request.getRemoteAddr());
        final String jwt = JwtUtil.generateToken(user, request.getRemoteAddr());
        return new AuthenticationResponse(jwt);
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
        user.setActivationCode(UUID.randomUUID().toString());
        user.addAuthorities("ADMIN", "USER");
        user = userService.save(user);
        mailService.sendActivationMail(user);
        return user; // فایل forbidden.html در مسیر templates
    }


    @RequestMapping(
            value = "/activate/{username}/{activationCode}",
            method = RequestMethod.GET
    )
    @ResponseBody
    public String activate(
            @PathVariable("username") @Valid @Email(message = "field.email") String email,
            @PathVariable("activationCode") @Valid @Length(min = 3, max = 100, message = "field.password") String activationCode
    ) {
        int isUpdate = userService.updateUserForActivationCode(email);
        return "isUpdate -> " + isUpdate;
    }


}

