package com.araguacaima.open_archi.persistence.diagrams.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PaletteItem extends PaletteInfo implements Comparable<PaletteItem> {

    private int rank;

    private String description;

    private Shape shape;

    private Set<ConnectTrigger> canBeConnectedFrom;

    private Set<ConnectTrigger> canBeConnectedTo;

    private boolean prototype;

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Set<ConnectTrigger> getCanBeConnectedFrom() {
        return canBeConnectedFrom;
    }

    public void setCanBeConnectedFrom(Set<ConnectTrigger> canBeConnectedFrom) {
        this.canBeConnectedFrom.clear();
        this.canBeConnectedFrom.addAll(canBeConnectedFrom);
    }

    public Set<ConnectTrigger> getCanBeConnectedTo() {
        return canBeConnectedTo;
    }

    public void setCanBeConnectedTo(Set<ConnectTrigger> canBeConnectedTo) {
        this.canBeConnectedTo.clear();
        this.canBeConnectedTo.addAll(canBeConnectedTo);
    }

    public boolean isPrototype() {
        return prototype;
    }

    public void setPrototype(boolean prototype) {
        this.prototype = prototype;
    }

    @Override
    public int compareTo(PaletteItem o) {
        if (o == null) {
            return 0;
        }
        if (o.getKind() == null || o.getShape() == null) {
            return -1001;
        }
        ElementKind shapeType = o.getShape().getType();
        if (shapeType == null) {
            return 0;
        } else if (shapeType.equals(ElementKind.DEFAULT)) {
            return -1000;
        } else if (shapeType.equals(ElementKind.CONSUMER)) {
            return -999;
        } else if (shapeType.equals(ElementKind.ARCHITECTURE_MODEL)) {
            return -998;
        } else if (shapeType.equals(ElementKind.LAYER)) {
            return -997;
        } else if (shapeType.equals(ElementKind.SYSTEM)) {
            return -996;
        } else if (shapeType.equals(ElementKind.CONTAINER)) {
            return -995;
        } else if (shapeType.equals(ElementKind.COMPONENT)) {
            return -994;
        } else if (shapeType.equals(this.getKind())) {
            return o.getRank() - this.getRank();
        }
        return 1;
    }
}
