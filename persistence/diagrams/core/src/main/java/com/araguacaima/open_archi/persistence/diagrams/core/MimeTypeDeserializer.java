package com.araguacaima.open_archi.persistence.diagrams.core;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class MimeTypeDeserializer extends JsonDeserializer<MimeType> {

    @Override
    public MimeType deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        return MimeType.findValue(jsonParser.getText());
    }
}