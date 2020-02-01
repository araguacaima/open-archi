package com.araguacaima.open_archi.web;

import com.araguacaima.open_archi.persistence.diagrams.architectural.System;
import com.araguacaima.open_archi.persistence.diagrams.architectural.*;
import com.araguacaima.open_archi.persistence.diagrams.component.ComponentElement;
import com.araguacaima.open_archi.persistence.diagrams.component.ComponentModel;
import com.araguacaima.open_archi.persistence.diagrams.component.Group;
import com.araguacaima.open_archi.persistence.diagrams.core.*;
import com.araguacaima.open_archi.persistence.diagrams.core.reliability.Constraint;
import com.araguacaima.open_archi.persistence.diagrams.meta.View;
import com.araguacaima.open_archi.persistence.diagrams.persons.Person;
import com.araguacaima.open_archi.persistence.diagrams.persons.Responsible;
import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import org.apache.commons.collections4.CollectionUtils;

import javax.transaction.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DBDelete {


    private static class BaseEntities {

        public static void process(BaseEntity baseEntity) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            process(baseEntity, false);
        }

        @SuppressWarnings("unchecked")
        public static void process(BaseEntity baseEntity, boolean keepOriginal) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            BaseEntity found = OrpheusDbJPAEntityManagerUtils.find(baseEntity);
            if (found == null) {
                if (Item.class.isAssignableFrom(baseEntity.getClass())) {
                    Map<String, Object> params = new HashMap<>();
                    params.put("kind", ((Item) baseEntity).getKind());
                    params.put("name", ((Item) baseEntity).getName());
                    found = OrpheusDbJPAEntityManagerUtils.findByQuery(Item.class, Item.GET_ITEMS_BY_NAME_AND_KIND, params);
                }
            }
            if (found != null) {
                if (!keepOriginal) {
                    OrpheusDbJPAEntityManagerUtils.delete(found);
                }
            } else {
                found = baseEntity;
                if (!keepOriginal) {
                    OrpheusDbJPAEntityManagerUtils.delete(found);
                }
            }
        }
    }

    private static class Taggables {

        public static void process(Taggable taggable) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<CompositeElement> clonedBy = taggable.getClonedBy();
            if (CollectionUtils.isNotEmpty(clonedBy)) {
                clonedBy.clear();
                OrpheusDbJPAEntityManagerUtils.merge(taggable);
            }
            CompositeElement clonedFrom = taggable.getClonedFrom();
            if (clonedFrom != null) {
                CompositeElements.process(clonedFrom);
            }
            ElementRole role = taggable.getRole();
            if (role != null) {
                ElementRoles.process(role);
            }
            BaseEntities.process(taggable);
        }
    }

    private static class Items {

        public static void process(Item item) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<ConnectTrigger> canBeConnectedFrom = item.getCanBeConnectedFrom();
            if (CollectionUtils.isNotEmpty(canBeConnectedFrom)) {
                canBeConnectedFrom.clear();
                OrpheusDbJPAEntityManagerUtils.merge(item);
            }
            Set<ConnectTrigger> canBeConnectedTo = item.getCanBeConnectedTo();
            if (CollectionUtils.isNotEmpty(canBeConnectedTo)) {
                canBeConnectedTo.clear();
                OrpheusDbJPAEntityManagerUtils.merge(item);
            }
            Set<CompositeElement> children = item.getChildren();
            if (CollectionUtils.isNotEmpty(children)) {
                children.clear();
                OrpheusDbJPAEntityManagerUtils.merge(item);
            }
            Image image = item.getImage();
            if (image != null) {
                Images.process(image);
            }
            MetaData metaData = item.getMetaData();
            if (metaData != null) {
                MetaDatas.process(metaData);
            }
            CompositeElement parent = item.getParent();
            if (parent != null) {
                CompositeElements.process(parent);
            }
            Set<Relationship> relationships = item.getRelationships();
            if (CollectionUtils.isNotEmpty(relationships)) {
                relationships.clear();
                OrpheusDbJPAEntityManagerUtils.merge(item);
            }
            Shape shape = item.getShape();
            if (shape != null) {
                Shapes.process(shape);
            }
            DiagramType diagramType = item.getDiagramType();
            if (diagramType != null) {
                item.setDiagramType(null);
            }
            Taggables.process(item);
        }
    }

    private static class CompositeElements {

        public static void process(CompositeElement compositeElement) {
            CompositeElement found = OrpheusDbJPAEntityManagerUtils.find(compositeElement);
            if (found != null) {
                found.override(compositeElement);
            } else {
                found = compositeElement;
            }

        }
    }

    private static class Features {

        public static void process(Feature feature) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<Constraint> incomingContraints = feature.getIncomingConstraints();
            if (CollectionUtils.isNotEmpty(incomingContraints)) {
                incomingContraints.clear();
                OrpheusDbJPAEntityManagerUtils.merge(feature);
            }
            Set<Constraint> outgoingConstraints = feature.getOutgoingConstraints();
            if (CollectionUtils.isNotEmpty(outgoingConstraints)) {
                outgoingConstraints.clear();
                OrpheusDbJPAEntityManagerUtils.merge(feature);
            }
            Items.process(feature);
        }
    }

    private static class Elements {

        public static void process(Element element) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<Feature> features = element.getFeatures();
            if (CollectionUtils.isNotEmpty(features)) {
                features.clear();
                OrpheusDbJPAEntityManagerUtils.merge(element);
            }
            Items.process(element);
        }
    }

    public static class Components {

        public static void process(Component component) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Elements.process(component);
        }
    }

    public static class Containers {

        public static void process(Container container) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<Container> containers = container.getContainers();
            if (CollectionUtils.isNotEmpty(containers)) {
                containers.clear();
                OrpheusDbJPAEntityManagerUtils.merge(container);
            }
            Set<Component> components = container.getComponents();
            if (CollectionUtils.isNotEmpty(components)) {
                components.clear();
                OrpheusDbJPAEntityManagerUtils.merge(container);
            }
            Elements.process(container);
        }
    }

    public static class Systems {

        public static void process(System system) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<System> systems = system.getSystems();
            if (CollectionUtils.isNotEmpty(systems)) {
                systems.clear();
                OrpheusDbJPAEntityManagerUtils.merge(system);
            }
            Set<Container> containers = system.getContainers();
            if (CollectionUtils.isNotEmpty(containers)) {
                containers.clear();
                OrpheusDbJPAEntityManagerUtils.merge(system);
            }
            Set<Component> components = system.getComponents();
            if (CollectionUtils.isNotEmpty(components)) {
                components.clear();
                OrpheusDbJPAEntityManagerUtils.merge(system);
            }
            Elements.process(system);
        }
    }

    public static class Layers {

        public static void process(Layer layer) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Map<String, Object> params = new HashMap<>();
            params.put("layerId", layer.getId());
            List<ArchitecturalModel> models = OrpheusDbJPAEntityManagerUtils.executeQuery(ArchitecturalModel.class, ArchitecturalModel.GET_ALL_MODELS_INCLUDING_LAYER, params);
            if (CollectionUtils.isNotEmpty(models)) {
                for (ArchitecturalModel model : models) {
                    Set<Layer> layers = model.getLayers();
                    if (layers.remove(layer)) {
                        OrpheusDbJPAEntityManagerUtils.merge(model);
                    }
                }
            }
            Elements.process(layer);
        }
    }

    public static class ArchitecturalModels {

        public static void process(ArchitecturalModel architecturalModel) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<Layer> layers = architecturalModel.getLayers();
            if (CollectionUtils.isNotEmpty(layers)) {
                layers.clear();
                OrpheusDbJPAEntityManagerUtils.merge(architecturalModel);
            }
            Set<System> systems = architecturalModel.getSystems();
            if (CollectionUtils.isNotEmpty(systems)) {
                systems.clear();
                OrpheusDbJPAEntityManagerUtils.merge(architecturalModel);
            }
            Set<Container> containers = architecturalModel.getContainers();
            if (CollectionUtils.isNotEmpty(containers)) {
                containers.clear();
                OrpheusDbJPAEntityManagerUtils.merge(architecturalModel);
            }
            Set<Component> components = architecturalModel.getComponents();
            if (CollectionUtils.isNotEmpty(components)) {
                components.clear();
                OrpheusDbJPAEntityManagerUtils.merge(architecturalModel);
            }
            Set<Consumer> consumers = architecturalModel.getConsumers();
            if (CollectionUtils.isNotEmpty(consumers)) {
                consumers.clear();
                OrpheusDbJPAEntityManagerUtils.merge(architecturalModel);
            }
            Set<DeploymentNode> deploymentNodes = architecturalModel.getDeploymentNodes();
            if (CollectionUtils.isNotEmpty(deploymentNodes)) {
                deploymentNodes.clear();
                OrpheusDbJPAEntityManagerUtils.merge(architecturalModel);
            }

            Elements.process(architecturalModel);
        }
    }


    public static class ComponentElements {

        public static void process(ComponentElement container) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Elements.process(container);
        }
    }

    public static class Groups {

        public static void process(Group container) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<Group> containers = container.getGroups();
            if (CollectionUtils.isNotEmpty(containers)) {
                containers.clear();
                OrpheusDbJPAEntityManagerUtils.merge(container);
            }
            Set<ComponentElement> elements = container.getElements();
            if (CollectionUtils.isNotEmpty(elements)) {
                elements.clear();
                OrpheusDbJPAEntityManagerUtils.merge(container);
            }
            Elements.process(container);
        }
    }

    public static class ComponentModelLayers {

        public static void process(com.araguacaima.open_archi.persistence.diagrams.component.Layer layer) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<Group> groups = layer.getGroups();
            if (CollectionUtils.isNotEmpty(groups)) {
                groups.clear();
                OrpheusDbJPAEntityManagerUtils.merge(layer);
            }
            Set<ComponentElement> components = layer.getElements();
            if (CollectionUtils.isNotEmpty(components)) {
                components.clear();
                OrpheusDbJPAEntityManagerUtils.merge(layer);
            }
            Elements.process(layer);
        }
    }


    public static class ComponentModels {

        public static void process(ComponentModel architecturalModel) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<com.araguacaima.open_archi.persistence.diagrams.component.Layer> layers = architecturalModel.getLayers();
            if (CollectionUtils.isNotEmpty(layers)) {
                layers.clear();
                OrpheusDbJPAEntityManagerUtils.merge(architecturalModel);
            }
            Set<Group> groups = architecturalModel.getGroups();
            if (CollectionUtils.isNotEmpty(groups)) {
                groups.clear();
                OrpheusDbJPAEntityManagerUtils.merge(architecturalModel);
            }
            Set<ComponentElement> components = architecturalModel.getElements();
            if (CollectionUtils.isNotEmpty(components)) {
                components.clear();
                OrpheusDbJPAEntityManagerUtils.merge(architecturalModel);
            }
            Elements.process(architecturalModel);
        }
    }

    private static class Relationships {

        public static void process(Relationship relationship) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            CompositeElement source = relationship.getSource();
            if (source != null) {
                CompositeElements.process(source);
            }
            CompositeElement destination = relationship.getDestination();
            if (destination != null) {
                CompositeElements.process(destination);
            }
            Connector connector = relationship.getConnector();
            if (connector != null) {
                Connectors.process(connector);
            }
            Taggables.process(relationship);
        }
    }

    private static class Consumers {

        public static void process(Consumer consumer) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Elements.process(consumer);
        }
    }

    private static class DeploymentNodes {

        public static void process(DeploymentNode deploymentNode) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<ContainerInstance> containerInstances = deploymentNode.getContainerInstances();
            if (CollectionUtils.isNotEmpty(containerInstances)) {
                containerInstances.clear();
                OrpheusDbJPAEntityManagerUtils.merge(deploymentNode);
            }
            Elements.process(deploymentNode);
        }
    }

    private static class Constraints {

        public static void process(Constraint constraint) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            BaseEntities.process(constraint);
        }
    }

    private static class ConnectTriggers {

        public static void process(ConnectTrigger connectTrigger) {
            ConnectTrigger found = OrpheusDbJPAEntityManagerUtils.find(connectTrigger);
            if (found != null) {
                found.override(connectTrigger);
            } else {
                found = connectTrigger;
            }

        }
    }

    private static class MetaDatas {

        public static void process(MetaData metaData) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Set<Responsible> responsibles = metaData.getResponsibles();
            if (CollectionUtils.isNotEmpty(responsibles)) {
                responsibles.clear();
                OrpheusDbJPAEntityManagerUtils.merge(metaData);
            }
            Set<Responsible> collaborators = metaData.getCollaborators();
            if (CollectionUtils.isNotEmpty(collaborators)) {
                collaborators.clear();
                OrpheusDbJPAEntityManagerUtils.merge(metaData);
            }
            Set<Taggable> relatedWith = metaData.getRelatedWith();
            if (CollectionUtils.isNotEmpty(relatedWith)) {
                relatedWith.clear();
                OrpheusDbJPAEntityManagerUtils.merge(metaData);
            }
            Set<Taggable> usedIn = metaData.getUsedIn();
            if (CollectionUtils.isNotEmpty(usedIn)) {
                usedIn.clear();
                OrpheusDbJPAEntityManagerUtils.merge(metaData);
            }
            Set<View> views = metaData.getViews();
            if (CollectionUtils.isNotEmpty(views)) {
                views.clear();
                OrpheusDbJPAEntityManagerUtils.merge(metaData);
            }
            BaseEntities.process(metaData);
        }
    }

    private static class Persons {

        public static void process(Person person) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            BaseEntities.process(person);
        }
    }

    private static class Responsibles {

        public static void process(Responsible responsible) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Person person = responsible.getPerson();
            if (person != null) {
                Persons.process(person);
            }
            BaseEntities.process(responsible);
        }
    }

    private static class Images {
        public static void process(Image image) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            BaseEntities.process(image);
        }
    }

    private static class Views {
        public static void process(View view) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            BaseEntities.process(view);
        }
    }

    private static class ElementRoles {

        public static void process(ElementRole elementRole) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            BaseEntities.process(elementRole);
        }
    }

    private static class Shapes {

        public static void process(Shape shape) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            /*Set<ColorScheme> colorSchemes = shape.getColorSchemes();
            if (CollectionUtils.isNotEmpty(colorSchemes)) {
                colorSchemes.clear();
                OrpheusDbJPAEntityManagerUtils.merge(shape);
            }*/
            BaseEntities.process(shape, true);
        }
    }

    private static class ColorSchemes {

        public static void process(ColorScheme colorScheme) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            BaseEntities.process(colorScheme, true);
        }
    }

    private static class ContainerInstances {

        public static void process(ContainerInstance containerInstance) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            Elements.process(containerInstance);
        }
    }

    private static class Connectors {

        public static void process(Connector connector) throws NotSupportedException, SystemException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
            BaseEntities.process(connector);
        }
    }

}
