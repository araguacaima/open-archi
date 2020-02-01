package com.araguacaima.open_archi.persistence.diagrams.core;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class StringToByteArray extends JsonDeserializer<byte[]> {

    @Override
    public byte[] deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        return (Base64.getEncoder().encode(jsonParser.getText().getBytes(StandardCharsets.UTF_8)));
    }
}