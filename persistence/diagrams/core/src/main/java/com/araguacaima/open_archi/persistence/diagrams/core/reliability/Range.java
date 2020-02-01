package com.araguacaima.open_archi.persistence.diagrams.core.reliability;

import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import com.araguacaima.open_archi.persistence.diagrams.core.CompositeElement;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Comparator;

import static com.araguacaima.open_archi.persistence.diagrams.core.Item.itemComparator;

@Entity
@PersistenceUnit(unitName = "open-archi")
@Table(schema = "Diagrams", name = "Range")
@DynamicUpdate
public class Range extends BaseEntity {

    @Column
    private String unit;

    @Column
    private Integer lower;

    @Column
    private Integer upper;

    public Range(String unit, Integer lower, Integer upper) {
        this.unit = unit;
        this.lower = lower;
        this.upper = upper;
    }

    public Range() {
    }

    public Integer getLower() {
        return lower;
    }

    public void setLower(Integer lower) {
        this.lower = lower;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getUpper() {
        return upper;
    }

    public void setUpper(Integer upper) {
        this.upper = upper;
    }

    @Override
    public void override(BaseEntity source) {
        override(source, false, null, null, itemComparator);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta) {
        override(source, keepMeta, null, null, itemComparator);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedBy, Comparator comparator) {
        Range source1 = (Range) source;
        this.setLower(source1.getLower());
        this.setUnit(source1.getUnit());
        this.setUpper(source1.getUpper());
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta, Comparator comparator) {
        if (((Range) source).getLower() != null) {
            this.setLower(((Range) source).getLower());
        }
        if (((Range) source).getUnit() != null) {
            this.setUnit(((Range) source).getUnit());
        }
        if (((Range) source).getUpper() != null) {
            this.setUpper(((Range) source).getUpper());
        }
    }


    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null || !Range.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        return CompareToBuilder.reflectionCompare(this, o);
    }
}
