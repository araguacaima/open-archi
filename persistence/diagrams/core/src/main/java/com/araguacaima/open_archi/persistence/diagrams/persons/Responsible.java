package com.araguacaima.open_archi.persistence.diagrams.persons;

import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import com.araguacaima.open_archi.persistence.diagrams.core.CompositeElement;
import com.araguacaima.open_archi.persistence.diagrams.core.Connector;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Comparator;

@PersistenceUnit(unitName = "open-archi")
@Entity
@Table(schema = "PERSONS", name = "Responsible")
@DynamicUpdate
public class Responsible extends BaseEntity {

    @Column
    private String organizationUnit;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    private Person person;

    public String getOrganizationUnit() {
        return organizationUnit;
    }

    public void setOrganizationUnit(String organizationUnit) {
        this.organizationUnit = organizationUnit;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person Responsible) {
        this.person = Responsible;
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
        this.setPerson(((Responsible) source).getPerson());
        this.setOrganizationUnit(((Responsible) source).getOrganizationUnit());
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta, Comparator comparator) {

        if (((Responsible) source).getPerson() != null) {
            this.setPerson(((Responsible) source).getPerson());
        }
        if (((Responsible) source).getOrganizationUnit() != null && !((Responsible) source).getOrganizationUnit().isEmpty()) {
            this.setOrganizationUnit(((Responsible) source).getOrganizationUnit());
        }

    }


    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null || !Responsible.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        return CompareToBuilder.reflectionCompare(this, o);
    }

}
