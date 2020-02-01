package com.araguacaima.open_archi.persistence.diagrams.architectural;

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
 * A system is the highest level of abstraction and describes something
 * that delivers value to its users, whether they are human or not. This includes
 * the system you are modelling, and the other systems upon
 * which your system depends.
 * <p>
 * See <a href="https://structurizr.com/help/model#System">Model - System System</a>
 * on the Structurizr website for more information.
 */
@Entity
@PersistenceUnit(unitName = "open-archi")
@NamedQueries({
        @NamedQuery(name = System.GET_ALL_SYSTEMS,
                query = "select a from System a"),
        @NamedQuery(name = System.GET_ALL_SYSTEMS_FROM_SYSTEM,
                query = "select a.systems from System a where a.id=:id"),
        @NamedQuery(name = System.GET_ALL_COMPONENTS_FROM_SYSTEM,
                query = "select a.components from System a where a.id=:id"),
        @NamedQuery(name = System.GET_ALL_CONTAINERS_FROM_SYSTEM,
                query = "select a.containers from System a where a.id=:id"),
        @NamedQuery(name = System.GET_CONTAINER,
                query = "select c from System a JOIN a.containers c where a.id=:id and c.id=:cid"),
        @NamedQuery(name = System.GET_SYSTEMS_USAGE_BY_ELEMENT_ID_LIST,
                query = "select s " +
                        "from System s " +
                        "   left join s.systems sys " +
                        "   left join s.containers con " +
                        "   left join s.components com " +
                        "where sys.id in :" + Item.ELEMENTS_USAGE_PARAM +
                        "   or con.id in :" + Item.ELEMENTS_USAGE_PARAM +
                        "   or com.id in :" + Item.ELEMENTS_USAGE_PARAM)})
public class System extends GroupStaticElement implements DiagramableElement<System> {

    public static final String GET_ALL_SYSTEMS_FROM_SYSTEM = "get.all.systems.from.system";
    public static final String GET_ALL_COMPONENTS_FROM_SYSTEM = "get.all.components.from.system";
    public static final String GET_ALL_SYSTEMS = "get.all.systems";
    public static final String GET_ALL_CONTAINERS_FROM_SYSTEM = "get.all.containers.from.system";
    public static final String GET_CONTAINER = "get.container";
    public static final String GET_SYSTEMS_USAGE_BY_ELEMENT_ID_LIST = "get.systems.usage.by.element.id.list";

    @Column
    @Enumerated(EnumType.STRING)
    private Scope scope = Scope.Unspecified;


    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "System_Systems",
            joinColumns = {@JoinColumn(name = "System_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Child_System_Id",
                    referencedColumnName = "Id")})
    private Set<System> systems = new LinkedHashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "System_Containers",
            joinColumns = {@JoinColumn(name = "System_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Container_Id",
                    referencedColumnName = "Id")})
    private Set<Container> containers = new LinkedHashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "System_Components",
            joinColumns = {@JoinColumn(name = "System_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Component_Id",
                    referencedColumnName = "Id")})
    private Set<Component> components = new LinkedHashSet<>();

    public System() {
        setKind(ElementKind.SYSTEM);
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Set<Container> getContainers() {
        return containers;
    }

    public void setContainers(Set<Container> containers) {
        this.containers = containers;
    }

    public Set<System> getSystems() {
        return systems;
    }

    public void setSystems(Set<System> systems) {
        this.systems = systems;
    }

    public Set<Component> getComponents() {
        return components;
    }

    public void setComponents(Set<Component> components) {
        this.components = components;
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
        System source1 = (System) source;
        this.setScope(source1.getScope());
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
        System source1 = (System) source;
        this.setScope(source1.getScope());
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
        return Comparator.comparing(System::getKind)
                .thenComparing(System::getName)
                .compare(this, (System) o);
    }
}
