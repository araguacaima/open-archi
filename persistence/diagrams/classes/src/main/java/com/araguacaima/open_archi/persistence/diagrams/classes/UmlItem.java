package com.araguacaima.open_archi.persistence.diagrams.classes;

import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import com.araguacaima.open_archi.persistence.diagrams.core.CompositeElement;
import com.araguacaima.open_archi.persistence.diagrams.core.Item;

import javax.persistence.Entity;
import javax.persistence.PersistenceUnit;
import java.util.Collection;
import java.util.Comparator;

/**
 * This is the superclass for all model elements.
 */
@Entity
@PersistenceUnit(unitName = "open-archi")
public abstract class UmlItem extends Item {

    public UmlItem() {

    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom) {
        override(source, keepMeta, suffix, clonedFrom, itemComparator);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom, Comparator comparator) {
        super.override(source, keepMeta, suffix, clonedFrom, comparator);
    }

    @Override
    public void copyNonEmpty(BaseEntity source) {
        copyNonEmpty(source, false);
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta) {
        copyNonEmpty(source, keepMeta, itemComparator);
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta, Comparator comparator) {
        super.copyNonEmpty(source, keepMeta, comparator);
    }

    @Override
    public boolean isIsGroup() {
        return true;
    }

    @Override
    public void setIsGroup(boolean container) {

    }
}