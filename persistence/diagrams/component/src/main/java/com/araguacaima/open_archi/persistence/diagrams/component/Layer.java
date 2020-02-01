package com.araguacaima.open_archi.persistence.diagrams.component;

import com.araguacaima.open_archi.persistence.diagrams.core.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity(name = "ComponentModelLayer")
@DiscriminatorValue(value = "ComponentModelLayer")
@PersistenceUnit(unitName = "open-archi")
@NamedQueries({
        @NamedQuery(name = Layer.GET_ALL_LAYERS,
                query = "select l from com.araguacaima.open_archi.persistence.diagrams.component.Layer l"),
        @NamedQuery(name = Layer.GET_LAYER,
                query = "select l from com.araguacaima.open_archi.persistence.diagrams.component.Layer l where l.id=:lid"),
        @NamedQuery(name = Layer.GET_ALL_GROUPS_FROM_LAYER,
                query = "select a.groups from com.araguacaima.open_archi.persistence.diagrams.component.Layer a where a.id=:id"),
        @NamedQuery(name = Layer.GET_ALL_ELEMENTS_FROM_LAYER,
                query = "select a.elements from com.araguacaima.open_archi.persistence.diagrams.component.Layer a where a.id=:id"),
        @NamedQuery(name = Layer.GET_LAYERS_USAGE_BY_ELEMENT_ID_LIST,
                query = "select l " +
                        "from com.araguacaima.open_archi.persistence.diagrams.component.Layer l " +
                        "   left join l.groups sys " +
                        "   left join l.elements com " +
                        "where sys.id in :" + Item.ELEMENTS_USAGE_PARAM +
                        "   or com.id in :" + Item.ELEMENTS_USAGE_PARAM)})
public class Layer extends ComponentGroupStaticElement implements DiagramableElement<Layer> {

    public static final String GET_ALL_LAYERS = "get.all.component.model.layers";
    public static final String GET_LAYER = "get.component.model.layer";
    public static final String GET_LAYERS_USAGE_BY_ELEMENT_ID_LIST = "get.layers.usage.by.component.element.id.list";
    public static final String GET_ALL_GROUPS_FROM_LAYER = "get.all.groups.from.layer";
    public static final String GET_ALL_ELEMENTS_FROM_LAYER = "get.all.elements.from.layer";

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Layer_Systems",
            joinColumns = {@JoinColumn(name = "Layer_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "System_Id",
                    referencedColumnName = "Id")})
    private Set<Group> groups = new LinkedHashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Layer_Components",
            joinColumns = {@JoinColumn(name = "Layer_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Component_Id",
                    referencedColumnName = "Id")})
    private Set<ComponentElement> elements = new LinkedHashSet<>();

    public Layer() {
        setKind(ElementKind.LAYER);
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
        Layer source1 = (Layer) source;
        Set<Group> groups = source1.getGroups();
        Set<ComponentElement> elements = source1.getElements();
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
        Layer source1 = (Layer) source;
        Set<Group> groups = source1.getGroups();
        Set<ComponentElement> elements = source1.getElements();
        Helper.fixCollection(groups, this.groups, keepMeta, comparator);
        Helper.fixCollection(elements, this.elements, keepMeta, comparator);
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null) {
            return -1;
        }
        return Comparator.comparing(Layer::getKind)
                .thenComparing(Layer::getName)
                .compare(this, (Layer) o);
    }
}
