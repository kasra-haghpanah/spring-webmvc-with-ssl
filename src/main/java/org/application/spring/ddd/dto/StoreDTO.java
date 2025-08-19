package org.application.spring.ddd.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public class StoreDTO {

    public StoreDTO() {
    }

    @Length(min = 2, max = 100, message = "field.name")
    private String name;

    @Valid
    @NotEmpty(message = "personDTOS نباید خالی باشد")
    List<PersonDTO> personDTOS;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PersonDTO> getPersonDTOS() {
        return personDTOS;
    }

    public void setPersonDTOS(List<PersonDTO> personDTOS) {
        this.personDTOS = personDTOS;
    }
}
