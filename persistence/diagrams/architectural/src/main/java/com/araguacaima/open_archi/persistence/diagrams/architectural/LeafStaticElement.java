package com.araguacaima.open_archi.persistence.diagrams.architectural;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PersistenceUnit;

/**
 * This is the superclass for model elements that describe the static structure
 * of a system, namely Person, System, Container and Component.
 */
@Entity
@PersistenceUnit(unitName = "open-archi")
@DiscriminatorValue("ArchitecturalLeafElement")
public abstract class LeafStaticElement extends StaticElement {

    @Override
    public boolean isIsGroup() {
        return false;
    }

    @Override
    public void setIsGroup(boolean container) {

    }

}
