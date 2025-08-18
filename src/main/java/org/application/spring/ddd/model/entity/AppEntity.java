package org.application.spring.ddd.model.entity;


import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.Serializable;


@MappedSuperclass
public abstract class AppEntity implements Serializable {

    @PrePersist
    public void onPrePersist() {
        if (this.id == null) {
            this.id = new ULID().nextULID();
        }
        this.authorization = getHeader("authorization");
    }

    @PreUpdate
    public void onPreUpdate() {

    }

    @Id
    @Column(name = "id", length = 26)
    private String id;

    @Column(name = "authorization")
    private String authorization;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public static <T> T getHeader(String key) {
        RequestAttributes requestAttributes = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes());
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            //request.setAttribute("","");
            return (T) request.getHeader("authorization");
        }
        return null;
    }
}
