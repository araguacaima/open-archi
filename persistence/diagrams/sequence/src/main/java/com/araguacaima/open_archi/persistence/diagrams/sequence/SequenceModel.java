package com.araguacaima.open_archi.persistence.diagrams.sequence;

import com.araguacaima.open_archi.persistence.diagrams.core.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.Set;

@Entity
@PersistenceUnit(unitName = "open-archi")
@DiscriminatorValue(value = "SequenceModel")
@NamedQueries(value = {
       /* @NamedQuery(
        name = Model.FIND_BY_NAME,
        query = "select m from Model m JOIN FETCH Item i where m.id = i.id and i.name = :" + Model.PARAM_NAME),*/
        @NamedQuery(name = SequenceModel.GET_MODELS_COUNT,
                query = "select count(a) from com.araguacaima.open_archi.persistence.diagrams.sequence.SequenceModel a"),
        @NamedQuery(
                name = SequenceModel.GET_ALL_SEQUENCE_MODELS,
                query = "select a from com.araguacaima.open_archi.persistence.diagrams.sequence.SequenceModel a")})
public class SequenceModel extends ModelElement implements DiagramableElement<SequenceModel> {

    public static final String FIND_BY_NAME = "SequenceModel.findByName";
    public static final String GET_MODELS_COUNT = "SequenceModel.getModelsCount";
    public static final String GET_ALL_SEQUENCE_MODELS = "SequenceModel.getAllModels";
    public static final String PARAM_NAME = "name";

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Sequence_Model_Sequences",
            joinColumns = {@JoinColumn(name = "Sequence_Model_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Sequence_Id",
                    referencedColumnName = "Id")})
    private Set<Sequence> sequences = new LinkedHashSet<>();

    public SequenceModel() {
        setKind(ElementKind.SEQUENCE_MODEL);
    }

    public Set<Sequence> getSequences() {
        return sequences;
    }

    public void setSequences(Set<Sequence> sequences) {
        this.sequences.clear();
        this.sequences.addAll(sequences);
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
        SequenceModel source1 = (SequenceModel) source;
        Set<Sequence> sequences = source1.getSequences();
        Helper.fixCollection(sequences, this.sequences, keepMeta, suffix, clonedFrom, comparator);
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
        SequenceModel source1 = (SequenceModel) source;
        Set<Sequence> sequences = source1.getSequences();
        Helper.fixCollection(sequences, this.sequences, keepMeta, comparator);
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null) {
            return -1;
        }
        return Comparator.comparing(SequenceModel::getKind)
                .thenComparing(SequenceModel::getName)
                .compare(this, (SequenceModel) o);
    }
}
