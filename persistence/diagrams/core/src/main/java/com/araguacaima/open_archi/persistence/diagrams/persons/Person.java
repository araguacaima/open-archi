package com.araguacaima.open_archi.persistence.diagrams.persons;

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

@PersistenceUnit(unitName = "open-archi")
@Entity
@Table(schema = "PERSONS", name = "Person")
@DynamicUpdate
public class Person extends BaseEntity {

    @Column
    private String lastNames;
    @Column
    private String names;

    public String getLastNames() {
        return lastNames;
    }

    public void setLastNames(String lastNames) {
        this.lastNames = lastNames;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
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
        this.names = ((Person) source).getNames();
        this.lastNames = ((Person) source).getLastNames();
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta, Comparator comparator) {

        if (((Person) source).getNames() != null) {
            this.names = ((Person) source).getNames();
        }
        if (((Person) source).getLastNames() != null) {
            this.lastNames = ((Person) source).getLastNames();
        }

    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null || !Person.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        return CompareToBuilder.reflectionCompare(this, o);
    }

}
