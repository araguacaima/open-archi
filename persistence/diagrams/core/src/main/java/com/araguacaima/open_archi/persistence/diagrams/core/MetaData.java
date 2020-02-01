package com.araguacaima.open_archi.persistence.diagrams.core;

import com.araguacaima.open_archi.persistence.diagrams.meta.View;
import com.araguacaima.open_archi.persistence.diagrams.persons.Responsible;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Comparator;
import java.util.Set;

@Entity
@PersistenceUnit(unitName = "open-archi")
@Table(name = "MetaData", schema = "Diagrams")
@DynamicUpdate
public class MetaData extends BaseEntity {

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    @JoinTable(schema = "Diagrams",
            name = "MetaData_Responsibles",
            joinColumns = {@JoinColumn(name = "MetaData_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Responsible_Id",
                    referencedColumnName = "Id")})
    private Set<Responsible> responsibles;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    @JoinTable(schema = "Diagrams",
            name = "MetaData_Collaborators",
            joinColumns = {@JoinColumn(name = "MetaData_Id",
                    referencedColumnName = "Id", table = "Responsible")},
            inverseJoinColumns = {@JoinColumn(name = "Collaborator_Id",
                    referencedColumnName = "Id")})
    private Set<Responsible> collaborators;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    @JoinTable(schema = "Diagrams",
            name = "MetaData_RelatedWith",
            joinColumns = {@JoinColumn(name = "MetaData_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "RelatedWith_Id",
                    referencedColumnName = "Id")})
    private Set<Taggable> relatedWith;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    @JoinTable(schema = "Diagrams",
            name = "MetaData_UsedIn",
            joinColumns = {@JoinColumn(name = "MetaData_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "UsedIn_Id",
                    referencedColumnName = "Id")})
    private Set<Taggable> usedIn;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    @JoinTable(schema = "Diagrams",
            name = "MetaData_Views",
            joinColumns = {@JoinColumn(name = "MetaData_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "View_Id",
                    referencedColumnName = "Id")})
    private Set<View> views;

    public Set<Responsible> getResponsibles() {
        return responsibles;
    }

    public void setResponsibles(Set<Responsible> responsibles) {
        this.responsibles.clear();
        this.responsibles.addAll(responsibles);
    }

    public Set<Responsible> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(Set<Responsible> collaborators) {
        this.collaborators.clear();
        this.collaborators.addAll(collaborators);
    }

    public Set<Taggable> getRelatedWith() {
        return relatedWith;
    }

    public void setRelatedWith(Set<Taggable> relatedWith) {
        this.relatedWith.clear();
        this.relatedWith.addAll(relatedWith);
    }

    public Set<Taggable> getUsedIn() {
        return usedIn;
    }

    public void setUsedIn(Set<Taggable> usedId) {
        this.usedIn.clear();
        this.usedIn.addAll(usedId);
    }

    public Set<View> getViews() {
        return views;
    }

    public void setViews(Set<View> views) {
        this.views.clear();
        this.views.addAll(views);
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
        override(source, keepMeta, suffix, clonedFrom, null);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedBy, Comparator comparator) {
        super.override(source, keepMeta, suffix, clonedBy, comparator);
        MetaData source1 = (MetaData) source;
        Set<Responsible> responsibles = source1.getResponsibles();
        Set<Responsible> collaborators = source1.getCollaborators();
        Set<Taggable> relatedWith = source1.getRelatedWith();
        Set<Taggable> usedIn = source1.getUsedIn();
        Set<View> views = source1.getViews();
        Helper.fixCollection(responsibles, this.responsibles, keepMeta, suffix, clonedBy, comparator);
        Helper.fixCollection(collaborators, this.collaborators, keepMeta, suffix, clonedBy, comparator);
        Helper.fixCollection(relatedWith, this.relatedWith, keepMeta, suffix, clonedBy, comparator);
        Helper.fixCollection(usedIn, this.usedIn, keepMeta, suffix, clonedBy, comparator);
        Helper.fixCollection(views, this.views, keepMeta, suffix, clonedBy, comparator);
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
        MetaData source1 = (MetaData) source;
        Set<Responsible> responsibles = source1.getResponsibles();
        Set<Responsible> collaborators = source1.getCollaborators();
        Set<Taggable> relatedWith = source1.getRelatedWith();
        Set<Taggable> usedIn = source1.getUsedIn();
        Set<View> views = source1.getViews();
        Helper.fixCollection(responsibles, this.responsibles, keepMeta, comparator);
        Helper.fixCollection(collaborators, this.collaborators, keepMeta, comparator);
        Helper.fixCollection(relatedWith, this.relatedWith, keepMeta, comparator);
        Helper.fixCollection(usedIn, this.usedIn, keepMeta, comparator);
        Helper.fixCollection(views, this.views, keepMeta, comparator);
    }


    @Override
    public int compareTo(Object o) {
        if (o == null || !MetaData.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        return CompareToBuilder.reflectionCompare(this, o);
    }
}
