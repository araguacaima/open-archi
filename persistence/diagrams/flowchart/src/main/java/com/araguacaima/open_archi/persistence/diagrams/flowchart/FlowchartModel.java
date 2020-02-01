package com.araguacaima.open_archi.persistence.diagrams.flowchart;

import com.araguacaima.open_archi.persistence.diagrams.core.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.Set;

@Entity
@PersistenceUnit(unitName = "open-archi")
@DiscriminatorValue(value = "FlowchartModel")
public class FlowchartModel extends ModelElement implements DiagramableElement<FlowchartModel> {

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Flowchart_Model_Flowcharts",
            joinColumns = {@JoinColumn(name = "Flowchart_Model_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Flowchart_Id",
                    referencedColumnName = "Id")})
    private Set<Initiator> initiators = new LinkedHashSet<>();


    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Flowchart_Model_Flowcharts",
            joinColumns = {@JoinColumn(name = "Flowchart_Model_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Flowchart_Id",
                    referencedColumnName = "Id")})
    private Set<Finisher> finishers = new LinkedHashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Flowchart_Model_Flowcharts",
            joinColumns = {@JoinColumn(name = "Flowchart_Model_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Flowchart_Id",
                    referencedColumnName = "Id")})
    private Set<Condition> conditions = new LinkedHashSet<>();

    public FlowchartModel() {
        setKind(ElementKind.FLOWCHART_MODEL);
    }

    public Set<Initiator> getInitiators() {
        return initiators;
    }

    public void setInitiators(Set<Initiator> inititators) {
        this.initiators.clear();
        this.initiators.addAll(inititators);
    }

    public Set<Finisher> getFinishers() {
        return finishers;
    }

    public void setFinishers(Set<Finisher> finishers) {
        this.finishers.clear();
        this.finishers.addAll(finishers);
    }

    public Set<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(Set<Condition> conditions) {
        this.conditions.clear();
        this.conditions.addAll(conditions);
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
        override(source, keepMeta, suffix, clonedFrom, null);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom, Comparator comparator) {
        super.override(source, keepMeta, suffix, clonedFrom, comparator);
        FlowchartModel source1 = (FlowchartModel) source;
        Set<Initiator> initiators = source1.getInitiators();
        Set<Finisher> finishers = source1.getFinishers();
        Set<Condition> conditions = source1.getConditions();
        Helper.fixCollection(initiators, this.initiators, keepMeta, suffix, clonedFrom, comparator);
        Helper.fixCollection(finishers, this.finishers, keepMeta, suffix, clonedFrom, comparator);
        Helper.fixCollection(conditions, this.conditions, keepMeta, suffix, clonedFrom, comparator);
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
        FlowchartModel source1 = (FlowchartModel) source;
        Set<Initiator> initiators = source1.getInitiators();
        Set<Finisher> finishers = source1.getFinishers();
        Set<Condition> conditions = source1.getConditions();
        Helper.fixCollection(initiators, this.initiators, keepMeta, comparator);
        Helper.fixCollection(finishers, this.finishers, keepMeta, comparator);
        Helper.fixCollection(conditions, this.conditions, keepMeta, comparator);
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null) {
            return -1;
        }
        return Comparator.comparing(FlowchartModel::getKind)
                .thenComparing(FlowchartModel::getName)
                .compare(this, (FlowchartModel) o);
    }
}
