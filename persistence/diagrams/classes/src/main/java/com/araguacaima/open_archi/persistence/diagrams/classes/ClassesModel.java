package com.araguacaima.open_archi.persistence.diagrams.classes;

import com.araguacaima.open_archi.persistence.diagrams.core.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@PersistenceUnit(unitName = "open-archi")
@DiscriminatorValue(value = "ClassesModel")
public class ClassesModel extends ModelElement implements DiagramableElement<ClassesModel> {


    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Model_Classes",
            joinColumns = {@JoinColumn(name = "Classes_Model_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Class_Id",
                    referencedColumnName = "Id")})
    private Set<UmlClass> classes;

    public ClassesModel() {
        setKind(ElementKind.UML_CLASS_MODEL);
    }

    public Set<UmlClass> getClasses() {
        return classes;
    }

    public void setClasses(Set<UmlClass> classes) {
        this.classes.clear();
        this.classes.addAll(classes);
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
        override(source, keepMeta, suffix, clonedFrom, itemComparator);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom, Comparator comparator) {
        super.override(source, keepMeta, suffix, clonedFrom, comparator);
        ClassesModel source1 = (ClassesModel) source;
        Set<UmlClass> classes = source1.getClasses();
        Helper.fixCollection(classes, this.classes, keepMeta, suffix, clonedFrom, comparator);
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
        ClassesModel source1 = (ClassesModel) source;
        Set<UmlClass> classes = source1.getClasses();
        Helper.fixCollection(classes, this.classes, keepMeta, comparator);
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null) {
            return -1;
        }
        return Comparator.comparing(ClassesModel::getKind)
                .thenComparing(ClassesModel::getName)
                .compare(this, (ClassesModel) o);
    }
}
