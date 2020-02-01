package com.araguacaima.open_archi.persistence.diagrams.core;

public class DefaultPalette extends AbstractPalette {

    private static final ColorScheme DEFAULT_ELEMENT_COLOR_SCHEME_PRIMARY = new ColorScheme(ColorSchemeOption.PRIMARY, "#0000FF", "transparent", "#e5e5ff");
    private static final ColorScheme DEFAULT_PERSON_COLOR_SCHEME_PRIMARY = new ColorScheme(ColorSchemeOption.PRIMARY, "#ED5656", "transparent", "#fdeeee");
    private static final ColorScheme DEFAULT_ELEMENT_COLOR_SCHEME_SECONDARY = new ColorScheme(ColorSchemeOption.SECONDARY, "#e5e5ff", "transparent", "#0000FF");
    private static final ColorScheme DEFAULT_PERSON_COLOR_SCHEME_SECONDARY = new ColorScheme(ColorSchemeOption.SECONDARY, "#fdeeee", "transparent", "#ED5656");
    private static PaletteKind kind = PaletteKind.DEFAULT;

    public DefaultPalette() {
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
        PaletteItem person = new PaletteItem();
        Shape shapePerson = new Shape();
        shapePerson.addColorScheme(DEFAULT_PERSON_COLOR_SCHEME_PRIMARY);
        shapePerson.addColorScheme(DEFAULT_PERSON_COLOR_SCHEME_SECONDARY);
        shapePerson.setType(ElementKind.PERSON);
        person.setName("Person");
        person.setShape(shapePerson);
        person.setKind(ElementKind.CONSUMER);
        person.setRank(1);
        person.setPrototype(false);
        addBasicElement(person);
    }

    @Override
    public PaletteKind getType() {
        return kind;
    }

}
