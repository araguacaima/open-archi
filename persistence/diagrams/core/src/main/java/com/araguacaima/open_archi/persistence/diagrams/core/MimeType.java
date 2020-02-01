package com.araguacaima.open_archi.persistence.diagrams.core;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MimeType {

    BMP("image/bmp"),
    VND("image/vnd.dwg"),
    GIF("image/gif"),
    JPEG("image/jpeg"),
    PNG("image/png"),
    SVG("image/svg+xml");

    private final String value;

    MimeType(String value) {
        this.value = value;
    }

    public static MimeType findValue(String value) throws IllegalArgumentException {
        MimeType result = null;
        try {
            result = MimeType.valueOf(value);
        } catch (IllegalArgumentException ignored) {
            for (MimeType mimeType : MimeType.values()) {
                String accionEnumStr = mimeType.getValue();
                if (accionEnumStr.equalsIgnoreCase(value)) {
                    return mimeType;
                }
            }
        }
        if (result == null) {
            throw new IllegalArgumentException("No enum const " + MimeType.class.getName() + "." + value);
        }
        return result;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
