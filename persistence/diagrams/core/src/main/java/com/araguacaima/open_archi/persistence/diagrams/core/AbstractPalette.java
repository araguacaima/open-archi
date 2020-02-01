package com.araguacaima.open_archi.persistence.diagrams.core;

import java.util.Set;
import java.util.TreeSet;

public abstract class AbstractPalette implements Palettable {

    private TreeSet<PaletteItem> basicElements = new TreeSet<>();
    private TreeSet<PaletteItem> generalElements = new TreeSet<>();

    @Override
    abstract public PaletteKind getType();

    @Override
    public Set<PaletteItem> getBasicElements() {
        return basicElements;
    }

    public void setBasicElements(TreeSet<PaletteItem> basicElements) {
        this.basicElements = basicElements;
    }

    @Override
    public void addBasicElement(PaletteItem element) {
        this.basicElements.add(element);
    }

    @Override
    public Set<PaletteItem> getGeneralElements() {
        return generalElements;
    }

    public void setGeneralElements(TreeSet<PaletteItem> generalElements) {
        this.generalElements = generalElements;
    }

    @Override
    public void addGeneralElement(PaletteItem element) {
        this.generalElements.add(element);
    }

}
