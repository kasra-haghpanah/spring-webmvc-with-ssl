package org.application.spring.ddd.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.application.spring.configuration.properties.Properties;
import org.application.spring.configuration.security.AuthenticationRequest;
import org.application.spring.configuration.security.AuthenticationResponse;
import org.application.spring.configuration.security.JwtUtil;
import org.application.spring.configuration.server.ServerUtil;
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

import java.util.*;
import java.util.stream.Stream;

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

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(Model model, HttpServletRequest request) {
        Locale locale = localeResolver.resolveLocale(request);

        model.addAttribute("title", messageSource.getMessage("login.title", new Object[]{}, locale));

        model.addAttribute("version", Properties.getVersion());
        return "index"; // فایل unauthorized.html در مسیر templates
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public AuthenticationResponse logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("access_token", null); // مقدار null یعنی حذف
        cookie.setPath("/"); // مسیر باید با مسیر اصلی کوکی یکی باشه
        cookie.setMaxAge(0); // صفر یعنی حذف فوری
        cookie.setHttpOnly(true); // اگر قبلاً HttpOnly بوده، حفظ کن
        cookie.setSecure(true);   // اگر روی HTTPS بوده، حفظ کن
        response.addCookie(cookie);
        return new AuthenticationResponse("");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public AuthenticationResponse login(
            @RequestBody AuthenticationRequest authRequest,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));
        User user = (User) authentication.getPrincipal();
        user.setIp(request.getRemoteAddr());

        Map<String, Object> map = new HashMap<>();
        String ip = ServerUtil.getClientIp(request);
        if (ip.equals("0:0:0:0:0:0:0:1")) {
            ip = "127.0.0.1";
        }
        map.put("ip", ip);
        map.put("firstName", user.getFirstName());
        map.put("lastName", user.getLastName());
        map.put("phoneNumber", user.getPhoneNumber());

        final String jwtToken = JwtUtil.generateToken(user, map);

        Cookie cookie = new Cookie("access_token", jwtToken);
        cookie.setHttpOnly(true); // جلوگیری از دسترسی جاوااسکریپت
        cookie.setSecure(true);   // فقط در HTTPS
        cookie.setPath("/");      // در کل دامنه معتبر باشه
        cookie.setMaxAge(Properties.getCookieAgeMinutes() * 60); // اعتبار 15 دقیقه

        response.addCookie(cookie);

        //response.setHeader("Location", "/spring/"); // redirect url
        //response.setStatus(HttpStatus.MOVED_PERMANENTLY.value()); // redirect url
        return new AuthenticationResponse(jwtToken);
    }

    @RequestMapping(value = "/refresh/token", method = RequestMethod.POST)
    @ResponseBody
    public AuthenticationResponse refreshToken(HttpServletRequest request, HttpServletResponse response) {

        String token = Optional.ofNullable(request.getCookies())
                .map(Arrays::stream)
                .orElseGet(Stream::empty)
                .filter(cookie -> cookie.getName().toLowerCase().equals("access_token"))
                .findFirst()
                .map(cookie -> cookie.getValue())
                .orElse(request.getHeader("Authorization") == null ? "" : request.getHeader("Authorization").replace("Bearer ", ""));

        String username = JwtUtil.extractUsername(token);

        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        User user = (User) userDetails;
        ((User) user).setIp(request.getRemoteAddr());
        //final String jwtToken = JwtUtil.generateToken(user, request.getRemoteAddr());

        user.setIp(request.getRemoteAddr());

        Map<String, Object> map = new HashMap<>();
        String ip = ServerUtil.getClientIp(request);
        if (ip.equals("0:0:0:0:0:0:0:1")) {
            ip = "127.0.0.1";
        }
        map.put("ip", ip);
        map.put("firstName", user.getFirstName());
        map.put("lastName", user.getLastName());
        map.put("phoneNumber", user.getPhoneNumber());

        final String jwtToken = JwtUtil.generateToken(user, map);


        Cookie cookie = new Cookie("access_token", jwtToken);
        cookie.setHttpOnly(true); // جلوگیری از دسترسی جاوااسکریپت
        cookie.setSecure(true);   // فقط در HTTPS
        cookie.setPath("/");      // در کل دامنه معتبر باشه
        cookie.setMaxAge(Properties.getCookieAgeMinutes() * 60); // اعتبار 15 دقیقه

        response.addCookie(cookie);

        return new AuthenticationResponse(jwtToken);
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

