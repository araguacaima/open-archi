package com.araguacaima.open_archi.persistence.diagrams.gantt;

import com.araguacaima.open_archi.persistence.diagrams.core.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@PersistenceUnit(unitName = "open-archi")
@DiscriminatorValue(value = "GanttModel")
public class GanttModel extends ModelElement implements DiagramableElement<GanttModel> {

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Gantt_Model_Gantts",
            joinColumns = {@JoinColumn(name = "Gantt_Model_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Gantt_Id",
                    referencedColumnName = "Id")})
    private Set<Gantt> gantts = new LinkedHashSet<>();

    public GanttModel() {
        setKind(ElementKind.GANTT_MODEL);
    }

    public Set<Gantt> getGantts() {
        return gantts;
    }

    public void setGantts(Set<Gantt> gantts) {
        this.gantts.clear();
        this.gantts.addAll(gantts);
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
        GanttModel source1 = (GanttModel) source;
        Set<Gantt> gantts = source1.getGantts();
        Helper.fixCollection(gantts, this.gantts, keepMeta, suffix, clonedFrom, comparator);
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
        GanttModel source1 = (GanttModel) source;
        Set<Gantt> gantts = source1.getGantts();
        Helper.fixCollection(gantts, this.gantts, keepMeta, comparator);
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null) {
            return -1;
        }
        return Comparator.comparing(GanttModel::getKind)
                .thenComparing(GanttModel::getName)
                .compare(this, (GanttModel) o);
    }
}
