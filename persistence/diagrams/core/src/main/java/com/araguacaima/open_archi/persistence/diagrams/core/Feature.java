package com.araguacaima.open_archi.persistence.diagrams.core;

import com.araguacaima.open_archi.persistence.diagrams.core.reliability.Constraint;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

/**
 * Represents a feature, such as a Java class or interface,
 * that is part of the implementation of a component.
 */

@Entity
@PersistenceUnit(unitName = "open-archi")
public class Feature extends Item {

    /**
     * the type of the feature ... Primary or Supporting
     */
    @Column
    @Enumerated(EnumType.STRING)
    private FeatureType type = FeatureType.Primary;

    /**
     * a URL; e.g. a reference to the feature in source code control
     */
    @Column
    private String url;

    /**
     * the visibility of the feature; e.g. public, package, private
     */
    @Column
    private String visibility;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Feature_Incoming_Constraint",
            joinColumns = {@JoinColumn(name = "Feature_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Incoming_Constraint_Id",
                    referencedColumnName = "Id")})
    private Set<Constraint> incomingConstraints;


    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Feature_Outgoing_Constraint",
            joinColumns = {@JoinColumn(name = "Feature_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Outgoing_Constraint_Id",
                    referencedColumnName = "Id")})
    private Set<Constraint> outgoingConstraints;

    public Feature() {
    }

    public FeatureType getType() {
        return type;
    }

    public void setType(FeatureType type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public Set<Constraint> getIncomingConstraints() {
        return incomingConstraints;
    }

    public void setIncomingConstraints(Set<Constraint> incomingConstraints) {
        this.incomingConstraints.clear();
        this.incomingConstraints.addAll(incomingConstraints);
    }

    public Set<Constraint> getOutgoingConstraints() {
        return outgoingConstraints;
    }

    public void setOutgoingConstraints(Set<Constraint> outgoingConstraints) {
        this.outgoingConstraints.clear();
        this.outgoingConstraints.addAll(outgoingConstraints);
    }

    @Override
    public ElementKind getKind() {
        return ElementKind.FEATURE;
    }

    public void override(Feature source, boolean keepMeta, String suffix, CompositeElement clonedFrom, Comparator comparator) {
        super.override(source, keepMeta, suffix, clonedFrom, comparator);
        this.setRole(source.getRole());
        this.setUrl(source.getUrl());
        this.setVisibility(source.getVisibility());
        this.setIncomingConstraints(source.getIncomingConstraints());
        this.setOutgoingConstraints(source.getOutgoingConstraints());
    }

    public void copyNonEmpty(Feature source, boolean keepMeta, Comparator comparator) {
        super.copyNonEmpty(source, keepMeta, comparator);
        if (source.getRole() != null) {
            this.setRole(source.getRole());
        }
        if (source.getUrl() != null) {
            this.setUrl(source.getUrl());
        }
        if (source.getVisibility() != null) {
            this.setVisibility(source.getVisibility());
        }
        if (source.getIncomingConstraints() != null && !source.getIncomingConstraints().isEmpty()) {
            this.setIncomingConstraints(source.getIncomingConstraints());
        }
        if (source.getOutgoingConstraints() != null && !source.getOutgoingConstraints().isEmpty()) {
            this.setOutgoingConstraints(source.getOutgoingConstraints());
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