package org.application.spring.ddd.model.json.type;

import java.util.List;

public class Authority {

    List<String> roles;

    public Authority() {
    }

    public Authority(List<String> roles) {
        this.roles = roles;
    }

    public Authority(String... roles) {
        this.roles = List.of(roles);
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
