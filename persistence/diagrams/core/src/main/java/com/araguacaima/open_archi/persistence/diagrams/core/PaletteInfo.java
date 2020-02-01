package com.araguacaima.open_archi.persistence.diagrams.core;

public abstract class PaletteInfo {

    private String id;
    private String name;
    private ElementKind kind;

    public PaletteInfo() {

    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ElementKind getKind() {
        return kind;
    }

    public void setKind(ElementKind kind) {
        this.kind = kind;
    }

}
