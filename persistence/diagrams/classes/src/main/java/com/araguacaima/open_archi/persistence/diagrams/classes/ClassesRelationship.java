package com.araguacaima.open_archi.persistence.diagrams.classes;

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
@DiscriminatorValue("ClassesRelationship")
public class ClassesRelationship extends com.araguacaima.open_archi.persistence.diagrams.core.Relationship {

    @Column
    @Enumerated(EnumType.STRING)
    private RelationshipType type;

    @Column
    @Enumerated(EnumType.STRING)
    private RelationshipKind kind = RelationshipKind.UML_CLASS_RELATIONSHIP;

    public ClassesRelationship() {
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
        this.type = ((ClassesRelationship) source).getType();
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
        if (((ClassesRelationship) source).getType() != null) {
            this.type = ((ClassesRelationship) source).getType();
        }

    }
}