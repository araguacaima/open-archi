package com.araguacaima.open_archi.persistence.diagrams.component;

import com.araguacaima.open_archi.persistence.diagrams.core.*;

public class Palette extends AbstractPalette {

    private static final ColorScheme DEFAULT_ELEMENT_COLOR_SCHEME_PRIMARY = new ColorScheme(ColorSchemeOption.PRIMARY, "#0000FF", "transparent", "#e5e5ff");
    private static final ColorScheme DEFAULT_ELEMENT_COLOR_SCHEME_SECONDARY = new ColorScheme(ColorSchemeOption.SECONDARY, "#e5e5ff", "transparent", "#0000FF");
    private static PaletteKind kind = PaletteKind.COMPONENT;

    public Palette() {
        PaletteItem element = new PaletteItem();
        Shape shape = new Shape();
        shape.addColorScheme(DEFAULT_ELEMENT_COLOR_SCHEME_PRIMARY);
        shape.addColorScheme(DEFAULT_ELEMENT_COLOR_SCHEME_SECONDARY);
        shape.setType(ElementKind.DEFAULT);
        element.setName("New Element");
        element.setShape(shape);
        element.setRank(0);
        element.setPrototype(false);
        addBasicElement(element);
    }

    @Override
    public PaletteKind getType() {
        return kind;
    }

}
