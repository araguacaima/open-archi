package com.araguacaima.open_archi.persistence.diagrams.core.reliability;

import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import com.araguacaima.open_archi.persistence.diagrams.core.CompositeElement;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Comparator;

@Entity
@PersistenceUnit(unitName = "open-archi")
@Table(schema = "Diagrams", name = "Measurable")
@DynamicUpdate
public class Measurable extends BaseEntity {

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    private Range range;

    @Column
    private double value;

    public Measurable(Range range) {
        this.range = range;
    }

    public Measurable() {
    }

    public Range getRange() {
        return range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public void override(BaseEntity source) {
        override(source, false, null, null, null);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta) {
        override(source, keepMeta, null, null, null);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedBy, Comparator comparator) {
        this.setValue(((Measurable) source).getValue());
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta, Comparator comparator) {

        if (((Measurable) source).getValue() != 0) {
            this.setValue(((Measurable) source).getValue());
        }

    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null || !Measurable.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        return CompareToBuilder.reflectionCompare(this, o);
    }
}
