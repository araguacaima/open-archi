package com.araguacaima.open_archi.persistence.diagrams.classes;

import com.araguacaima.open_archi.persistence.diagrams.core.*;

public class Palette extends AbstractPalette {

    private static PaletteKind kind = PaletteKind.UML_CLASS;

    public Palette() {
        PaletteItem class_ = new PaletteItem();
        class_.setShape(new Shape(ElementKind.DEFAULT));
        class_.setRank(0);
        class_.setName("Activity");
        class_.setKind(ElementKind.CLASS);
        addBasicElement(class_);
        PaletteItem field = new PaletteItem();
        field.setShape(new Shape(ElementKind.DEFAULT));
        field.setRank(1);
        field.setName("Actor");
        field.setKind(ElementKind.FIELD);
        addBasicElement(field);
        PaletteItem method = new PaletteItem();
        method.setShape(new Shape(ElementKind.DEFAULT));
        method.setRank(2);
        method.setName("Activity");
        method.setKind(ElementKind.CLASS);
        addBasicElement(method);
        PaletteItem parameter = new PaletteItem();
        parameter.setShape(new Shape(ElementKind.DEFAULT));
        parameter.setRank(3);
        parameter.setName("Actor");
        parameter.setKind(ElementKind.PARAMETER);
        addBasicElement(parameter);
    }

    @Override
    public PaletteKind getType() {
        return kind;
    }

}
