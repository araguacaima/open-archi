package com.araguacaima.open_archi.persistence.diagrams.architectural;

import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import com.araguacaima.open_archi.persistence.diagrams.core.CompositeElement;
import com.araguacaima.open_archi.persistence.diagrams.core.ElementKind;

import javax.persistence.*;
import java.util.Comparator;

/**
 * However you think about your users (as actors, roles, persons, etc),
 * people are the various human users of your system.
 * <p>
 * See <a href="https://structurizr.com/help/model#Consumer">Model - CONSUMER</a>
 * on the Structurizr website for more information.
 */
@Entity
@PersistenceUnit(unitName = "open-archi")
public class Consumer extends LeafStaticElement {

    @Column
    @Enumerated(EnumType.STRING)
    private Scope scope = Scope.Unspecified;

    public Consumer() {
        setKind(ElementKind.CONSUMER);
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    @Override
    public void override(BaseEntity source) {
        override(source, false, null, null);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta) {
        override(source, keepMeta, null, null);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom) {
        override(source, keepMeta, suffix, clonedFrom, itemComparator);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom, Comparator comparator) {
        super.override(source, keepMeta, suffix, clonedFrom, comparator);
        this.setScope(((Consumer) source).getScope());
    }

    @Override
    public void copyNonEmpty(BaseEntity source) {
        copyNonEmpty(source, false);
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta) {
        copyNonEmpty(source, keepMeta, itemComparator);
    }

    public void copyNonEmpty(BaseEntity source, boolean keepMeta, Comparator comparator) {
        Consumer source1 = (Consumer) source;
        if (source1.getScope() != null) {
            this.setScope(source1.getScope());
        }
    }


}