package com.araguacaima.open_archi.persistence.diagrams.core;

import java.util.Comparator;

public interface ClonableAndOverridable {

    void override(BaseEntity source);

    void override(BaseEntity source, boolean keepMeta);

    void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom);

    void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom, Comparator comparator);

    void copyNonEmpty(BaseEntity source);

    void copyNonEmpty(BaseEntity source, boolean keepMeta);

    void copyNonEmpty(BaseEntity source, boolean keepMeta, Comparator comparator);
}
