package com.araguacaima.open_archi.persistence.diagrams.core;

import com.araguacaima.open_archi.persistence.commons.exceptions.EntityError;
import org.apache.commons.lang3.builder.CompareToBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@PersistenceUnit(unitName = "open-archi")
@Table(name = "ElementRole", schema = "Diagrams", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
@NamedQueries({@NamedQuery(name = ElementRole.GET_ALL_ROLES, query = "select a from ElementRole a "), @NamedQuery(name = ElementRole.GET_ROLE_BY_NAME, query = "select a from ElementRole a where a.name  = :name ")})
public class ElementRole extends BaseEntity {

    public static final String GET_ALL_ROLES = "get.all.roles";
    public static final String GET_ROLE_BY_NAME = "get.role.by.name";

    @Column
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public <T extends BaseEntity> T validateRequest() throws EntityError {
        return super.validateRequest();
    }

    @Override
    public <T extends BaseEntity> T validateCreation() throws EntityError {
        return super.validateCreation();
    }

    @Override
    public <T extends BaseEntity> T validateModification() throws EntityError {
        return super.validateModification();
    }

    @Override
    public <T extends BaseEntity> T validateReplacement() throws EntityError {
        return super.validateReplacement();
    }


    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null || !ElementRole.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        return CompareToBuilder.reflectionCompare(this, o);
    }
}
