package com.araguacaima.open_archi.persistence.capabilities.commons;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Set;

@Entity
@PersistenceUnit(unitName = "open-archi")
@NamedQueries({@NamedQuery(name = Capability.GET_ALL_CAPABILITIES,
        query = "select c " +
                "from com.araguacaima.open_archi.persistence.capabilities.commons.Capability c"),
        @NamedQuery(name = Capability.GET_ALL_DIMENSIONS,
                query = "select c.dimensions " +
                        "from com.araguacaima.open_archi.persistence.capabilities.commons.Capability c where c.id = :id")})
public class Capability extends NamedBaseEntity {
    public static final String GET_ALL_CAPABILITIES = "get.all.capabilities";
    public static final String GET_ALL_DIMENSIONS = "get.all.dimensions";
    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Capability_DimensionsIds",
            joinColumns = {@JoinColumn(name = "Capability_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Dimension_Id",
                    referencedColumnName = "Id")})
    private Set<Dimension> dimensions;

    public Set<Dimension> getDimensions() {
        return dimensions;
    }

    public void setDimensions(Set<Dimension> dimensions) {
        this.dimensions = dimensions;
    }

}
