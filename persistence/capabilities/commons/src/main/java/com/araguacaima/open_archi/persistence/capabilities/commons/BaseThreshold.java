package com.araguacaima.open_archi.persistence.capabilities.commons;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PersistenceUnit;

@Entity
@PersistenceUnit(unitName = "open-archi")
public class BaseThreshold extends BaseEntity {

    @Column(name = "Value")
    protected String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
