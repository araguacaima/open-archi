package com.araguacaima.open_archi.persistence.diagrams.core;

import java.util.Set;

public interface Palettable {

    PaletteKind getType();

    Set<PaletteItem> getBasicElements();

    void addBasicElement(PaletteItem element);

    Set<PaletteItem> getGeneralElements();

    void addGeneralElement(PaletteItem element);
}
