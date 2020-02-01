package com.araguacaima.open_archi.persistence.diagrams.core;

import java.util.Set;

public interface ComponentModelDiagramableElement<T> extends DiagramableElement<T> {

    Set<? extends Item> getIncludedInModels();

    Set<? extends Item> getIncludedInLayers();

    Set<? extends Item> getIncludedInGroups();

}
