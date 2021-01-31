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
public abstract class BaseEntity {

    @Id
    @NotNull
    @Column(name = "Id")
    @org.hibernate.annotations.Type(type = "objectid")
    protected String id;

    public BaseEntity() {
        this.id = generateId();
    }

    @JsonIgnore
    private String generateId() {
        return UUID.randomUUID().toString();
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void resetId() {
        this.id = generateId();
    }


}