package com.araguacaima.open_archi.persistence.diagrams.bpm;

import com.araguacaima.open_archi.persistence.diagrams.core.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@PersistenceUnit(unitName = "open-archi")
@DiscriminatorValue(value = "BpmModel")
public class BpmModel extends ModelElement implements DiagramableElement<BpmModel> {

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Model_Pools",
            joinColumns = {@JoinColumn(name = "Bpm_Model_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Pool_Id",
                    referencedColumnName = "Id")})
    private Set<Pool> pools = new LinkedHashSet<>();

    public BpmModel() {
        setKind(ElementKind.BPM_MODEL);
    }

    public Set<Pool> getPools() {
        return pools;
    }

    public void setPools(Set<Pool> pools) {
        this.pools.clear();
        this.pools.addAll(pools);
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
        Set<Pool> pools = ((BpmModel) source).getPools();
        Helper.fixCollection(pools, this.pools, keepMeta, suffix, clonedFrom, comparator);
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
        Set<Pool> pools = ((BpmModel) source).getPools();
        Helper.fixCollection(pools, this.pools, keepMeta, comparator);
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null) {
            return -1;
        }
        return Comparator.comparing(BpmModel::getKind)
                .thenComparing(BpmModel::getName)
                .compare(this, (BpmModel) o);
    }
}
