package com.araguacaima.open_archi.persistence.diagrams.core;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Converter
public class Base64RawConverter implements AttributeConverter<byte[], String> {

    public String convertToDatabaseColumn(byte[] value) {
        if (value == null) {
            return null;
        }

        return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
    }

    public byte[] convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }
        return (Base64.getEncoder().encode(value.getBytes(StandardCharsets.UTF_8)));
    }
}