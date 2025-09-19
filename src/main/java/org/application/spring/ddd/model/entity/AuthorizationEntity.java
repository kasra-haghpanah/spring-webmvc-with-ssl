package org.application.spring.ddd.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public class AuthorizationEntity extends AppEntity {

    @Column(name = "authorization")
    private String authorization;

    public AuthorizationEntity() {
        try {
            this.authorization = AppEntity.getHeader("Authorization");
        } catch (Exception e) {
            //throw new RuntimeException(e);
        }

    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }
}
