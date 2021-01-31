package com.araguacaima.open_archi.persistence.capabilities.commons;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PersistenceUnit;

@Entity
@PersistenceUnit(unitName = "open-archi")
public class Threshold extends BaseThreshold {

    @Column(name = "OverridesBase")
    private boolean overridesBase;

    public boolean isOverridesBase() {
        return overridesBase;
    }

    public void setOverridesBase(boolean overridesBase) {
        this.overridesBase = overridesBase;
    }
}
