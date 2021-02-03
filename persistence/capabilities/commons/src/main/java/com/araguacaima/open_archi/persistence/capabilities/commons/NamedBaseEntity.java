package com.araguacaima.open_archi.persistence.capabilities.commons;

import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceUnit;

@MappedSuperclass
@PersistenceUnit(unitName = "open-archi")
public abstract class NamedBaseEntity extends BaseEntity {

    @Column(name = "Name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Object o) {
        if (o == null) return 1;
        return this.name.compareTo(((NamedBaseEntity) o).getName());
    }
}