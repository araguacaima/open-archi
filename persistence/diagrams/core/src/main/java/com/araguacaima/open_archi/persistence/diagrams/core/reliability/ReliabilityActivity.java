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
@Table(name = "Activity", schema = "Diagrams")
@DynamicUpdate
public class ReliabilityActivity extends BaseEntity implements MeasurableRange {

    public static Measurable DEFAULT_BIG = new Measurable(new Range(Requests.REQUESTS_PER_MONTH.name(),
            4000001,
            20000000));
    public static Measurable DEFAULT_HUGE = new Measurable(new Range(Requests.REQUESTS_PER_MONTH.name(),
            20000001,
            80000000));
    public static Measurable DEFAULT_MEDIUM = new Measurable(new Range(Requests.REQUESTS_PER_SECOND.name(),
            600001,
            4000000));
    public static Measurable DEFAULT_MINIMAL = new Measurable(new Range(Requests.REQUESTS_PER_SECOND.name(),
            0,
            120000));
    public static Measurable DEFAULT_SMALL = new Measurable(new Range(Requests.REQUESTS_PER_SECOND.name(),
            120001,
            600000));
    private static Set<Measurable> RANGES = new HashSet<Measurable>() {{
        add(DEFAULT_BIG);
        add(DEFAULT_HUGE);
        add(DEFAULT_MEDIUM);
        add(DEFAULT_MINIMAL);
        add(DEFAULT_SMALL);
    }};
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    private Measurable value;

    public ReliabilityActivity() {
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
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom, Comparator comparator) {
        this.setValue(((ReliabilityActivity) source).getValue());
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta, Comparator comparator) {

        if (((ReliabilityActivity) source).getValue() != null) {
            this.setValue(((ReliabilityActivity) source).getValue());
        }

    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null || !ReliabilityActivity.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        return CompareToBuilder.reflectionCompare(this, o);
    }
}
