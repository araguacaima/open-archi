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
@Table(name = "Database", schema = "Diagrams")
@DynamicUpdate
public class Database extends BaseEntity implements MeasurableRange {
    public static Measurable DEFAULT_BIG = new Measurable(new Range(StorageUnit.Giga_Bytes.name(), 251, 500));
    public static Measurable DEFAULT_HUGE = new Measurable(new Range(StorageUnit.Giga_Bytes.name(), 501, null));
    public static Measurable DEFAULT_MEDIUM = new Measurable(new Range(StorageUnit.Giga_Bytes.name(), 101, 250));
    public static Measurable DEFAULT_MINIMAL = new Measurable(new Range(StorageUnit.Giga_Bytes.name(), 0, 50));
    public static Measurable DEFAULT_SMALL = new Measurable(new Range(StorageUnit.Giga_Bytes.name(), 51, 100));
    private static final Set<Measurable> RANGE = new HashSet<Measurable>() {{
        add(DEFAULT_BIG);
        add(DEFAULT_HUGE);
        add(DEFAULT_MEDIUM);
        add(DEFAULT_MINIMAL);
        add(DEFAULT_SMALL);
    }};
    @Column
    @Enumerated(EnumType.STRING)
    private DataBaseType type;
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    private Measurable value;

    public Database(DataBaseType type) {
        this.type = type;
    }

    public Database() {
    }

    public DataBaseType getType() {
        return type;
    }

    public void setType(DataBaseType type) {
        this.type = type;
    }

    public Measurable getValue() {
        return value;
    }

    public void setValue(Measurable value) {
        this.value = value;
    }

    @Override
    public Set<Measurable> getRanges() {
        return RANGE;
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
        this.setValue(((Database) source).getValue());
        this.setType(((Database) source).getType());
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta, Comparator comparator) {

        if (((Database) source).getValue() != null) {
            this.setValue(((Database) source).getValue());
        }
        if (((Database) source).getType() != null) {
            this.setType(((Database) source).getType());
        }

    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null || !Database.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        return CompareToBuilder.reflectionCompare(this, o);
    }
}
