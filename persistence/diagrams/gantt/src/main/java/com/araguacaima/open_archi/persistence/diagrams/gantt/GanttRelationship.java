package com.araguacaima.open_archi.persistence.diagrams.gantt;

import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import com.araguacaima.open_archi.persistence.diagrams.core.CompositeElement;
import com.araguacaima.open_archi.persistence.diagrams.core.RelationshipKind;
import com.araguacaima.open_archi.persistence.diagrams.core.RelationshipType;

import javax.persistence.*;
import java.util.Comparator;

/**
 * A relationship between two Gantt activities.
 */

@Entity
@PersistenceUnit(unitName = "open-archi")
@DiscriminatorValue(value = "GanttRelationship")
public class GanttRelationship extends com.araguacaima.open_archi.persistence.diagrams.core.Relationship {

    @Column
    @Enumerated(EnumType.STRING)
    private RelationshipType type;


    @Column
    @Enumerated(EnumType.STRING)
    private RelationshipKind kind = RelationshipKind.GANTT_RELATIONSHIP;

    public GanttRelationship() {
    }

    public RelationshipType getType() {
        return type;
    }

    public void setType(RelationshipType type) {
        this.type = type;
    }

    public RelationshipKind getKind() {
        return kind;
    }

    public void setKind(RelationshipKind kind) {
        this.kind = kind;
    }

    @Override
    public void override(BaseEntity source) {
        override(source, false, null, null);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta) {
        override(source, keepMeta, null, null);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom) {
        override(source, keepMeta, suffix, clonedFrom, relationshipComparator);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom, Comparator comparator) {
        super.override(source, keepMeta, suffix, clonedFrom, comparator);
        this.type = ((GanttRelationship) source).getType();
    }

    @Override
    public void copyNonEmpty(BaseEntity source) {
        copyNonEmpty(source, false);
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta) {
        copyNonEmpty(source, keepMeta, relationshipComparator);
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta, Comparator comparator) {
        if (((GanttRelationship) source).getType() != null) {
            super.copyNonEmpty(source, keepMeta, comparator);
            this.type = ((GanttRelationship) source).getType();
        }

    }
}