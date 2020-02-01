package com.araguacaima.open_archi.persistence.diagrams.architectural;

import com.araguacaima.open_archi.persistence.diagrams.core.*;
import com.araguacaima.open_archi.persistence.diagrams.core.specification.FixRank;
import org.apache.commons.collections4.IterableUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.ToIntFunction;

@Entity
@PersistenceUnit(unitName = "open-archi")
@NamedQueries({
        @NamedQuery(name = Layer.GET_ALL_LAYERS,
                query = "select l from Layer l"),
        @NamedQuery(name = Layer.GET_LAYER,
                query = "select l from Layer l where l.id=:lid"),
        @NamedQuery(name = Layer.GET_ALL_SYSTEMS_FROM_LAYER,
                query = "select a.systems from Layer a where a.id=:id"),
        @NamedQuery(name = Layer.GET_ALL_CONTAINERS_FROM_LAYER,
                query = "select a.containers from Layer a where a.id=:id"),
        @NamedQuery(name = Layer.GET_ALL_COMPONENTS_FROM_LAYER,
                query = "select a.components from Layer a where a.id=:id"),
        @NamedQuery(name = Layer.GET_LAYERS_USAGE_BY_ELEMENT_ID_LIST,
                query = "select l " +
                        "from Layer l " +
                        "   left join l.systems sys " +
                        "   left join l.containers con " +
                        "   left join l.components com " +
                        "where sys.id in :" + Item.ELEMENTS_USAGE_PARAM +
                        "   or con.id in :" + Item.ELEMENTS_USAGE_PARAM +
                        "   or com.id in :" + Item.ELEMENTS_USAGE_PARAM)})
public class Layer extends GroupStaticElement implements DiagramableElement<Layer> {

    public static final String GET_ALL_LAYERS = "get.all.layers";
    public static final String GET_LAYER = "get.layer";
    public static final String GET_LAYERS_USAGE_BY_ELEMENT_ID_LIST = "get.layers.usage.by.element.id.list";
    public static final String GET_ALL_SYSTEMS_FROM_LAYER = "get.all.systems.from.layer";
    public static final String GET_ALL_CONTAINERS_FROM_LAYER = "get.all.containers.from.layer";
    public static final String GET_ALL_COMPONENTS_FROM_LAYER = "get.all.components.from.layer";

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Layer_Systems",
            joinColumns = {@JoinColumn(name = "Layer_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "System_Id",
                    referencedColumnName = "Id")})
    private Set<System> systems = new LinkedHashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Layer_Containers",
            joinColumns = {@JoinColumn(name = "Layer_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Container_Id",
                    referencedColumnName = "Id")})
    private Set<Container> containers = new LinkedHashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Layer_Components",
            joinColumns = {@JoinColumn(name = "Layer_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Component_Id",
                    referencedColumnName = "Id")})
    private Set<Component> components = new LinkedHashSet<>();

    public Layer() {
        setKind(ElementKind.LAYER);
    }

    public Set<System> getSystems() {
        return systems;
    }

    public void setSystems(Set<System> systems) {
        this.systems.clear();
        this.systems.addAll(systems);
    }

    public Set<Container> getContainers() {
        return containers;
    }

    public void setContainers(Set<Container> containers) {
        this.containers.clear();
        this.containers.addAll(containers);
    }

    public Set<Component> getComponents() {
        return components;
    }

    public void setComponents(Set<Component> components) {
        this.components.clear();
        this.components.addAll(components);
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
        Set<System> systems = source1.getSystems();
        Set<Container> containers = source1.getContainers();
        Set<Component> components = source1.getComponents();
        Helper.fixCollection(systems, this.systems, keepMeta, suffix, clonedFrom, comparator);
        Helper.fixCollection(containers, this.containers, keepMeta, suffix, clonedFrom, comparator);
        Helper.fixCollection(components, this.components, keepMeta, suffix, clonedFrom, comparator);
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
        Set<System> systems = source1.getSystems();
        Set<Container> containers = source1.getContainers();
        Set<Component> components = source1.getComponents();
        Helper.fixCollection(systems, this.systems, keepMeta, comparator);
        Helper.fixCollection(containers, this.containers, keepMeta, comparator);
        Helper.fixCollection(components, this.components, keepMeta, comparator);
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null) {
            return -1;
        }
        Layer o1 = (Layer) o;
        int rank = Rank.compareFunc(o1).applyAsInt(this);
        if (rank == 0) {
            return 1;
        }
        return rank;
    }
}
