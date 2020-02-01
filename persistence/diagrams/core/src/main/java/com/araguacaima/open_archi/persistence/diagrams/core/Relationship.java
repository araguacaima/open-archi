package com.araguacaima.open_archi.persistence.diagrams.core;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Objects;

/**
 * A relationship between two elements.
 */
@Entity
@PersistenceUnit(unitName = "open-archi")
public class Relationship extends Taggable {

    public static final Comparator<Relationship> relationshipComparator = Comparator.comparing(Relationship::getSource,
            Comparator.nullsFirst(CompositeElement::compareTo)).thenComparing(Relationship::getDestination,
            Comparator.nullsFirst(CompositeElement::compareTo));
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    private CompositeElement source;
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    private CompositeElement destination;
    @Column
    private String description;
    @Column
    private String sourcePort;
    @Column
    private String destinationPort;
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    private Connector connector;


    public Relationship() {
    }

    public CompositeElement getSource() {
        return source;
    }

    public void setSource(CompositeElement source) {
        this.source = source;
    }

    public CompositeElement getDestination() {
        return destination;
    }

    public void setDestination(CompositeElement destination) {
        this.destination = destination;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(String sourcePort) {
        this.sourcePort = sourcePort;
    }

    public String getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(String destinationPort) {
        this.destinationPort = destinationPort;
    }

    public Connector getConnector() {
        return connector;
    }

    public void setConnector(Connector connector) {
        this.connector = connector;
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
        if (clonedFrom != null) {
            this.setClonedFrom(clonedFrom);
        }
        CompositeElement source_ = ((Relationship) source).getSource();
        if (source_ != null) {
            CompositeElement compositeElement = new CompositeElement();
            compositeElement.override(source_);
            this.source = compositeElement;
        }
        CompositeElement destination_ = ((Relationship) source).getDestination();
        if (destination_ != null) {
            CompositeElement compositeElement = new CompositeElement();
            compositeElement.override(destination_);
            this.destination = compositeElement;
        }
        this.description = ((Relationship) source).getDescription();
        this.sourcePort = ((Relationship) source).getSourcePort();
        this.destinationPort = ((Relationship) source).getDestinationPort();
        this.connector = ((Relationship) source).getConnector();

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
        CompositeElement source_ = ((Relationship) source).getSource();
        if (source_ != null) {
            CompositeElement compositeElement = new CompositeElement();
            compositeElement.copyNonEmpty(source_);
            this.source = compositeElement;
        }
        CompositeElement destination_ = ((Relationship) source).getDestination();
        if (destination_ != null) {
            CompositeElement compositeElement = new CompositeElement();
            compositeElement.copyNonEmpty(destination_);
            this.destination = compositeElement;
        }
        if (((Relationship) source).getDescription() != null) {
            this.description = ((Relationship) source).getDescription();
        }
        if (((Relationship) source).getSourcePort() != null) {
            this.sourcePort = ((Relationship) source).getSourcePort();
        }
        if (((Relationship) source).getDestinationPort() != null) {
            this.destinationPort = ((Relationship) source).getDestinationPort();
        }
        if (((Relationship) source).getConnector() != null) {
            this.connector = ((Relationship) source).getConnector();
        }

    }

    @Override
    public boolean isIsGroup() {
        return false;
    }

    @Override
    public void setIsGroup(boolean container) {

    }

    @Override
    public boolean equals(Object o) {
        return relationshipComparator.compare(this, (Relationship) o) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSource(), getDestination(), getDescription(), getSourcePort(), getDestinationPort(), getConnector());
    }
}