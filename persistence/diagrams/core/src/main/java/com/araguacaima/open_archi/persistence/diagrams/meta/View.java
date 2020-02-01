package com.araguacaima.open_archi.persistence.diagrams.meta;


import com.araguacaima.open_archi.persistence.commons.exceptions.EntityError;
import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import com.araguacaima.open_archi.persistence.diagrams.core.CompositeElement;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Comparator;

@Entity
@PersistenceUnit(unitName = "open-archi")
@Table(name = "View", schema = "Meta")
@DynamicUpdate
public class View extends BaseEntity {

    @Column
    private String parentId;

    @Column
    private boolean visible;

    @Column
    private String name;

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    public <T extends BaseEntity> T validateRequest() throws EntityError {
        return super.validateRequest();
        //Do nothing. All request are valid on this entity
    }

    @Override
    public <T extends BaseEntity> T validateCreation() {
        T result = super.validateCreation();
        if (name == null) {
            throw new EntityError(resourceBundle.getString(getCreationErrorMessageKey()));
        }
        return result;
    }

    @Override
    public <T extends BaseEntity> T validateModification() throws EntityError {
        T result = super.validateModification();
        if (name != null) {
            throw new EntityError(resourceBundle.getString(getModificationErrorMessageKey()));
        }
        return result;
    }

    @Override
    public void override(BaseEntity source) {
        override(source, false, null, null, null);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta) {
        override(source, keepMeta, null, null, null);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedBy, Comparator comparator) {
        View source_ = (View) source;
        this.setParentId(source_.getParentId());
        this.setVisible(source_.isVisible());
        this.setName(source_.getName());
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta, Comparator comparator) {
        View source_ = (View) source;
        if (source_.getParentId() != null) {
            this.setParentId(source_.getParentId());
        }
        this.setVisible(source_.isVisible());
        if (source_.getName() != null) {
            this.setName(source_.getName());
        }


    }


    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null || !View.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        return CompareToBuilder.reflectionCompare(this, o);
    }
}
