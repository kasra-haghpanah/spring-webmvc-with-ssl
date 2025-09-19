package org.application.spring.ddd.model.entity;

import jakarta.persistence.*;
import org.application.spring.ddd.model.json.converter.AuthorityConverter;
import org.application.spring.ddd.model.json.type.Authority;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@Entity(name = User.TABLE_NAME)
@Table(name = User.TABLE_NAME)
public class User extends AuthorizationEntity implements UserDetails {

    @Transient
    public static final String TABLE_NAME = "User";

    @Column(name = "userName", length = 30)
    private String userName;
    @Column(name = "password", length = 150)
    private String password;

    @Convert(converter = AuthorityConverter.class)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "authority", columnDefinition = "JSON")
    private Authority authority;
    @Column(name = "firstName", length = 100)
    private String firstName;
    @Column(name = "lastName", length = 100)
    private String lastName;
    @Column(name = "phoneNumber", length = 15)
    private String phoneNumber;
    @Column(name = "activationCode")
    private String activationCode;
    @Transient
    private String ip;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authority
                .getRoles()
                .stream()
                .map(role -> {
                    return new SimpleGrantedAuthority(role);
                })
                .collect(Collectors.toSet());
    }


    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userName;
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
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    public void addAuthorities(String... roles) {
        this.authority = new Authority(roles);
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getFirstName() {
        return this.firstName;
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
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
