package org.application.spring.ddd.model.entity;

import jakarta.persistence.*;

@Entity(name = File.TABLE_NAME)
@Table(name = File.TABLE_NAME)
public class File extends AppEntity {

    @Transient
    public static final String TABLE_NAME = "File";

    @Column(name = "type", length = 15)
    private String type;

    @Column(name = "name", length = 100)
    private String name;

    @Lob
    @Column(name = "content", columnDefinition = "LONGBLOB")
    private byte[] content;

    @Column(name = "owner_id", length = 26)
    private String ownerId;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

}
