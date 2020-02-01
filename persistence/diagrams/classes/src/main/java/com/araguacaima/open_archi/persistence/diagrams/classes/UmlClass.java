package com.araguacaima.open_archi.persistence.diagrams.classes;

import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import com.araguacaima.open_archi.persistence.diagrams.core.CompositeElement;
import com.araguacaima.open_archi.persistence.diagrams.core.ElementKind;
import com.araguacaima.open_archi.persistence.diagrams.core.Helper;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.*;

@Entity
@PersistenceUnit(unitName = "open-archi")
public class UmlClass extends UmlItem {

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "UmlClass_Fields",
            joinColumns = {@JoinColumn(name = "UmlClass_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Field_Id",
                    referencedColumnName = "Id")})
    private Map<String, UmlField> fields = new HashMap<>();
    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "UmlClass_Methods",
            joinColumns = {@JoinColumn(name = "UmlClass_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Method_Id",
                    referencedColumnName = "Id")})
    private Map<String, UmlMethod> methods = new HashMap<>();

    public UmlClass() {
        setKind(ElementKind.CLASS);
    }


    public Map<String, UmlField> getFields() {
        return fields;
    }

    public void setFields(Map<String, UmlField> fields) {
        this.fields = fields;
    }

    public Map<String, UmlMethod> getMethods() {
        return methods;
    }

    public void setMethods(Map<String, UmlMethod> methods) {
        this.methods = methods;
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom) {
        override(source, keepMeta, suffix, clonedFrom, itemComparator);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom, Comparator comparator) {
        super.override(source, keepMeta, suffix, clonedFrom, comparator);
        UmlClass source1 = (UmlClass) source;
        Helper.fixMap(source1.getMethods(), this.getMethods());
        Helper.fixMap(source1.getFields(), this.getFields());
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
        UmlClass source1 = (UmlClass) source;
        if (source1.getFields() != null && !source1.getFields().isEmpty()) {
            Helper.fixMap(source1.getMethods(), this.getMethods());
        }
        if (source1.getMethods() != null && !source1.getMethods().isEmpty()) {
            Helper.fixMap(source1.getFields(), this.getFields());
        }
    }

}
