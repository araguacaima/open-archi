package com.araguacaima.open_archi.persistence.diagrams.core.reliability;

import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import com.araguacaima.open_archi.persistence.diagrams.core.CompositeElement;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

@Entity
@PersistenceUnit(unitName = "open-archi")
@Table(name = "Concurrency", schema = "Diagrams")
@DynamicUpdate
public class Concurrency extends BaseEntity implements MeasurableRange {
    public static Measurable HIGH = new Measurable(new Range(ConcurrencyUnit
            .NUMBER_OF_CONCURRENT_CONSUMERS.name(),
            0,
            10));
    public static Measurable LOW = new Measurable(new Range(ConcurrencyUnit.NUMBER_OF_CONCURRENT_CONSUMERS.name(),
            11,
            30));
    public static Measurable MEDIUM = new Measurable(new Range(ConcurrencyUnit
            .NUMBER_OF_CONCURRENT_CONSUMERS.name(),
            31,
            null));
    private static final Set<Measurable> RANGES = new HashSet<Measurable>() {{
        add(HIGH);
        add(LOW);
        add(MEDIUM);
    }};
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    private Measurable value;

    public Concurrency() {
    }

    public Measurable getValue() {
        return value;
    }

    public void setValue(Measurable value) {
        this.value = value;
    }

    @Override
    public Set<Measurable> getRanges() {
        return RANGES;
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
        this.setValue(((Concurrency) source).getValue());
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta, Comparator comparator) {

        if (((Concurrency) source).getValue() != null) {
            this.setValue(((Concurrency) source).getValue());
        }

    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null || !Concurrency.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        return CompareToBuilder.reflectionCompare(this, o);
    }
}
