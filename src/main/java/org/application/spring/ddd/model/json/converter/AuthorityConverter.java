package org.application.spring.ddd.model.json.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.application.spring.ddd.model.json.type.Authority;

@Converter(autoApply = true)
public class AuthorityConverter implements AttributeConverter<Authority,String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Authority authority) {

        try {
            return objectMapper.writeValueAsString(authority);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Authority convertToEntityAttribute(String s) {
        try {
            return objectMapper.readValue(s,Authority.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}

