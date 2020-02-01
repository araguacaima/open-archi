package com.araguacaima.open_archi.persistence.diagrams.architectural;

import com.araguacaima.open_archi.persistence.diagrams.core.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A container represents something that hosts code or data. A container is
 * something that needs to be running in order for the overall system
 * to work. In real terms, a container is something like a server-side web application,
 * a client-side web application, client-side desktop application, a mobile app,
 * a microservice, a database schema, a file system, etc.
 * <p>
 * A container is essentially a context or boundary inside which some code is executed
 * or some data is stored. And each container is a separately deployable thing.
 */
@Entity
@PersistenceUnit(unitName = "open-archi")
@NamedQueries({
        @NamedQuery(name = Container.GET_ALL_CONTAINERS,
                query = "select a from Container a"),
        @NamedQuery(name = Container.GET_CONTAINERS_USAGE_BY_ELEMENT_ID_LIST,
                query = "select c from Container c left join c.components co where co.id in :" + Item.ELEMENTS_USAGE_PARAM)})
public class Container extends GroupStaticElement implements DiagramableElement<Container> {

    public static final String GET_ALL_CONTAINERS = "get.all.containers";
    public static final String GET_ALL_COMPONENTS_FROM_CONTAINER = "get.all.components.from.container";
    public static final String GET_CONTAINERS_USAGE_BY_ELEMENT_ID_LIST = "get.containers.usage.by.element.id.list";

    @Column
    private String technology;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Container_Components",
            joinColumns = {@JoinColumn(name = "Container_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Component_Id",
                    referencedColumnName = "Id")})
    private Set<Component> components = new LinkedHashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Container_Containers",
            joinColumns = {@JoinColumn(name = "Container_Parent_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Container_Id",
                    referencedColumnName = "Id")})
    private Set<Container> containers = new LinkedHashSet<>();

    public Container() {
        setKind(ElementKind.CONTAINER);
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public Set<Component> getComponents() {
        return components;
    }

    public void setComponents(Set<Component> components) {
        this.components.clear();
        this.components.addAll(components);
    }

    public Set<Container> getContainers() {
        return containers;
    }

    public void setContainers(Set<Container> containers) {
        this.containers.clear();
        this.containers.addAll(containers);
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
        Container source1 = (Container) source;
        this.setTechnology(source1.getTechnology());
        Set<Container> containers = source1.getContainers();
        Set<Component> components = source1.getComponents();
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
        Container source1 = (Container) source;
        this.setTechnology(source1.getTechnology());
        Set<Container> containers = source1.getContainers();
        Set<Component> components = source1.getComponents();
        Helper.fixCollection(containers, this.containers, keepMeta, comparator);
        Helper.fixCollection(components, this.components, keepMeta, comparator);
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null || !Container.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        return Comparator.comparing(Container::getKind)
                .thenComparing(Container::getName)
                .compare(this, (Container) o);
    }
}
