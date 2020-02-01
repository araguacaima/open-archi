package com.araguacaima.open_archi.persistence.diagrams.flowchart;

import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import com.araguacaima.open_archi.persistence.diagrams.core.CompositeElement;
import com.araguacaima.open_archi.persistence.diagrams.core.RelationshipKind;
import com.araguacaima.open_archi.persistence.diagrams.core.RelationshipType;

import javax.persistence.*;
import java.util.Comparator;

/**
 * A relationship between two classes.
 */
@Entity
@PersistenceUnit(unitName = "open-archi")
@DiscriminatorValue("FlowchartRelationship")
public class FlowchartRelationship extends com.araguacaima.open_archi.persistence.diagrams.core.Relationship {

    @Column
    @Enumerated(EnumType.STRING)
    private RelationshipType type;

    @Column
    @Enumerated(EnumType.STRING)
    private RelationshipKind kind = RelationshipKind.FLOWCHART_RELATIONSHIP;

    public FlowchartRelationship() {
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
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom, Comparator comparator) {
        super.override(source, keepMeta, suffix, clonedFrom, comparator);
        FlowchartRelationship source1 = (FlowchartRelationship) source;
        this.type = source1.getType();
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta, Comparator comparator) {

        if (((FlowchartRelationship) source).getType() != null) {
            super.copyNonEmpty(source, keepMeta, comparator);
            FlowchartRelationship source1 = (FlowchartRelationship) source;
            this.type = source1.getType();
        }

    }
}