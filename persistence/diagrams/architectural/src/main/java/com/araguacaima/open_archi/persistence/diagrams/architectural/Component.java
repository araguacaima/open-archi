package com.araguacaima.open_archi.persistence.diagrams.architectural;

import com.araguacaima.open_archi.persistence.diagrams.core.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Comparator;

/**
 * The word "component" is a hugely overloaded term in the system development
 * industry, but in this context a component is simply a grouping of related
 * functionality encapsulated behind a well-defined interface. If you're using a
 * language like Java or C#, the simplest way to think of a component is that
 * it's a collection of implementation classes behind an interface. Aspects such
 * as how those components are packaged (e.g. one component vs many components
 * per JAR file, DLL, shared library, etc) is a separate and orthogonal concern.
 */
@Entity
@PersistenceUnit(unitName = "open-archi")
@NamedQueries({
        @NamedQuery(name = Component.GET_ALL_COMPONENTS,
                query = "select a from Component a"),
        @NamedQuery(name = Component.GET_COMPONENTS_USAGE_BY_ELEMENT_ID_LIST,
                query = "select c from Component c where c.id in :" + Item.ELEMENTS_USAGE_PARAM)})
public class Component extends LeafStaticElement implements DiagramableElement<Component> {

    public static final String GET_ALL_COMPONENTS = "get.all.components";
    public static final String GET_COMPONENTS_USAGE_BY_ELEMENT_ID_LIST = "get.components.usage.by.element.id.list";

    @Column
    private String technology;
/*
    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "components")
    @JsonIgnoreProperties(value = "components")
    private Set<ArchitecturalModel> includedInModels = new LinkedHashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "components")
    @JsonIgnoreProperties(value = "components")
    private Set<Layer> includedInLayers = new LinkedHashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "components")
    @JsonIgnoreProperties(value = "components")
    private Set<System> includedInSystems = new LinkedHashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.ALL, mappedBy = "components")
    @JsonIgnoreProperties(value = "components")
    private Set<Container> includedInContainers = new LinkedHashSet<>();*/

    public Component() {
        setKind(ElementKind.COMPONENT);
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }
/*
    public Set<ArchitecturalModel> getIncludedInModels() {
        return includedInModels;
    }

    public void setIncludedInModels(Set<ArchitecturalModel> includedInModels) {
        this.includedInModels.clear();
        this.includedInModels.addAll(includedInModels);
    }

    public Set<Layer> getIncludedInLayers() {
        return includedInLayers;
    }

    public void setIncludedInLayers(Set<Layer> includedInLayers) {
        this.includedInLayers.clear();
        this.includedInLayers.addAll(includedInLayers);
    }

    public Set<System> getIncludedInSystems() {
        return includedInSystems;
    }

    public void setIncludedInSystems(Set<System> includedInSystems) {
        this.includedInSystems.clear();
        this.includedInSystems.addAll(includedInSystems);
    }

    public Set<Container> getIncludedInContainers() {
        return includedInContainers;
    }

    public void setIncludedInContainers(Set<Container> includedInContainers) {
        this.includedInContainers.clear();
        this.includedInContainers.addAll(includedInContainers);
    }*/

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
        Component source1 = (Component) source;
        this.setTechnology(source1.getTechnology());
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
        Component source1 = (Component) source;
        if (source1.getTechnology() != null) {
            this.setTechnology(source1.getTechnology());
        }
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null || !Component.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        return Comparator.comparing(Component::getKind)
                .thenComparing(Component::getName)
                .compare(this, (Component) o);
    }

}