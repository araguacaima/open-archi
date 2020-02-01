package com.araguacaima.open_archi.persistence.diagrams.core;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Comparator;

@Entity
@PersistenceUnit(unitName = "open-archi")
@Table(name = "Connector", schema = "Diagrams")
@DynamicUpdate
public class Connector extends BaseEntity {

    @Column
    @Enumerated(EnumType.STRING)
    private ElementKind type;
    @Column
    private String stroke = "#333333";

    public ElementKind getType() {
        return type;
    }

    public void setType(ElementKind type) {
        this.type = type;
    }

    public String getStroke() {
        return stroke;
    }

    public void setStroke(String stroke) {
        this.stroke = stroke;
    }

    @Override
    public void override(BaseEntity source) {
        override(source, false, null, null, null);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta) {
        override(source, keepMeta, null, null, null);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom) {
        override(source, keepMeta, suffix, clonedFrom, null);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedBy, Comparator comparator) {
        Connector source1 = (Connector) source;
        this.setType(source1.getType());
        this.setStroke(source1.getStroke());
    }


    @Override
    public void copyNonEmpty(BaseEntity source) {
        copyNonEmpty(source, false);
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta) {
        copyNonEmpty(source, keepMeta, null);
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta, Comparator comparator) {
        Connector source1 = (Connector) source;
        if (source1.getType() != null) {
            this.setType(source1.getType());
        }
        if (source1.getStroke() != null) {
            this.setStroke(source1.getStroke());
        }
    }


    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null || !Connector.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        return CompareToBuilder.reflectionCompare(this, o);
    }
}
