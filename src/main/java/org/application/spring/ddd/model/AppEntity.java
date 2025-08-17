package org.application.spring.ddd.model;


import com.vladmihalcea.hibernate.type.array.IntArrayType;
import com.vladmihalcea.hibernate.type.array.LongArrayType;
import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import com.vladmihalcea.hibernate.type.json.JsonBlobType;
import de.huxhorn.sulky.ulid.ULID;
import jakarta.persistence.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.Serializable;

import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;


@TypeDefs({
        @TypeDef(name = "string-array", typeClass = StringArrayType.class),
        @TypeDef(name = "int-array", typeClass = IntArrayType.class),
        @TypeDef(name = "long-array", typeClass = LongArrayType.class),
        @TypeDef(name = "json", typeClass = JsonBlobType.class),
        @TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
})
@MappedSuperclass
public abstract class AppEntity implements Serializable {

    @PrePersist
    public void onPrePersist() {
        if (this.id == null) {
            this.id = new ULID().nextULID();
        }

        RequestAttributes requestAttributes = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes());
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            //request.setAttribute("","");
            if (request != null) {
                this.authorization = request.getHeader("authorization");
            }
        }

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
}
