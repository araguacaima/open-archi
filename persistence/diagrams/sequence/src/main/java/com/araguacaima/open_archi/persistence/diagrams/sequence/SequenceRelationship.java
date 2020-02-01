package com.araguacaima.open_archi.persistence.diagrams.sequence;

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
@DiscriminatorValue(value = "SequenceRelationship")
public class SequenceRelationship extends com.araguacaima.open_archi.persistence.diagrams.core.Relationship {

    @Column
    @Enumerated(EnumType.STRING)
    private RelationshipType type;

    @Column
    private int time;

    @Column
    @Enumerated(EnumType.STRING)
    private RelationshipKind kind = RelationshipKind.SEQUENCE_RELATIONSHIP;

    public SequenceRelationship() {
    }

    public RelationshipType getType() {
        return type;
    }

    public void setType(RelationshipType type) {
        this.type = type;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
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
        SequenceRelationship source1 = (SequenceRelationship) source;
        this.type = source1.getType();
        this.time = source1.getTime();
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
        super.copyNonEmpty(source, keepMeta, comparator);
        if (((SequenceRelationship) source).getType() != null) {
            this.type = ((SequenceRelationship) source).getType();
        }
        if (((SequenceRelationship) source).getTime() != 0) {
            this.time = ((SequenceRelationship) source).getTime();
        }

    }
}