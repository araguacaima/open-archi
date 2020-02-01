package com.araguacaima.open_archi.web;

import com.araguacaima.open_archi.persistence.diagrams.architectural.System;
import com.araguacaima.open_archi.persistence.diagrams.architectural.*;
import com.araguacaima.open_archi.persistence.diagrams.component.ComponentElement;
import com.araguacaima.open_archi.persistence.diagrams.component.ComponentModel;
import com.araguacaima.open_archi.persistence.diagrams.component.Group;
import com.araguacaima.open_archi.persistence.diagrams.core.*;
import com.araguacaima.open_archi.persistence.diagrams.core.reliability.Constraint;
import com.araguacaima.open_archi.persistence.diagrams.meta.History;
import com.araguacaima.open_archi.persistence.diagrams.meta.MetaInfo;
import com.araguacaima.open_archi.persistence.diagrams.meta.Version;
import com.araguacaima.open_archi.persistence.diagrams.meta.View;
import com.araguacaima.open_archi.persistence.diagrams.persons.Person;
import com.araguacaima.open_archi.persistence.diagrams.persons.Responsible;
import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import org.apache.commons.collections4.CollectionUtils;

import javax.transaction.*;
import java.util.*;

@SuppressWarnings("Duplicates")
public class DBUpdate {

    private static class Versions {

        public static <T extends Version> T process(Version metaInfo) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            return process(metaInfo, false);
        }

        @SuppressWarnings("unchecked")
        public static <T extends Version> T process(Version metaInfo, boolean keepOriginal) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Version found = OrpheusDbJPAEntityManagerUtils.find(metaInfo);
            if (found == null) {
                keepOriginal = false;
            }
            if (found != null) {
                if (!keepOriginal) {
                    found.override(metaInfo);
                    OrpheusDbJPAEntityManagerUtils.merge(found);
                }
            } else {
                found = metaInfo;
                OrpheusDbJPAEntityManagerUtils.persist(found);
            }
            return (T) found;
        }
    }

    private static class Histories {

        public static <T extends History> T process(History metaInfo) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            return process(metaInfo, false);
        }

        @SuppressWarnings("unchecked")
        public static <T extends History> T process(History history, boolean keepOriginal) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            History found = OrpheusDbJPAEntityManagerUtils.find(history);
            if (found == null) {
                keepOriginal = false;
            }
            if (found != null) {
                if (!keepOriginal) {
                    found.override(history);
                    found.setVersion(Versions.process(found.getVersion(), true));
                    OrpheusDbJPAEntityManagerUtils.merge(found);
                }
            } else {
                found = history;
                found.setVersion(Versions.process(found.getVersion(), true));
                OrpheusDbJPAEntityManagerUtils.persist(found);
            }
            return (T) found;
        }
    }

    private static class MetaInfos {

        public static <T extends MetaInfo> T process(MetaInfo metaInfo) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            return process(metaInfo, false);
        }

        @SuppressWarnings("unchecked")
        public static <T extends MetaInfo> T process(MetaInfo metaInfo, boolean keepOriginal) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            /*MetaInfo found = OrpheusDbJPAEntityManagerUtils.find(metaInfo);
            if (found == null) {
                keepOriginal = false;
            }
            if (found != null) {
                if (!keepOriginal) {
                    found.override(metaInfo);
                    if (found.getHistory().isEmpty()) {
                        found.addNewHistory(found.getCreated(), found.getCreatedBy());
                        for (History history : found.getHistory()) {
                            Histories.process(history, true);
                        }
                        for (History history : found.getHistory()) {
                            OrpheusDbJPAEntityManagerUtils.merge(history);
                        }
                    }
                    OrpheusDbJPAEntityManagerUtils.merge(found);
                }
            } else {
                found = metaInfo;
                if (found.getHistory().isEmpty()) {
                    found.addNewHistory(found.getCreated(), found.getCreatedBy());
                    for (History history : found.getHistory()) {
                        Histories.process(history, true);
                    }
                    for (History history : found.getHistory()) {
                        OrpheusDbJPAEntityManagerUtils.merge(history);
                    }
                }
                OrpheusDbJPAEntityManagerUtils.persist(found);
            }
            return (T) found;*/
            return (T) metaInfo;
        }
    }

    private static class BaseEntities {

        public static <T extends BaseEntity> T process(BaseEntity baseEntity) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            return process(baseEntity, false);
        }

        @SuppressWarnings("unchecked")
        public static <T extends BaseEntity> T process(BaseEntity baseEntity, boolean keepOriginal) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            BaseEntity found = OrpheusDbJPAEntityManagerUtils.find(baseEntity);
            if (found == null) {
                keepOriginal = false;
                if (Item.class.isAssignableFrom(baseEntity.getClass())) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("kind", ((Item) baseEntity).getKind());
                    params.put("name", ((Item) baseEntity).getName());
                    found = OrpheusDbJPAEntityManagerUtils.findByQuery(Item.class, Item.GET_ITEMS_BY_NAME_AND_KIND, params);
                    if (found != null) {
                        keepOriginal = true;
                    }
                }
            }
            if (found != null) {
                if (!keepOriginal) {
                    found.override(baseEntity);
                    MetaInfo meta = MetaInfos.process(found.getMeta(), true);
                    found.setMeta(meta);
                    OrpheusDbJPAEntityManagerUtils.merge(found);
                } else {
                    OrpheusDbJPAEntityManagerUtils.detach(found);
                }
            } else {
                found = baseEntity;
                MetaInfo meta = MetaInfos.process(found.getMeta(), true);
                found.setMeta(meta);
                OrpheusDbJPAEntityManagerUtils.persist(found);
            }
            return (T) found;
        }
    }

    private static class Taggables {

        public static Taggable process(Taggable taggable) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<CompositeElement> clonedBy = taggable.getClonedBy();
            if (CollectionUtils.isNotEmpty(clonedBy)) {
                Collection<CompositeElement> compositeElementsList = new ArrayList<>();
                Set<CompositeElement> clonedBy_ = new LinkedHashSet<>(clonedBy);
                clonedBy.clear();
                OrpheusDbJPAEntityManagerUtils.merge(taggable);
                for (CompositeElement compositeElement : clonedBy_) {
                    compositeElementsList.add(CompositeElements.process(compositeElement));
                }
                clonedBy.addAll(compositeElementsList);
            }
            CompositeElement clonedFrom = taggable.getClonedFrom();
            if (clonedFrom != null) {
                clonedFrom = CompositeElements.process(clonedFrom);
                taggable.setClonedFrom(clonedFrom);
            }
            ElementRole role = taggable.getRole();
            if (role != null) {
                role = ElementRoles.process(role);
                taggable.setRole(role);
            }
            Set<Rank> ranks = taggable.getRanks();
            if (CollectionUtils.isNotEmpty(ranks)) {
                Collection<Rank> rankList = new ArrayList<>();
                Set<Rank> ranks_ = new LinkedHashSet<>(ranks);
                ranks.clear();
                OrpheusDbJPAEntityManagerUtils.merge(taggable);
                for (Rank rank : ranks_) {
                    rankList.add(Ranks.process(rank));
                }
                ranks.addAll(rankList);
            }
            return BaseEntities.process(taggable);
        }
    }

    private static class Items {

        public static Item process(Item item) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<ConnectTrigger> canBeConnectedFrom = item.getCanBeConnectedFrom();
            if (CollectionUtils.isNotEmpty(canBeConnectedFrom)) {
                Collection<ConnectTrigger> canBeConnectedFromList = new ArrayList<>();
                Set<ConnectTrigger> canBeConnectedFrom_ = new LinkedHashSet<>(canBeConnectedFrom);
                canBeConnectedFrom.clear();
                OrpheusDbJPAEntityManagerUtils.merge(item);
                for (ConnectTrigger connectTrigger : canBeConnectedFrom_) {
                    canBeConnectedFromList.add(ConnectTriggers.process(connectTrigger));
                }
                canBeConnectedFrom.addAll(canBeConnectedFromList);
            }
            Set<ConnectTrigger> canBeConnectedTo = item.getCanBeConnectedTo();
            if (CollectionUtils.isNotEmpty(canBeConnectedTo)) {
                Collection<ConnectTrigger> canBeConnectedToList = new ArrayList<>();
                Set<ConnectTrigger> canBeConnectedTo_ = new LinkedHashSet<>(canBeConnectedTo);
                canBeConnectedTo.clear();
                OrpheusDbJPAEntityManagerUtils.merge(item);
                for (ConnectTrigger connectTrigger : canBeConnectedTo_) {
                    canBeConnectedToList.add(ConnectTriggers.process(connectTrigger));
                }
                canBeConnectedTo.addAll(canBeConnectedToList);
            }
            Set<CompositeElement> children = item.getChildren();
            if (CollectionUtils.isNotEmpty(children)) {
                Collection<CompositeElement> childrenList = new ArrayList<>();
                Set<CompositeElement> children_ = new LinkedHashSet<>(children);
                children.clear();
                OrpheusDbJPAEntityManagerUtils.merge(item);
                for (CompositeElement child : children_) {
                    childrenList.add(CompositeElements.process(child));
                }
                children.addAll(childrenList);
            }
            Image image = item.getImage();
            if (image != null) {
                image = Images.process(image);
                item.setImage(image);
            }
            MetaData metaData = item.getMetaData();
            if (metaData != null) {
                metaData = MetaDatas.process(metaData);
                item.setMetaData(metaData);
            }
            CompositeElement parent = item.getParent();
            if (parent != null) {
                parent = CompositeElements.process(parent);
                item.setParent(parent);
            }
            Set<Relationship> relationships = item.getRelationships();
            if (CollectionUtils.isNotEmpty(relationships)) {
                Collection<Relationship> relationshipsList = new ArrayList<>();
                Set<Relationship> relationships_ = new LinkedHashSet<>(relationships);
                relationships.clear();
                OrpheusDbJPAEntityManagerUtils.merge(item);
                for (Relationship relationship : relationships_) {
                    relationshipsList.add(Relationships.process(relationship));
                }
                relationships.addAll(relationshipsList);
            }
            Shape shape = item.getShape();
            if (shape != null) {
                shape = Shapes.process(shape);
                item.setShape(shape);
            }
            return (Item) Taggables.process(item);
        }
    }

    private static class CompositeElements {

        public static CompositeElement process(CompositeElement compositeElement) {
            CompositeElement found = OrpheusDbJPAEntityManagerUtils.find(compositeElement);
            if (found != null) {
                found.override(compositeElement);
            } else {
                found = compositeElement;
            }
            return found;
        }
    }

    private static class Ranks {

        public static Rank process(Rank rank) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Rank found = OrpheusDbJPAEntityManagerUtils.find(rank);
            if (found != null) {
                found.override(rank);
                OrpheusDbJPAEntityManagerUtils.merge(found);
            } else {
                found = rank;
                OrpheusDbJPAEntityManagerUtils.persist(found);
            }
            return found;
        }
    }

    private static class Features {

        public static Feature process(Feature feature) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<Constraint> incomingContraints = feature.getIncomingConstraints();
            if (CollectionUtils.isNotEmpty(incomingContraints)) {
                Collection<Constraint> constraintList = new ArrayList<>();
                Set<Constraint> incomingContraints_ = new LinkedHashSet<>(incomingContraints);
                incomingContraints.clear();
                OrpheusDbJPAEntityManagerUtils.merge(feature);
                for (Constraint constraint : incomingContraints_) {
                    constraintList.add(Constraints.process(constraint));
                }
                incomingContraints.addAll(constraintList);
            }
            Set<Constraint> outgoingConstraints = feature.getOutgoingConstraints();
            if (CollectionUtils.isNotEmpty(outgoingConstraints)) {
                Collection<Constraint> constraintList = new ArrayList<>();
                Set<Constraint> outgoingConstraints_ = new LinkedHashSet<>(outgoingConstraints);
                outgoingConstraints.clear();
                OrpheusDbJPAEntityManagerUtils.merge(feature);
                for (Constraint constraint : outgoingConstraints_) {
                    constraintList.add(Constraints.process(constraint));
                }
                outgoingConstraints.addAll(constraintList);
            }
            return (Feature) Items.process(feature);
        }
    }

    private static class Elements {

        public static Element process(Element element) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<Feature> features = element.getFeatures();
            if (CollectionUtils.isNotEmpty(features)) {
                Collection<Feature> featureList = new ArrayList<>();
                Set<Feature> features_ = new LinkedHashSet<>(features);
                features.clear();
                OrpheusDbJPAEntityManagerUtils.merge(element);
                for (Feature feature : features_) {
                    featureList.add(Features.process(feature));
                }
                features.addAll(featureList);
            }
            return (Element) Items.process(element);
        }
    }

    public static class Components {

        public static Component process(Component component) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            return (Component) Elements.process(component);
        }
    }

    public static class Containers {

        public static Container process(Container container) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<Container> containers = container.getContainers();
            if (CollectionUtils.isNotEmpty(containers)) {
                Collection<Container> containerList = new ArrayList<>();
                Set<Container> containers_ = new LinkedHashSet<>(containers);
                containers.clear();
                OrpheusDbJPAEntityManagerUtils.merge(container);
                for (Container container_ : containers_) {
                    containerList.add(Containers.process(container_));
                }
                containers.addAll(containerList);
            }
            Set<Component> components = container.getComponents();
            if (CollectionUtils.isNotEmpty(components)) {
                Collection<Component> componentList = new ArrayList<>();
                Set<Component> components_ = new LinkedHashSet<>(components);
                components.clear();
                OrpheusDbJPAEntityManagerUtils.merge(container);
                for (Component component_ : components_) {
                    componentList.add(Components.process(component_));
                }
                components.addAll(componentList);
            }
            return (Container) Elements.process(container);
        }
    }

    public static class Systems {

        public static System process(System system) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<System> systems = system.getSystems();
            if (CollectionUtils.isNotEmpty(systems)) {
                Collection<System> systemList = new ArrayList<>();
                Set<System> systems_ = new LinkedHashSet<>(systems);
                systems.clear();
                OrpheusDbJPAEntityManagerUtils.merge(system);
                for (System system_ : systems_) {
                    systemList.add(Systems.process(system_));
                }
                systems.addAll(systemList);
            }
            Set<Container> containers = system.getContainers();
            if (CollectionUtils.isNotEmpty(containers)) {
                Collection<Container> containerList = new ArrayList<>();
                Set<Container> containers_ = new LinkedHashSet<>(containers);
                containers.clear();
                OrpheusDbJPAEntityManagerUtils.merge(system);
                for (Container container_ : containers_) {
                    containerList.add(Containers.process(container_));
                }
                containers.addAll(containerList);
            }
            Set<Component> components = system.getComponents();
            if (CollectionUtils.isNotEmpty(components)) {
                Collection<Component> componentList = new ArrayList<>();
                Set<Component> components_ = new LinkedHashSet<>(components);
                components.clear();
                OrpheusDbJPAEntityManagerUtils.merge(system);
                for (Component component_ : components_) {
                    componentList.add(Components.process(component_));
                }
                components.addAll(componentList);
            }
            return (System) Elements.process(system);
        }
    }

    public static class Layers {

        public static Layer process(Layer layer) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<System> systems = layer.getSystems();
            if (CollectionUtils.isNotEmpty(systems)) {
                Collection<System> systemList = new ArrayList<>();
                Set<System> systems_ = new LinkedHashSet<>(systems);
                systems.clear();
                OrpheusDbJPAEntityManagerUtils.merge(layer);
                for (System system_ : systems_) {
                    systemList.add(Systems.process(system_));
                }
                systems.addAll(systemList);
            }
            Set<Container> containers = layer.getContainers();
            if (CollectionUtils.isNotEmpty(containers)) {
                Collection<Container> containerList = new ArrayList<>();
                Set<Container> containers_ = new LinkedHashSet<>(containers);
                containers.clear();
                OrpheusDbJPAEntityManagerUtils.merge(layer);
                for (Container container_ : containers_) {
                    containerList.add(Containers.process(container_));
                }
                containers.addAll(containerList);
            }
            Set<Component> components = layer.getComponents();
            if (CollectionUtils.isNotEmpty(components)) {
                Collection<Component> componentList = new ArrayList<>();
                Set<Component> components_ = new LinkedHashSet<>(components);
                components.clear();
                OrpheusDbJPAEntityManagerUtils.merge(layer);
                for (Component component_ : components_) {
                    componentList.add(Components.process(component_));
                }
                components.addAll(componentList);
            }
            return (Layer) Elements.process(layer);
        }
    }

    public static class ArchitecturalModels {

        public static ArchitecturalModel process(ArchitecturalModel architecturalModel) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<Layer> layers = architecturalModel.getLayers();
            if (CollectionUtils.isNotEmpty(layers)) {
                Collection<Layer> layerList = new ArrayList<>();
                Set<Layer> layers_ = new LinkedHashSet<>(layers);
                layers.clear();
                OrpheusDbJPAEntityManagerUtils.merge(architecturalModel);
                for (Layer layer_ : layers_) {
                    layerList.add(Layers.process(layer_));
                }
                layers.addAll(layerList);
            }
            Set<System> systems = architecturalModel.getSystems();
            if (CollectionUtils.isNotEmpty(systems)) {
                Collection<System> systemList = new ArrayList<>();
                Set<System> systems_ = new LinkedHashSet<>(systems);
                systems.clear();
                OrpheusDbJPAEntityManagerUtils.merge(architecturalModel);
                for (System system_ : systems_) {
                    systemList.add(Systems.process(system_));
                }
                systems.addAll(systemList);
            }
            Set<Container> containers = architecturalModel.getContainers();
            if (CollectionUtils.isNotEmpty(containers)) {
                Collection<Container> containerList = new ArrayList<>();
                Set<Container> containers_ = new LinkedHashSet<>(containers);
                containers.clear();
                OrpheusDbJPAEntityManagerUtils.merge(architecturalModel);
                for (Container container_ : containers_) {
                    containerList.add(Containers.process(container_));
                }
                containers.addAll(containerList);
            }
            Set<Component> components = architecturalModel.getComponents();
            if (CollectionUtils.isNotEmpty(components)) {
                Collection<Component> componentList = new ArrayList<>();
                Set<Component> components_ = new LinkedHashSet<>(components);
                components.clear();
                OrpheusDbJPAEntityManagerUtils.merge(architecturalModel);
                for (Component component_ : components_) {
                    componentList.add(Components.process(component_));
                }
                components.addAll(componentList);
            }
            Set<Consumer> consumers = architecturalModel.getConsumers();
            if (CollectionUtils.isNotEmpty(consumers)) {
                Collection<Consumer> consumerList = new ArrayList<>();
                Set<Consumer> consumers_ = new LinkedHashSet<>(consumers);
                consumers.clear();
                OrpheusDbJPAEntityManagerUtils.merge(architecturalModel);
                for (Consumer consumer_ : consumers_) {
                    consumerList.add(Consumers.process(consumer_));
                }
                consumers.addAll(consumerList);
            }
            Set<DeploymentNode> deploymentNodes = architecturalModel.getDeploymentNodes();
            if (CollectionUtils.isNotEmpty(deploymentNodes)) {
                Collection<DeploymentNode> deploymentNodeList = new ArrayList<>();
                Set<DeploymentNode> deploymentNodes_ = new LinkedHashSet<>(deploymentNodes);
                deploymentNodes.clear();
                OrpheusDbJPAEntityManagerUtils.merge(architecturalModel);
                for (DeploymentNode deploymentNode_ : deploymentNodes_) {
                    deploymentNodeList.add(DeploymentNodes.process(deploymentNode_));
                }
                deploymentNodes.addAll(deploymentNodeList);
            }
            return (ArchitecturalModel) Elements.process(architecturalModel);
        }
    }


    public static class ComponentElements {

        public static ComponentElement process(ComponentElement container) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            return (ComponentElement) Elements.process(container);
        }
    }

    public static class Groups {

        public static Group process(Group container) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<Group> containers = container.getGroups();
            if (CollectionUtils.isNotEmpty(containers)) {
                Collection<Group> containerList = new ArrayList<>();
                Set<Group> containers_ = new LinkedHashSet<>(containers);
                containers.clear();
                OrpheusDbJPAEntityManagerUtils.merge(container);
                for (Group container_ : containers_) {
                    containerList.add(Groups.process(container_));
                }
                containers.addAll(containerList);
            }
            Set<ComponentElement> elements = container.getElements();
            if (CollectionUtils.isNotEmpty(elements)) {
                Collection<ComponentElement> componentList = new ArrayList<>();
                Set<ComponentElement> elements_ = new LinkedHashSet<>(elements);
                elements.clear();
                OrpheusDbJPAEntityManagerUtils.merge(container);
                for (ComponentElement component_ : elements_) {
                    componentList.add(ComponentElements.process(component_));
                }
                elements.addAll(componentList);
            }
            return (Group) Elements.process(container);
        }
    }

    public static class ComponentModelLayers {

        public static com.araguacaima.open_archi.persistence.diagrams.component.Layer process(com.araguacaima.open_archi.persistence.diagrams.component.Layer layer) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<Group> groups = layer.getGroups();
            if (CollectionUtils.isNotEmpty(groups)) {
                Collection<Group> groupList = new ArrayList<>();
                Set<Group> groups_ = new LinkedHashSet<>(groups);
                groups.clear();
                OrpheusDbJPAEntityManagerUtils.merge(layer);
                for (Group group : groups_) {
                    groupList.add(Groups.process(group));
                }
                groups.addAll(groupList);
            }
            Set<ComponentElement> components = layer.getElements();
            if (CollectionUtils.isNotEmpty(components)) {
                Collection<ComponentElement> componentList = new ArrayList<>();
                Set<ComponentElement> components_ = new LinkedHashSet<>(components);
                components.clear();
                OrpheusDbJPAEntityManagerUtils.merge(layer);
                for (ComponentElement component_ : components_) {
                    componentList.add(ComponentElements.process(component_));
                }
                components.addAll(componentList);
            }
            return (com.araguacaima.open_archi.persistence.diagrams.component.Layer) Elements.process(layer);
        }
    }


    public static class ComponentModels {

        public static ComponentModel process(ComponentModel componentModel) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<com.araguacaima.open_archi.persistence.diagrams.component.Layer> layers = componentModel.getLayers();
            if (CollectionUtils.isNotEmpty(layers)) {
                Collection<com.araguacaima.open_archi.persistence.diagrams.component.Layer> layerList = new ArrayList<>();
                Set<com.araguacaima.open_archi.persistence.diagrams.component.Layer> layers_ = new LinkedHashSet<>(layers);
                layers.clear();
                OrpheusDbJPAEntityManagerUtils.merge(componentModel);
                for (com.araguacaima.open_archi.persistence.diagrams.component.Layer layer_ : layers_) {
                    layerList.add(ComponentModelLayers.process(layer_));
                }
                layers.addAll(layerList);
            }
            Set<Group> groups = componentModel.getGroups();
            if (CollectionUtils.isNotEmpty(groups)) {
                Collection<Group> groupList = new ArrayList<>();
                Set<Group> groups_ = new LinkedHashSet<>(groups);
                groups.clear();
                OrpheusDbJPAEntityManagerUtils.merge(componentModel);
                for (Group group : groups_) {
                    groupList.add(Groups.process(group));
                }
                groups.addAll(groupList);
            }
            Set<ComponentElement> components = componentModel.getElements();
            if (CollectionUtils.isNotEmpty(components)) {
                Collection<ComponentElement> componentList = new ArrayList<>();
                Set<ComponentElement> components_ = new LinkedHashSet<>(components);
                components.clear();
                OrpheusDbJPAEntityManagerUtils.merge(componentModel);
                for (ComponentElement component_ : components_) {
                    componentList.add(ComponentElements.process(component_));
                }
                components.addAll(componentList);
            }
            return (ComponentModel) Elements.process(componentModel);
        }
    }

    private static class Relationships {

        public static Relationship process(Relationship relationship) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            CompositeElement source = relationship.getSource();
            if (source != null) {
                source = CompositeElements.process(source);
                relationship.setSource(source);
            }
            CompositeElement destination = relationship.getDestination();
            if (destination != null) {
                destination = CompositeElements.process(destination);
                relationship.setDestination(destination);
            }
            Connector connector = relationship.getConnector();
            if (connector != null) {
                connector = Connectors.process(connector);
                relationship.setConnector(connector);
            }
            return (Relationship) Taggables.process(relationship);
        }
    }

    private static class Consumers {

        public static Consumer process(Consumer consumer) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            return (Consumer) Elements.process(consumer);
        }
    }

    private static class DeploymentNodes {

        public static DeploymentNode process(DeploymentNode deploymentNode) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<ContainerInstance> containerInstances = deploymentNode.getContainerInstances();
            if (CollectionUtils.isNotEmpty(containerInstances)) {
                Collection<ContainerInstance> containerInstanceList = new ArrayList<>();
                Set<ContainerInstance> containerInstances_ = new LinkedHashSet<>(containerInstances);
                containerInstances.clear();
                OrpheusDbJPAEntityManagerUtils.merge(deploymentNode);
                for (ContainerInstance containerInstance_ : containerInstances_) {
                    containerInstanceList.add(ContainerInstances.process(containerInstance_));
                }
                containerInstances.addAll(containerInstanceList);
            }
            return (DeploymentNode) Elements.process(deploymentNode);
        }
    }

    private static class Constraints {

        public static Constraint process(Constraint constraint) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            return BaseEntities.process(constraint);
        }
    }

    private static class ConnectTriggers {

        public static ConnectTrigger process(ConnectTrigger connectTrigger) {
            ConnectTrigger found = OrpheusDbJPAEntityManagerUtils.find(connectTrigger);
            if (found != null) {
                found.override(connectTrigger);
            } else {
                found = connectTrigger;
            }
            return found;
        }
    }

    private static class MetaDatas {

        public static MetaData process(MetaData metaData) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<Responsible> responsibles = metaData.getResponsibles();
            if (CollectionUtils.isNotEmpty(responsibles)) {
                Collection<Responsible> responsibleList = new ArrayList<>();
                Set<Responsible> responsibles_ = new LinkedHashSet<>(responsibles);
                responsibles.clear();
                OrpheusDbJPAEntityManagerUtils.merge(metaData);
                for (Responsible responsible_ : responsibles_) {
                    responsibleList.add(Responsibles.process(responsible_));
                }
                responsibles.addAll(responsibleList);
            }
            Set<Responsible> collaborators = metaData.getCollaborators();
            if (CollectionUtils.isNotEmpty(collaborators)) {
                Collection<Responsible> collaboratorList = new ArrayList<>();
                Set<Responsible> collaborators_ = new LinkedHashSet<>(collaborators);
                collaborators.clear();
                OrpheusDbJPAEntityManagerUtils.merge(metaData);
                for (Responsible collaborator_ : collaborators_) {
                    collaboratorList.add(Responsibles.process(collaborator_));
                }
                collaborators.addAll(collaboratorList);
            }
            Set<Taggable> relatedWith = metaData.getRelatedWith();
            if (CollectionUtils.isNotEmpty(relatedWith)) {
                Collection<Taggable> taggableList = new ArrayList<>();
                Set<Taggable> relatedWith_ = new LinkedHashSet<>(relatedWith);
                relatedWith.clear();
                OrpheusDbJPAEntityManagerUtils.merge(metaData);
                for (Taggable taggable_ : relatedWith_) {
                    taggableList.add(Taggables.process(taggable_));
                }
                relatedWith.addAll(taggableList);
            }
            Set<Taggable> usedIn = metaData.getUsedIn();
            if (CollectionUtils.isNotEmpty(usedIn)) {
                Collection<Taggable> taggableList = new ArrayList<>();
                Set<Taggable> usedIn_ = new LinkedHashSet<>(usedIn);
                usedIn.clear();
                OrpheusDbJPAEntityManagerUtils.merge(metaData);
                for (Taggable taggable_ : usedIn_) {
                    taggableList.add(Taggables.process(taggable_));
                }
                usedIn.addAll(taggableList);
            }
            Set<View> views = metaData.getViews();
            if (CollectionUtils.isNotEmpty(views)) {
                Collection<View> viewList = new ArrayList<>();
                Set<View> views_ = new LinkedHashSet<>(views);
                views.clear();
                OrpheusDbJPAEntityManagerUtils.merge(metaData);
                for (View view_ : views_) {
                    viewList.add(Views.process(view_));
                }
                views.addAll(viewList);
            }
            return BaseEntities.process(metaData);
        }
    }

    private static class Persons {

        public static Person process(Person person) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            return (Person) BaseEntities.process(person);
        }
    }

    private static class Responsibles {

        public static Responsible process(Responsible responsible) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Person person = responsible.getPerson();
            if (person != null) {
                person = Persons.process(person);
                responsible.setPerson(person);
            }
            return (Responsible) BaseEntities.process(responsible);
        }
    }

    private static class Images {
        public static Image process(Image image) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            return (Image) BaseEntities.process(image);
        }
    }

    private static class Views {
        public static View process(View view) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            return (View) BaseEntities.process(view);
        }
    }

    private static class ElementRoles {

        public static ElementRole process(ElementRole elementRole) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            return (ElementRole) BaseEntities.process(elementRole);
        }
    }

    private static class Shapes {

        public static Shape process(Shape shape) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Shape found = OrpheusDbJPAEntityManagerUtils.find(shape);
            if (found != null) {
                Shape clonedShape = new Shape();
                clonedShape.override(found);
                clonedShape.override(shape);
                clonedShape.resetId();
                shape = clonedShape;
            }
            Set<ColorScheme> colorSchemes = shape.getColorSchemes();
            if (CollectionUtils.isNotEmpty(colorSchemes)) {
                Collection<ColorScheme> colorSchemeList = new ArrayList<>();
                Set<ColorScheme> colorSchemes_ = new LinkedHashSet<>(colorSchemes);
                colorSchemes.clear();
                for (ColorScheme colorScheme_ : colorSchemes_) {
                    colorSchemeList.add(ColorSchemes.process(colorScheme_, false));
                }
                colorSchemes.addAll(colorSchemeList);
            }
            return (Shape) BaseEntities.process(shape, false);
        }
    }

    private static class ColorSchemes {

        public static ColorScheme process(ColorScheme colorScheme) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            return process(colorScheme, true);
        }


        public static ColorScheme process(ColorScheme colorScheme, boolean keepOriginal) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            return (ColorScheme) BaseEntities.process(colorScheme, keepOriginal);
        }
    }

    private static class ContainerInstances {

        public static ContainerInstance process(ContainerInstance containerInstance) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            return (ContainerInstance) Elements.process(containerInstance);
        }
    }

    private static class Connectors {

        public static Connector process(Connector connector) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            return (Connector) BaseEntities.process(connector);
        }
    }

}
