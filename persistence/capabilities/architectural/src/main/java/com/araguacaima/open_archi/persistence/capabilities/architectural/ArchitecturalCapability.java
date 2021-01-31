package com.araguacaima.open_archi.persistence.capabilities.architectural;


import com.araguacaima.open_archi.persistence.capabilities.commons.Capability;
import com.araguacaima.open_archi.persistence.diagrams.architectural.ArchitecturalModel;
import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Capabilities associated to an architecture model.
 */
@Entity
@PersistenceUnit(unitName = "open-archi")
public class ArchitecturalCapability extends BaseEntity {

    @Id
    @NotNull
    @Column(name = "Id")
    @org.hibernate.annotations.Type(type = "objectid")
    private String id;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    private ArchitecturalModel model;

    private Set<Capability> capabilities;


    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public ArchitecturalModel getModel() {
        return model;
    }

    public void setModel(ArchitecturalModel model) {
        this.model = model;
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}