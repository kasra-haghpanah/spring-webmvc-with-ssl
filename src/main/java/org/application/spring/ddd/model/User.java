package org.application.spring.ddd.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Entity(name = "User")
@Table(name = User.TABLE_NAME)
public class User extends AppEntity implements UserDetails {

    @Transient
    public static final String TABLE_NAME = "user";

    @Column(name = "userName", length = 150)
    private String userName;
    @Column(name = "password", length = 150)
    private String password;
    @Column(name = "authority")
    @Lob
    private String authority;
    @Column(name = "firstName", length = 100)
    private String firstName;
    @Column(name = "lastName", length = 100)
    private String lastName;
    @Column(name = "activationCode")
    private String activationCode;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        return Arrays
                .stream(this.authority.split(","))
                .map(role -> {
                    return new SimpleGrantedAuthority(role);
                })
                .collect(Collectors.toSet());

    }


    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return this.activationCode == null || activationCode.equals("");
        //return UserDetails.super.isEnabled();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAuthority() {
        return authority;
    }

    public void setAuthority(String... authority) {
        if (authority != null && authority.length > 0) {
            for (int i = 0; i < authority.length; i++) {
                authority[i] = authority[i].trim().toUpperCase();
            }

            Set<String> roles = Set.of(authority);
            String[] authorities = new String[roles.size()];
            int i = 0;
            for (String role : roles) {
                authorities[i++] = role;
            }

            this.authority = String.join(",", authorities);
        }

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


}
