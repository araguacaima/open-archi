package com.araguacaima.open_archi.persistence.diagrams.architectural;

import com.araguacaima.open_archi.persistence.diagrams.core.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * An architecture model.
 */
@Entity
@PersistenceUnit(unitName = "open-archi")
@DiscriminatorValue(value = "ArchitectureModel")
@NamedQueries({
        @NamedQuery(name = ArchitecturalModel.GET_ALL_MODEL_PROTOTYPES,
                query = "select m from ArchitecturalModel m where m.prototype=true"),
        @NamedQuery(name = ArchitecturalModel.GET_ALL_RELATIONSHIPS,
                query = "select m.relationships from ArchitecturalModel m where m.id=:id"),
        @NamedQuery(name = ArchitecturalModel.GET_ALL_CONSUMERS_FOR_MODEL,
                query = "select m.consumers from ArchitecturalModel m where m.id=:id"),
        @NamedQuery(name = ArchitecturalModel.GET_CONSUMER_FOR_MODEL,
                query = "select c from ArchitecturalModel m JOIN m.consumers c where m.id=:id and c.id=:cid"),
        @NamedQuery(name = ArchitecturalModel.GET_ALL_SYSTEMS_FROM_MODEL,
                query = "select m.systems from ArchitecturalModel m where m.id=:id"),
        @NamedQuery(name = ArchitecturalModel.GET_ALL_LAYERS_FROM_MODEL,
                query = "select m.layers from ArchitecturalModel m where m.id=:id"),
        @NamedQuery(name = ArchitecturalModel.GET_ALL_MODELS_INCLUDING_LAYER,
                query = "select am from ArchitecturalModel am left join am.layers l where l.id=:layerId"),
        @NamedQuery(name = ArchitecturalModel.GET_SYSTEM,
                query = "select s from ArchitecturalModel m JOIN m.systems s where m.id=:id and s.id=:sid"),
        @NamedQuery(name = ArchitecturalModel.GET_MODELS_USAGE_BY_ELEMENT_ID_LIST,
                query = "select m " +
                        "from ArchitecturalModel m " +
                        "   left join m.layers lay " +
                        "   left join m.systems sys " +
                        "   left join m.containers con " +
                        "   left join m.components com " +
                        "where lay.id in :" + Item.ELEMENTS_USAGE_PARAM +
                        "   or sys.id in :" + Item.ELEMENTS_USAGE_PARAM +
                        "   or con.id in :" + Item.ELEMENTS_USAGE_PARAM +
                        "   or com.id in :" + Item.ELEMENTS_USAGE_PARAM)})
public class ArchitecturalModel extends ModelElement implements DiagramableElement<ArchitecturalModel> {

    public static final String GET_ALL_MODEL_PROTOTYPES = "get.all.model.prototypes";
    public static final String GET_ALL_RELATIONSHIPS = "get.all.relationships";
    public static final String GET_ALL_CONSUMERS_FOR_MODEL = "get.all.consumers.for.model";
    public static final String GET_CONSUMER_FOR_MODEL = "get.consumer.for.model";
    public static final String GET_ALL_LAYERS_FROM_MODEL = "get.all.layers.from.model";
    public static final String GET_ALL_SYSTEMS_FROM_MODEL = "get.all.systems.from.model";
    public static final String GET_SYSTEM = "get.system";
    public static final String GET_MODELS_USAGE_BY_ELEMENT_ID_LIST = "get.models.usage.by.element.id.list";
    public static final String GET_ALL_MODELS_INCLUDING_LAYER = "get.all.models.including.layer";

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Architecture_Model_Consumers",
            joinColumns = {@JoinColumn(name = "Architecture_Model_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Consumer_Id",
                    referencedColumnName = "Id")})
    private Set<Consumer> consumers = new LinkedHashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.REMOVE)
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
    private Set<System> systems = new LinkedHashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Architecture_Model_Containers",
            joinColumns = {@JoinColumn(name = "Architecture_Model_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Container_Id",
                    referencedColumnName = "Id")})
    private Set<Container> containers = new LinkedHashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Architecture_Model_Components",
            joinColumns = {@JoinColumn(name = "Architecture_Model_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Component_Id",
                    referencedColumnName = "Id")})
    private Set<Component> components = new LinkedHashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Architecture_Model_DeploymentNodes",
            joinColumns = {@JoinColumn(name = "Architecture_Model_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "DeploymentNode_Id",
                    referencedColumnName = "Id")})
    private Set<DeploymentNode> deploymentNodes = new LinkedHashSet<>();

    public ArchitecturalModel() {
        setKind(ElementKind.ARCHITECTURE_MODEL);
    }

    public Set<Layer> getLayers() {
        return layers;
    }

    public void setLayers(Set<Layer> layers) {
        this.layers.clear();
        this.layers.addAll(layers);
    }

    public Set<Consumer> getConsumers() {
        return consumers;
    }

    public void setConsumers(Set<Consumer> consumers) {
        this.consumers.clear();
        this.consumers.addAll(consumers);
    }

    public Set<System> getSystems() {
        return systems;
    }

    public void setSystems(Set<System> systems) {
        this.systems.clear();
        this.systems.addAll(systems);
    }

    public Set<DeploymentNode> getDeploymentNodes() {
        return deploymentNodes;
    }

    public void setDeploymentNodes(Set<DeploymentNode> deploymentNodes) {
        this.deploymentNodes.clear();
        this.deploymentNodes.addAll(deploymentNodes);
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
        ArchitecturalModel source1 = (ArchitecturalModel) source;
        Set<Consumer> consumers = source1.getConsumers();
        Set<Layer> layers = source1.getLayers();
        Set<System> systems = source1.getSystems();
        Set<Container> containers = source1.getContainers();
        Set<Component> components = source1.getComponents();
        Set<DeploymentNode> deploymentNodes = source1.getDeploymentNodes();
        Helper.fixCollection(consumers, this.consumers, keepMeta, suffix, clonedFrom, comparator);
        Helper.fixCollection(layers, this.layers, keepMeta, suffix, clonedFrom, comparator);
        Helper.fixCollection(systems, this.systems, keepMeta, suffix, clonedFrom, comparator);
        Helper.fixCollection(containers, this.containers, keepMeta, suffix, clonedFrom, comparator);
        Helper.fixCollection(components, this.components, keepMeta, suffix, clonedFrom, comparator);
        Helper.fixCollection(deploymentNodes, this.deploymentNodes, keepMeta, suffix, clonedFrom, comparator);
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
        ArchitecturalModel source1 = (ArchitecturalModel) source;
        Set<Consumer> consumers = source1.getConsumers();
        Set<Layer> layers = source1.getLayers();
        Set<System> systems = source1.getSystems();
        Set<Container> containers = source1.getContainers();
        Set<Component> components = source1.getComponents();
        Set<DeploymentNode> deploymentNodes = source1.getDeploymentNodes();
        Helper.fixCollection(consumers, this.consumers, keepMeta, comparator);
        Helper.fixCollection(layers, this.layers, keepMeta, comparator);
        Helper.fixCollection(systems, this.systems, keepMeta, comparator);
        Helper.fixCollection(containers, this.containers, keepMeta, comparator);
        Helper.fixCollection(components, this.components, keepMeta, comparator);
        Helper.fixCollection(deploymentNodes, this.deploymentNodes, keepMeta, comparator);
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null) {
            return -1;
        }
        return Comparator.comparing(ArchitecturalModel::getKind)
                .thenComparing(ArchitecturalModel::getName)
                .compare(this, (ArchitecturalModel) o);
    }
}