package org.application.spring.ddd.dto;

public class FileDto {
    private String id;
    private String type;
    private String name;
    private String ownerId;

    public FileDto() {
    }

    public FileDto(String id, String type, String name, String ownerId) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.ownerId = ownerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    // getter ها
}

