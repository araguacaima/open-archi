package com.araguacaima.open_archi.persistence.diagrams.core;

public class ColorSchemeWrapper {
    public static ColorScheme clone(ColorScheme colorScheme) {
        ColorScheme colorScheme1 = new ColorScheme();
        colorScheme1.setId(colorScheme.getId());
        colorScheme1.setMeta(colorScheme.getMeta());
        colorScheme1.setFillColor(colorScheme.getFillColor());
        colorScheme1.setName(colorScheme.getName());
        colorScheme1.setStrokeColor(colorScheme.getStrokeColor());
        colorScheme1.setTextColor(colorScheme.getTextColor());
        colorScheme1.setKey(colorScheme.getKey());
        return colorScheme1;
    }
}
