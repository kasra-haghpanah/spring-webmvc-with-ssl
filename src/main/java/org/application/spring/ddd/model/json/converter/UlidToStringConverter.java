package org.application.spring.ddd.model.json.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import de.huxhorn.sulky.ulid.ULID;

@Converter(autoApply = true)
public class UlidToStringConverter implements AttributeConverter<ULID.Value, String> {

    private final ULID ulid = new ULID();

    @Override
    public String convertToDatabaseColumn(ULID.Value ulidValue) {
        return ulidValue != null ? ulidValue.toString() : null;
    }

    @Override
    public ULID.Value convertToEntityAttribute(String dbData) {
        return dbData != null ? ulid.parseULID(dbData) : null;
    }
}

