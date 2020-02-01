package com.araguacaima.open_archi.persistence.diagrams.core.reliability;

import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@PersistenceUnit(unitName = "open-archi")
@Table(name = "BulkProcessing", schema = "Diagrams")
@DynamicUpdate
public class BulkProcessing extends BaseEntity {

    @Override
    public int compareTo(@NotNull Object o) {
        return 0;
    }
}
