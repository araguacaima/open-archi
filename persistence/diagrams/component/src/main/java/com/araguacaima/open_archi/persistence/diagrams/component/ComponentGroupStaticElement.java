package com.araguacaima.open_archi.persistence.diagrams.component;

import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import com.araguacaima.open_archi.persistence.diagrams.core.CompositeElement;
import com.araguacaima.open_archi.persistence.diagrams.core.ElementKind;

import javax.persistence.Entity;
import javax.persistence.PersistenceUnit;
import java.util.Comparator;

/**
 * This is the superclass for model elements that describe the static structure
 * of a system, namely Person, System, Container and Element.
 */
@Entity
@PersistenceUnit(unitName = "open-archi")
public abstract class ComponentGroupStaticElement extends ComponentStaticElement {
    private int columnSpan = 1;
    private int rowSpan = 1;

    public ComponentGroupStaticElement() {
        setKind(ElementKind.GROUP);
    }

    public int getColumnSpan() {
        return columnSpan;
    }

    public void setColumnSpan(int columnSpan) {
        this.columnSpan = columnSpan;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    @Override
    public boolean isIsGroup() {
        return true;
    }

    @Override
    public void setIsGroup(boolean container) {

    }


    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom) {
        override(source, keepMeta, suffix, clonedFrom, itemComparator);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom, Comparator comparator) {
        super.override(source, keepMeta, suffix, clonedFrom, comparator);
        ComponentGroupStaticElement source1 = (ComponentGroupStaticElement) source;
        this.columnSpan = source1.getColumnSpan();
        this.rowSpan = source1.getRowSpan();
    }

    @Override
    public void copyNonEmpty(BaseEntity source) {
        copyNonEmpty(source, false);
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta) {
        copyNonEmpty(source, keepMeta, itemComparator);
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta, Comparator comparator) {
        super.copyNonEmpty(source, keepMeta, comparator);
        ComponentGroupStaticElement source1 = (ComponentGroupStaticElement) source;
        this.columnSpan = source1.getColumnSpan();
        this.rowSpan = source1.getRowSpan();
    }
}
