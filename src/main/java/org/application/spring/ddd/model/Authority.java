package org.application.spring.ddd.model;

import java.util.List;

public class Authority {

    List<String> roles;

    public List<String> getRoles() {
        return roles;
    }

    public Authority(List<String> roles) {
        this.roles = roles;
    }

    public Authority(String... roles) {
        this.roles = List.of(roles);
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
