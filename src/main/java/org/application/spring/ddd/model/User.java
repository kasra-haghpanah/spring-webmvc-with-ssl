package org.application.spring.ddd.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
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

    //@Type(value = JsonBinaryType.class)
    @JdbcTypeCode(SqlTypes.JSON)
    private Authority authority;
    @Column(name = "firstName", length = 100)
    private String firstName;
    @Column(name = "lastName", length = 100)
    private String lastName;
    @Column(name = "activationCode")
    private String activationCode;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authority
                .roles
                .stream()
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

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    public void setAuthority(String... roles) {
        this.authority = new Authority(roles);
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
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
