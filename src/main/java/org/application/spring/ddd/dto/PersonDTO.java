package org.application.spring.ddd.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.application.spring.configuration.security.SecurityConfig;

public class PersonDTO {

    @Email(message = "field.email")
    @NotBlank(message = "email نباید خالی باشد")
    private String email;

    @NotBlank(message = "firstName نباید خالی باشد")
    private String firstName;

    @NotBlank(message = "lastName نباید خالی باشد")
    private String lastName;

    @Pattern(regexp = "[0-9]{11,13}", message = "field.phone")
    private String phoneNumber;

    public PersonDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
