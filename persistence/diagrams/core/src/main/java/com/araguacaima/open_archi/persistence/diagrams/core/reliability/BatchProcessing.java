package com.araguacaima.open_archi.persistence.diagrams.core.reliability;

import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@PersistenceUnit(unitName = "open-archi")
@Table(name = "BatchProcessing", schema = "Diagrams")
@DynamicUpdate
public class BatchProcessing extends BaseEntity {

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null || !BatchProcessing.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        return CompareToBuilder.reflectionCompare(this, o);
    }

}
