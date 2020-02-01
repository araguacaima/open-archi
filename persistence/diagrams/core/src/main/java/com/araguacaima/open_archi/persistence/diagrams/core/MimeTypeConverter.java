package com.araguacaima.open_archi.persistence.diagrams.core;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class MimeTypeConverter implements AttributeConverter<MimeType, String> {

    public String convertToDatabaseColumn(MimeType value) {
        if (value == null) {
            return null;
        }
        return value.getValue();
    }

    public MimeType convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }
        return MimeType.findValue(value);
    }
}