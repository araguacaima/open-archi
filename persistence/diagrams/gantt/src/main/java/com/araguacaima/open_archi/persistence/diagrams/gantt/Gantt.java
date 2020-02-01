package com.araguacaima.open_archi.persistence.diagrams.gantt;

import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import com.araguacaima.open_archi.persistence.diagrams.core.CompositeElement;
import com.araguacaima.open_archi.persistence.diagrams.core.ElementKind;
import com.araguacaima.open_archi.persistence.diagrams.core.Item;

import javax.persistence.*;
import java.util.Collection;
import java.util.Comparator;

@Entity
@PersistenceUnit(unitName = "open-archi")
public class Gantt extends Item {

    @Column
    private int diagramStart;

    @Column
    private int diagramEnd;

    public int getDiagramStart() {
        return diagramStart;
    }

    public void setDiagramStart(int start) {
        this.diagramStart = start;
    }

    public int getDiagramEnd() {
        return diagramEnd;
    }

    public void setDiagramEnd(int end) {
        this.diagramEnd = end;
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.GANTT;
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom) {
        override(source, keepMeta, suffix, clonedFrom, itemComparator);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom, Comparator comparator) {
        super.override(source, keepMeta, suffix, clonedFrom, comparator);
        Gantt source1 = (Gantt) source;
        this.setDiagramStart(source1.getDiagramStart());
        this.setDiagramEnd(source1.getDiagramEnd());
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
        Gantt source1 = (Gantt) source;
        if (source1.getDiagramStart() != 0) {
            this.setDiagramStart(source1.getDiagramStart());
        }
        if (source1.getDiagramEnd() != 0) {
            this.setDiagramEnd(source1.getDiagramEnd());
        }

    }

    @Override
    public boolean isIsGroup() {
        return false;
    }

    @Override
    public void setIsGroup(boolean container) {

    }
}
