package com.araguacaima.open_archi.persistence.capabilities.commons;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceUnit;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@MappedSuperclass
@PersistenceUnit(unitName = "open-archi")
public abstract class NamedBaseEntity extends BaseEntity{

    @Column(name = "Name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}