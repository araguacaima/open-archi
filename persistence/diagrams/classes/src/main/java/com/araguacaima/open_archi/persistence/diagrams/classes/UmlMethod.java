package com.araguacaima.open_archi.persistence.diagrams.classes;

import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import com.araguacaima.open_archi.persistence.diagrams.core.CompositeElement;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Comparator;

@Entity
@PersistenceUnit(unitName = "open-archi")
@Table(name = "UmlMethod", schema = "Diagrams")
@DynamicUpdate
public class UmlMethod extends BaseEntity {
    @Column
    private String name;
    @Column
    private String type;
    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "UmlMethod_Parameters",
            joinColumns = {@JoinColumn(name = "UmlMethod_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Parameter_Id",
                    referencedColumnName = "Id")})
    private Collection<UmlParameter> parameters;
    @Column
    @Enumerated(EnumType.STRING)
    private Visibility visibility = Visibility.PACKAGE;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Collection<UmlParameter> getParameters() {
        return parameters;
    }

    public void setParameters(Collection<UmlParameter> parameters) {
        this.parameters = parameters;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
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
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom, Comparator comparator) {
        super.override(source, keepMeta, suffix, clonedFrom, comparator);
        UmlMethod source1 = (UmlMethod) source;
        this.name = source1.getName();
        this.type = source1.getType();
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
        UmlMethod source1 = (UmlMethod) source;
        if (source1.getName() != null) {
            this.name = source1.getName();
        }
        if (source1.getType() != null) {
            this.type = source1.getType();
        }
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null || !UmlMethod.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        return CompareToBuilder.reflectionCompare(this, o);
    }

}
