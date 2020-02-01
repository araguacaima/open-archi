package com.araguacaima.open_archi.persistence.diagrams.architectural;

import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import com.araguacaima.open_archi.persistence.diagrams.core.CompositeElement;
import com.araguacaima.open_archi.persistence.diagrams.core.RelationshipKind;
import com.araguacaima.open_archi.persistence.diagrams.core.RelationshipType;

import javax.persistence.*;
import java.util.Comparator;

/**
 * A relationship between two architectural elements.
 */
@Entity
@PersistenceUnit(unitName = "open-archi")
@DiscriminatorValue("ArchitectureRelationship")
public class ArchitecturalRelationship extends com.araguacaima.open_archi.persistence.diagrams.core.Relationship {

    @Column
    @Enumerated(EnumType.STRING)
    protected RelationshipKind kind = RelationshipKind.ARCHITECTURE_RELATIONSHIP;
    @Column
    private String technology;
    @Column
    @Enumerated(EnumType.STRING)
    private InteractionStyle interactionStyle = InteractionStyle.SYNCHRONOUS;
    @Column
    @Enumerated(EnumType.STRING)
    private RelationshipType type = RelationshipType.BIDIRECTIONAL;

    public ArchitecturalRelationship() {
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public InteractionStyle getInteractionStyle() {
        return interactionStyle;
    }

    public void setInteractionStyle(InteractionStyle interactionStyle) {
        this.interactionStyle = interactionStyle;
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
        this.technology = ((ArchitecturalRelationship) source).getTechnology();
        this.interactionStyle = ((ArchitecturalRelationship) source).getInteractionStyle();
        this.type = ((ArchitecturalRelationship) source).getType();
    }

    @Override
    public void copyNonEmpty(BaseEntity source) {
        copyNonEmpty(source, false);
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta) {
        copyNonEmpty(source, keepMeta, relationshipComparator);
    }

    public void copyNonEmpty(BaseEntity source, boolean keepMeta, Comparator comparator) {

        super.copyNonEmpty(source, keepMeta, comparator);
        if (((ArchitecturalRelationship) source).getTechnology() != null) {
            this.technology = ((ArchitecturalRelationship) source).getTechnology();
        }
        if (((ArchitecturalRelationship) source).getInteractionStyle() != null) {
            this.interactionStyle = ((ArchitecturalRelationship) source).getInteractionStyle();
        }
        if (((ArchitecturalRelationship) source).getType() != null) {
            this.type = ((ArchitecturalRelationship) source).getType();
        }

    }
}