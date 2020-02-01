package com.araguacaima.open_archi.persistence.diagrams.flowchart;

import com.araguacaima.open_archi.persistence.diagrams.core.*;

public class Palette extends AbstractPalette {

    private static PaletteKind kind = PaletteKind.FLOWCHART;

    public Palette() {
        PaletteItem box = new PaletteItem();
        box.setShape(new Shape(ElementKind.DEFAULT));
        box.setRank(0);
        box.setName("Element");
        box.setKind(ElementKind.FLOWCHART_MODEL);
        addBasicElement(box);
        PaletteItem start = new PaletteItem();
        start.setShape(new Shape(ElementKind.FLOWCHART_INITIATOR));
        start.setRank(1);
        start.setName("Start");
        start.setKind(ElementKind.FLOWCHART_MODEL);
        addBasicElement(start);
        PaletteItem end = new PaletteItem();
        end.setShape(new Shape(ElementKind.FLOWCHART_FINISHER));
        end.setRank(2);
        end.setName("End");
        end.setKind(ElementKind.FLOWCHART_MODEL);
        addBasicElement(end);
        PaletteItem diamond = new PaletteItem();
        diamond.setShape(new Shape(ElementKind.FLOWCHART_CONDITION));
        diamond.setRank(3);
        diamond.setName("?");
        diamond.setKind(ElementKind.FLOWCHART_MODEL);
        addBasicElement(diamond);
    }

    @Override
    public PaletteKind getType() {
        return kind;
    }

}
