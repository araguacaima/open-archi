package com.araguacaima.open_archi.persistence.diagrams.bpm;

import com.araguacaima.open_archi.persistence.diagrams.core.*;

public class Palette extends AbstractPalette {


    private static PaletteKind kind = PaletteKind.BPM;

    public Palette() {
        PaletteItem box = new PaletteItem();
        box.setShape(new Shape(ElementKind.DEFAULT));
        box.setRank(0);
        box.setName("Activity");
        box.setKind(ElementKind.BPM);
        addBasicElement(box);
        PaletteItem start = new PaletteItem();
        start.setShape(new Shape(ElementKind.FLOWCHART_INITIATOR));
        start.setRank(1);
        start.setName("Start");
        start.setKind(ElementKind.BPM);
        addBasicElement(start);
        PaletteItem end = new PaletteItem();
        end.setShape(new Shape(ElementKind.FLOWCHART_FINISHER));
        end.setRank(2);
        end.setName("End");
        end.setKind(ElementKind.BPM);
        addBasicElement(end);
        PaletteItem diamond = new PaletteItem();
        diamond.setShape(new Shape(ElementKind.FLOWCHART_CONDITION));
        diamond.setRank(3);
        diamond.setName("?");
        diamond.setKind(ElementKind.BPM);
        addBasicElement(diamond);
        PaletteItem lane = new PaletteItem();
        lane.setShape(new Shape(ElementKind.LANE));
        lane.setRank(4);
        lane.setName("Lane");
        lane.setKind(ElementKind.BPM);
        addBasicElement(lane);
        PaletteItem pool = new PaletteItem();
        pool.setShape(new Shape(ElementKind.POOL));
        pool.setRank(5);
        pool.setName("Pool");
        pool.setKind(ElementKind.BPM);
        addBasicElement(pool);
    }

    @Override
    public PaletteKind getType() {
        return kind;
    }

}
