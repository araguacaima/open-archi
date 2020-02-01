package com.araguacaima.open_archi.persistence.diagrams.er;

import com.araguacaima.open_archi.persistence.diagrams.core.*;

public class Palette extends AbstractPalette {

    private static PaletteKind kind = PaletteKind.ENTITY_RELATIONSHIP;

    public Palette() {
        PaletteItem box = new PaletteItem();
        box.setShape(new Shape(ElementKind.DEFAULT));
        box.setRank(0);
        box.setName("Entity");
        box.setKind(ElementKind.ENTITY);
        addBasicElement(box);
        PaletteItem target = new PaletteItem();
        target.setShape(new Shape(ElementKind.DEFAULT));
        target.setRank(1);
        target.setName("Attribute");
        target.setKind(ElementKind.ATTRIBUTE);
        addBasicElement(target);
    }

    @Override
    public PaletteKind getType() {
        return kind;
    }

}