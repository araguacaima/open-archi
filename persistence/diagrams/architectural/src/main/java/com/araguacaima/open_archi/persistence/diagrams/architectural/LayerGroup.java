package com.araguacaima.open_archi.persistence.diagrams.architectural;

import com.araguacaima.open_archi.persistence.diagrams.core.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@PersistenceUnit(unitName = "open-archi")
@NamedQueries({
        @NamedQuery(name = LayerGroup.GET_ALL_GROUPS,
                query = "select l from LayerGroup l"),
        @NamedQuery(name = LayerGroup.GET_GROUP,
                query = "select l from LayerGroup l where l.id=:lid"),
        @NamedQuery(name = LayerGroup.GET_ALL_SYSTEMS_FROM_GROUP,
                query = "select a.systems from LayerGroup a where a.id=:id"),
        @NamedQuery(name = LayerGroup.GET_ALL_CONTAINERS_FROM_GROUP,
                query = "select a.containers from LayerGroup a where a.id=:id"),
        @NamedQuery(name = LayerGroup.GET_ALL_COMPONENTS_FROM_GROUP,
                query = "select a.components from LayerGroup a where a.id=:id"),
        @NamedQuery(name = LayerGroup.GET_GROUPS_USAGE_BY_ELEMENT_ID_LIST,
                query = "select l " +
                        "from LayerGroup l " +
                        "   left join l.systems sys " +
                        "   left join l.containers con " +
                        "   left join l.components com " +
                        "where sys.id in :" + Item.ELEMENTS_USAGE_PARAM +
                        "   or con.id in :" + Item.ELEMENTS_USAGE_PARAM +
                        "   or com.id in :" + Item.ELEMENTS_USAGE_PARAM)})
public class LayerGroup extends GroupStaticElement implements DiagramableElement<LayerGroup> {

    public static final String GET_ALL_GROUPS = "get.all.layer.groups";
    public static final String GET_GROUP = "get.lyer.group";
    public static final String GET_GROUPS_USAGE_BY_ELEMENT_ID_LIST = "get.layer.groups.usage.by.element.id.list";
    public static final String GET_ALL_SYSTEMS_FROM_GROUP = "get.all.systems.from.group";
    public static final String GET_ALL_CONTAINERS_FROM_GROUP = "get.all.containers.from.group";
    public static final String GET_ALL_COMPONENTS_FROM_GROUP = "get.all.components.from.group";

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Layer_Group_Systems",
            joinColumns = {@JoinColumn(name = "Group_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "System_Id",
                    referencedColumnName = "Id")})
    private Set<System> systems = new LinkedHashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Layer_Group_Containers",
            joinColumns = {@JoinColumn(name = "Group_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Container_Id",
                    referencedColumnName = "Id")})
    private Set<Container> containers = new LinkedHashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Layer_Group_Components",
            joinColumns = {@JoinColumn(name = "Group_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Component_Id",
                    referencedColumnName = "Id")})
    private Set<Component> components = new LinkedHashSet<>();
    
    public LayerGroup() {
        setKind(ElementKind.GROUP);
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
        LayerGroup source1 = (LayerGroup) source;
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
        LayerGroup source1 = (LayerGroup) source;
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
        LayerGroup o1 = (LayerGroup) o;
        int rank = Rank.compareFunc(o1).applyAsInt(this);
        if (rank == 0) {
            return 1;
        }
        return rank;
    }
}
