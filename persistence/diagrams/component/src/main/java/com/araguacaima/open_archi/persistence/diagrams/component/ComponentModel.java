package com.araguacaima.open_archi.persistence.diagrams.component;

import com.araguacaima.open_archi.persistence.diagrams.core.*;
import org.apache.commons.collections4.IterableUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * An architecture model.
 */
@Entity
@PersistenceUnit(unitName = "open-archi")
@DiscriminatorValue(value = "ComponentModel")
@NamedQueries({
        @NamedQuery(name = ComponentModel.GET_ALL_MODEL_PROTOTYPES,
                query = "select m from com.araguacaima.open_archi.persistence.diagrams.component.ComponentModel m where m.prototype=true"),
        @NamedQuery(name = ComponentModel.GET_ALL_RELATIONSHIPS,
                query = "select m.relationships from com.araguacaima.open_archi.persistence.diagrams.component.ComponentModel m where m.id=:id"),
        @NamedQuery(name = ComponentModel.GET_ALL_GROUPS_FROM_MODEL,
                query = "select m.groups from com.araguacaima.open_archi.persistence.diagrams.component.ComponentModel m where m.id=:id"),
        @NamedQuery(name = ComponentModel.GET_ALL_LAYERS_FROM_MODEL,
                query = "select m.layers from com.araguacaima.open_archi.persistence.diagrams.component.ComponentModel m where m.id=:id"),
        @NamedQuery(name = ComponentModel.GET_MODELS_USAGE_BY_ELEMENT_ID_LIST,
                query = "select m " +
                        "from com.araguacaima.open_archi.persistence.diagrams.component.ComponentModel m " +
                        "   left join m.layers lay " +
                        "   left join m.groups grp " +
                        "   left join m.elements ele " +
                        "where lay.id in :" + Item.ELEMENTS_USAGE_PARAM +
                        "   or grp.id in :" + Item.ELEMENTS_USAGE_PARAM +
                        "   or ele.id in :" + Item.ELEMENTS_USAGE_PARAM)})
public class ComponentModel extends ModelElement implements DiagramableElement<ComponentModel> {

    public static final String GET_ALL_MODEL_PROTOTYPES = "get.all.component.model.prototypes";
    public static final String GET_ALL_RELATIONSHIPS = "get.all.component.model.relationships";
    public static final String GET_ALL_LAYERS_FROM_MODEL = "get.all.layers.from.component.model";
    public static final String GET_ALL_GROUPS_FROM_MODEL = "get.all.groups.from.model";
    public static final String GET_MODELS_USAGE_BY_ELEMENT_ID_LIST = "get.component.models.usage.by.element.id.list";

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Architecture_Model_Layers",
            joinColumns = {@JoinColumn(name = "Architecture_Model_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Layer_Id",
                    referencedColumnName = "Id")})
    private Set<Layer> layers = new LinkedHashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Architecture_Model_Systems",
            joinColumns = {@JoinColumn(name = "Architecture_Model_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "System_Id",
                    referencedColumnName = "Id")})
    private Set<Group> groups = new LinkedHashSet<>();


    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Architecture_Model_Components",
            joinColumns = {@JoinColumn(name = "Architecture_Model_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Component_Id",
                    referencedColumnName = "Id")})
    private Set<ComponentElement> elements = new LinkedHashSet<>();

    public ComponentModel() {
        setKind(ElementKind.COMPONENT_MODEL);
    }

    public Set<Layer> getLayers() {
        return layers;
    }

    public void setLayers(Set<Layer> layers) {
        this.layers.clear();
        this.layers.addAll(layers);
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups.clear();
        this.groups.addAll(groups);
    }

    public Set<ComponentElement> getElements() {
        return elements;
    }

    public void setElements(Set<ComponentElement> elements) {
        this.elements.clear();
        this.elements.addAll(elements);
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
        ComponentModel source1 = (ComponentModel) source;
        Set<Layer> layers = source1.getLayers();
        Set<Group> groups = source1.getGroups();
        Set<ComponentElement> elements = source1.getElements();
        Helper.fixCollection(layers, this.layers, keepMeta, suffix, clonedFrom, comparator);
        Helper.fixCollection(groups, this.groups, keepMeta, suffix, clonedFrom, comparator);
        Helper.fixCollection(elements, this.elements, keepMeta, suffix, clonedFrom, comparator);
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
        ComponentModel source1 = (ComponentModel) source;
        Set<Layer> layers = source1.getLayers();
        Set<Group> groups = source1.getGroups();
        Set<ComponentElement> elements = source1.getElements();
        Helper.fixCollection(layers, this.layers, keepMeta, comparator);
        Helper.fixCollection(groups, this.groups, keepMeta, comparator);
        Helper.fixCollection(elements, this.elements, keepMeta, comparator);
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null) {
            return -1;
        }
        return Comparator.comparing(ComponentModel::getKind)
                .thenComparing(ComponentModel::getName)
                .compare(this, (ComponentModel) o);
    }
}