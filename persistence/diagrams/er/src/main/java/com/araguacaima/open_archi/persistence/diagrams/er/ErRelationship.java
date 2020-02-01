package com.araguacaima.open_archi.persistence.diagrams.er;

import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import com.araguacaima.open_archi.persistence.diagrams.core.CompositeElement;
import com.araguacaima.open_archi.persistence.diagrams.core.RelationshipKind;
import com.araguacaima.open_archi.persistence.diagrams.core.RelationshipType;

import javax.persistence.*;
import java.util.Comparator;

/**
 * A relationship between two entities.
 */

@javax.persistence.Entity
@PersistenceUnit(unitName = "open-archi")
@DiscriminatorValue("ErRelationship")
public class ErRelationship extends com.araguacaima.open_archi.persistence.diagrams.core.Relationship {

    @Column
    @Enumerated(EnumType.STRING)
    private RelationshipType type;
    @Column
    private String sourceText;
    @Column
    private String destinationText;

    @Column
    @Enumerated(EnumType.STRING)
    private RelationshipKind kind = RelationshipKind.ENTITY_RELATIONSHIP_RELATIONSHIP;

    public ErRelationship() {
    }

    public RelationshipType getType() {
        return type;
    }

    public void setType(RelationshipType type) {
        this.type = type;
    }

    public String getSourceText() {
        return sourceText;
    }

    public void setSourceText(String sourceText) {
        this.sourceText = sourceText;
    }

    public String getDestinationText() {
        return destinationText;
    }

    public void setDestinationText(String destinationText) {
        this.destinationText = destinationText;
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
        this.type = ((ErRelationship) source).getType();
        this.sourceText = ((ErRelationship) source).getSourceText();
        this.destinationText = ((ErRelationship) source).getDestinationText();
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
        super.copyNonEmpty(source, keepMeta, comparator);
        ErRelationship source1 = (ErRelationship) source;
        if (source1.getType() != null) {
            this.type = source1.getType();
        }
        if (source1.getSourceText() != null) {
            this.sourceText = source1.getSourceText();
        }
        if (source1.getDestinationText() != null) {
            this.destinationText = source1.getDestinationText();
        }

    }

}