package com.araguacaima.open_archi.persistence.diagrams.core;

public interface DiagramableElement<T> extends Comparable {

    ElementKind getKind();

    void setKind(ElementKind kind);

    String getName();

    void setName(String name);

}
