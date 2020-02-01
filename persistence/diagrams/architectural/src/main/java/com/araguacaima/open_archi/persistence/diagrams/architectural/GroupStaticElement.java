package com.araguacaima.open_archi.persistence.diagrams.architectural;

import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import com.araguacaima.open_archi.persistence.diagrams.core.CompositeElement;

import javax.persistence.Entity;
import javax.persistence.PersistenceUnit;
import java.util.Collection;

/**
 * This is the superclass for model elements that describe the static structure
 * of a system, namely Person, System, Container and Component.
 */
@Entity
@PersistenceUnit(unitName = "open-archi")
public abstract class GroupStaticElement extends StaticElement {

    @Override
    public boolean isIsGroup() {
        return true;
    }

    @Override
    public void setIsGroup(boolean container) {

    }
}
