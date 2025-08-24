package org.application.spring.ddd.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.application.spring.configuration.security.AuthResponse;
import org.application.spring.ddd.dto.StoreDTO;
import org.application.spring.ddd.model.entity.User;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@Validated
public class ValidateController {


    @RequestMapping(value = "/validate/store", method = RequestMethod.POST)
    @ResponseBody
    public String createStore(@RequestBody @Valid StoreDTO storeDTO) {
        return "Store created successfully";
    }


    @RequestMapping(
            value = "/validate/signup",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    @ResponseBody
    public AuthResponse signup(
            @RequestParam @Valid @Email(message = "field.email") String email,
            @RequestParam @Valid @Length(min = 3, max = 30, message = "field.password") String password,
            @RequestParam @Valid @Length(min = 2, max = 100, message = "field.name") String firstName,
            @RequestParam @Valid @Length(min = 2, max = 100, message = "field.name") String lastName,
            @RequestParam @Valid @Pattern(regexp = "[0-9]{11,13}", message = "field.phone") String phoneNumber
    ) {
        // <script>alert('XSS')</script><p>Hello <b>World</b></p>
        return new AuthResponse("jwt-token");
    }


}
