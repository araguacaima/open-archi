package com.araguacaima.open_archi.persistence.capabilities.commons;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Set;

@Entity
@PersistenceUnit(unitName = "open-archi")
public class Metric extends NamedBaseEntity {

    @OneToOne
    private BaseThreshold baseThreshold;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Measurement_ThreadholdsIds",
            joinColumns = {@JoinColumn(name = "Measurement_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Threadhold_Id",
                    referencedColumnName = "Id")})
    private Set<Threshold> thresholds;


    public BaseThreshold getBaseThreshold() {
        return baseThreshold;
    }

    public void setBaseThreshold(BaseThreshold baseThreshold) {
        this.baseThreshold = baseThreshold;
    }

    public Set<Threshold> getThresholds() {
        return thresholds;
    }

    public void setThresholds(Set<Threshold> threadholds) {
        this.thresholds = threadholds;
    }
}
