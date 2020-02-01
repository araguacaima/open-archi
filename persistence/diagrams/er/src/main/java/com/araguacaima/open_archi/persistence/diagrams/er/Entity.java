package com.araguacaima.open_archi.persistence.diagrams.er;

import com.araguacaima.open_archi.persistence.diagrams.core.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

@javax.persistence.Entity
@PersistenceUnit(unitName = "open-archi")
public class Entity extends Element {

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Entity_Attributes",
            joinColumns = {@JoinColumn(name = "Entity_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Attribute_Id",
                    referencedColumnName = "Id")})
    private Collection<Attribute> attributes;

    @Override
    public ElementKind getKind() {
        return ElementKind.ENTITY;
    }

    public Collection<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Collection<Attribute> attributes) {
        this.attributes = attributes;
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom) {
        override(source, keepMeta, suffix, clonedFrom, itemComparator);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom, Comparator comparator) {
        super.override(source, keepMeta, suffix, clonedFrom, comparator);
        Entity source1 = (Entity) source;
        Set<Attribute> attributes = new LinkedHashSet<>(source1.getAttributes());
        Helper.fixCollection(attributes, new LinkedHashSet<>(this.attributes), keepMeta, suffix, clonedFrom, comparator);
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
        Entity source1 = (Entity) source;
        Set<Attribute> attributes = new LinkedHashSet<>(source1.getAttributes());
        Helper.fixCollection(attributes, new LinkedHashSet<>(this.attributes), keepMeta, comparator);
    }

    @Override
    public boolean isIsGroup() {
        return true;
    }

    @Override
    public void setIsGroup(boolean container) {

    }
}
