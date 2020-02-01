package com.araguacaima.open_archi.persistence.diagrams.er;

import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import com.araguacaima.open_archi.persistence.diagrams.core.CompositeElement;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

@Entity
@PersistenceUnit(unitName = "open-archi")
@Table(name = "Attribute", schema = "Diagrams")
@DynamicUpdate
public class Attribute extends BaseEntity {
    @Column
    private String name;
    @Column
    private String type;
    @Column
    private boolean key;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
        Attribute source1 = (Attribute) source;
        this.setName(source1.getName());
        this.setType(((Attribute) source).getType());
        this.setKey(((Attribute) source).isKey());
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
        Attribute source1 = (Attribute) source;
        if (source1.getName() != null) {
            this.setName(source1.getName());
        }
        if (source1.getType() != null) {
            this.setType(source1.getType());
        }
        this.setKey(source1.isKey());
    }


    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null || !Attribute.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        return CompareToBuilder.reflectionCompare(this, o);
    }
}
