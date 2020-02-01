class OpenArchiToDiagram {

    static process(model) {
        const type = model.kind;
        let diagram;
        alreadyProcessedNodes = [];
        alreadyProcessedLinks = [];
        switch (type) {
            case "FLOWCHART_MODEL":
                diagram = OpenArchiToDiagram.flowchartModel(model);
                break;
            case "SEQUENCE_MODEL":
                diagram = OpenArchiToDiagram.sequenceModel(model);
                break;
            case "GANTT_MODEL":
                diagram = OpenArchiToDiagram.ganttModel(model);
                break;
            case "ENTITY_RELATIONSHIP_MODEL":
                diagram = OpenArchiToDiagram.entityRelationshipModel(model);
                break;
            case "UML_CLASS_MODEL":
                diagram = OpenArchiToDiagram.umlModel(model);
                break;
            case "BPM_MODEL":
                diagram = OpenArchiToDiagram.bpmModel(model);
                break;
            case "ARCHITECTURE_MODEL":
            case "LAYER":
            case "SYSTEM":
            case "CONTAINER":
            case "COMPONENT":
                diagram = OpenArchiToDiagram.architectureModel(model);
                break;
            default:
                console.log("Still not implemented");
        }
        return diagram;
    };


    static architectureModel(model) {

        let diagram = {
            class: "go.GraphLinksModel"
        };
        if (model.kind !== "ARCHITECTURE_MODEL" && model.kind !== "LAYER" && model.kind !== "SYSTEM" && model.kind !== "CONTAINER" && model.kind !== "COMPONENT") {
            return diagram;
        }
        diagram.nodeDataArray = [];
        diagram.linkDataArray = [];

        OpenArchiToDiagram.processElement(model, diagram);

        return diagram;
    }

    static flowchartModel(model) {
        let diagram = {
            class: "go.GraphLinksModel"
        };
        diagram.nodeDataArray = [];
        diagram.linkDataArray = [];
        diagram.nodeDataArray.push({key: -1, category: "Start", loc: "5 0", text: "Start"});
        diagram.linkDataArray.push({from: -1, to: model.id, fromPort: "B", toPort: "T"});
        diagram.nodeDataArray.push({key: model.id, loc: "5 100", text: model.name});
        diagram.linkDataArray.push({from: model.id, to: -2, fromPort: "B", toPort: "T"});
        diagram.nodeDataArray.push({key: -2, category: "End", loc: "5 200", text: "End!"});
        return diagram;
    }

    static sequenceModel(model) {
        let diagram = {
            class: "go.GraphLinksModel"
        };
        diagram.nodeDataArray = [];
        diagram.linkDataArray = [];
        return diagram;
    }

    static ganttModel(model) {
        let diagram = {
            class: "go.GraphLinksModel"
        };
        diagram.nodeDataArray = [];
        diagram.linkDataArray = [];
        return diagram;
    }

    static entityRelationshipModel(model) {
        let diagram = {
            class: "go.GraphLinksModel"
        };
        diagram.nodeDataArray = [];
        diagram.linkDataArray = [];
        return diagram;
    }

    static umlModel(model) {
        let diagram = {
            class: "go.GraphLinksModel"
        };
        diagram.nodeDataArray = [];
        diagram.linkDataArray = [];
        return diagram;
    }

    static bpmModel(model) {
        let diagram = {
            class: "go.GraphLinksModel"
        };
        diagram.nodeDataArray = [];
        diagram.linkDataArray = [];
        return diagram;
    }

    static common(model) {
        let object = {};
        object.id = model.id;
        object.key = model.id;
        if (object.key === undefined) {
            object.key = Commons.prototype.random();
        }
        if (model.key === undefined || model.key < 0 || (model.id !== undefined && model.id !== model.key)) {
            model.key = object.key;
        }
        object.status = model.status;
        object.name = model.name;
        object.kind = model.kind;
        object.description = model.description;
        object.prototype = model.prototype;
        let shape = model.shape;
        if (shape) {
            object = OpenArchiToDiagram.fillShape(object, model);
        } else {
            object.category = model.kind;
            object.figure = (model.figure === undefined || model.figure === "") ? "RoundedRectangle" : model.figure;
            object.isGroup = model.isGroup;
        }
        if (model.clonedFrom) {
            object.clonedFrom = model.clonedFrom;
        }
        let image = model.image;
        if (image) {
            let raw = image.raw;
            raw = "data:image/svg+xml;base64," + window.btoa(raw);
            object.image = {raw: raw, type: image.type};
            if (image.id) {
                object.image.id = image.id;
            }
        }
        object.diagramType = model.diagramType;

        object.metaData = model.metaData;
        if (object.metaData !== undefined) {
            let views = object.metaData.views;
            if (views === undefined || _.isEmpty(views)) {
                views = [];
                views.push({parentId: model.id});
                object.metaData.views = views;
            } else {
                let found = false;
                for (let i = 0; i < views.length; i++) {
                    const view = views[i];
                    if (view.parentId === model.id) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    views.push({parentId: model.id});
                }
            }
        }
        object.rank = model.rank;
        return object;
    }

    static processModel(model, nodes, links) {
        let modelElement = OpenArchiToDiagram.common(model);
        links.pushAll(extractLinks(model));
        //TODO Añadir campos propios del model
        let model_ = fulfill(modelElement, modelElement.isGroup);
        nodes.push(model_);
        let links_;
        //TODO completar
        links.push(links_);
    }

    static processLayer(layer, nodes, links, parentId) {
        if (!_.isObject(layer)) {
            return;
        }
        let layerElement = OpenArchiToDiagram.common(layer);
        links.pushAll(extractLinks(layer));
        layerElement.isGroup = true;
        //TODO Añadir campos propios del layer
        //Los layer sólo se pueden agrupar en el modelo directamente, no en otros layers, systems, containers o components
        if (parentId !== undefined) {
            layerElement.group = parentId;
        }
        nodes.push(layerElement);

        if (layer.systems) {
            const systems = OpenArchiToDiagram.orderElementsByRank(layer.systems, parentId);
            systems.forEach(item => {
                OpenArchiToDiagram.processSystem(item, nodes, links, layer.key);
            });
        }

        if (layer.containers) {
            const containers = OpenArchiToDiagram.orderElementsByRank(layer.containers, parentId);
            containers.forEach(item => {
                OpenArchiToDiagram.processContainer(item, nodes, links, layer.key);
            });
        }

        if (layer.components) {
            const components = OpenArchiToDiagram.orderElementsByRank(layer.components, parentId);
            components.forEach(item => {
                OpenArchiToDiagram.processComponent(item, nodes, links, layer.key);
            });
        }
    }

    static processSystem(system, nodes, links, parentId) {
        if (!_.isObject(system)) {
            return;
        }
        let systemElement = OpenArchiToDiagram.common(system);
        links.pushAll(extractLinks(system));
        systemElement.isGroup = true;
        //TODO Añadir campos propios del system
        //Los systems sólo se pueden agrupar en layers u otros systems
        let systems = system.systems;
        let hasSystems = systems !== undefined && !Commons.prototype.isEmpty(systems);
        if (hasSystems) {
            systems = OpenArchiToDiagram.orderElementsByRank(systems, parentId);
            systems.forEach(function (system_) {
                OpenArchiToDiagram.processSystem(system_, nodes, links, system.key)
            });
        }
        let containers = system.containers;
        let hasContainers = containers !== undefined && !Commons.prototype.isEmpty(containers);
        if (hasContainers) {
            containers = OpenArchiToDiagram.orderElementsByRank(containers, parentId);
            containers.forEach(function (container_) {
                OpenArchiToDiagram.processContainer(container_, nodes, links, system.key)
            });
        }
        let components = system.components;
        let hasComponents = components !== undefined && !Commons.prototype.isEmpty(components);
        if (hasComponents) {
            components = OpenArchiToDiagram.orderElementsByRank(components, parentId);
            components.forEach(function (component_) {
                OpenArchiToDiagram.processComponent(component_, nodes, links, system.key)
            });
        }
        if (parentId !== undefined) {
            systemElement.group = parentId;
        }
        nodes.push(fulfill(systemElement, true, parentId));
    }

    static processContainer(container, nodes, links, parentId) {
        if (!_.isObject(container)) {
            return;
        }
        let containerElement = OpenArchiToDiagram.common(container);
        links.pushAll(extractLinks(container));
        containerElement.isGroup = true;
        //TODO Añadir campos propios del container
        //Los containers sólo se pueden agrupar en layers u otros containers

        let containers = container.containers;
        let hasContainers = containers !== undefined && !Commons.prototype.isEmpty(containers);
        if (hasContainers) {
            containers = OpenArchiToDiagram.orderElementsByRank(containers, parentId);
            containers.forEach(function (container_) {
                OpenArchiToDiagram.processContainer(container_, nodes, links, container.key)
            });
        }

        let components = container.components;
        let hasComponents = components !== undefined && !Commons.prototype.isEmpty(components);
        if (hasComponents) {
            components = OpenArchiToDiagram.orderElementsByRank(components, parentId);
            components.forEach(function (component_) {
                OpenArchiToDiagram.processComponent(component_, nodes, links, container.key)
            });
        }
        if (parentId !== undefined) {
            containerElement.group = parentId;
        }
        nodes.push(fulfill(containerElement, true, parentId));
    }


    static processComponent(component, nodes, links, parentId) {
        if (!_.isObject(component)) {
            return;
        }
        let componentElement = OpenArchiToDiagram.common(component);
        links.pushAll(extractLinks(component));
        componentElement.isGroup = false;
        //TODO Añadir campos propios del component
        //Los components sólo se pueden agrupar en layers u otros components
        if (parentId !== undefined) {
            componentElement.group = parentId;
        }
        nodes.push(fulfill(componentElement, false, parentId));
    }

    static processElement(model, diagram) {
        OpenArchiToDiagram.processModel(model, diagram.nodeDataArray, diagram.linkDataArray);
        const parentId = model.key;
        if (model.layers) {
            const layers = OpenArchiToDiagram.orderElementsByRank(model.layers, parentId);
            layers.forEach(layer => OpenArchiToDiagram.processLayer(layer, diagram.nodeDataArray, diagram.linkDataArray, parentId));
        }
        if (model.systems) {
            const systems = OpenArchiToDiagram.orderElementsByRank(model.systems, parentId);
            systems.forEach(system => OpenArchiToDiagram.processSystem(system, diagram.nodeDataArray, diagram.linkDataArray, parentId));
        }
        if (model.containers) {
            const containers = OpenArchiToDiagram.orderElementsByRank(model.containers, parentId);
            containers.forEach(container => OpenArchiToDiagram.processContainer(container, diagram.nodeDataArray, diagram.linkDataArray, parentId));
        }
        if (model.components) {
            const components = OpenArchiToDiagram.orderElementsByRank(model.components, parentId);
            components.forEach(component => OpenArchiToDiagram.processComponent(component, diagram.nodeDataArray, diagram.linkDataArray, parentId));
        }
        diagram.linkDataArray = removeDuplicates(diagram.linkDataArray.filter((link) => {
            return link !== undefined;
        }));
        diagram.nodeDataArray = removeDuplicates(diagram.nodeDataArray.filter((node) => {
            return node !== undefined;
        }));
    }

    static orderElementsByRank(elements, parentId) {
        elements.sort(function (a, b) {
            const rank1 = a.rank;
            const rank2 = b.rank;
            if (rank1 !== undefined) {
                const order1 = rank1[parentId];
                if (order1 !== undefined) {
                    const order2 = rank2[parentId];
                    if (order2 !== undefined) {
                        return order1 - order2;
                    } else {
                        return order1;
                    }
                }
            } else {
                if (rank2 === undefined) {
                    return 0;
                } else {
                    const order2 = rank2[parentId];
                    if (order2 !== undefined) {
                        return order2;
                    } else {
                        return 0;
                    }
                }
            }
        });
        return elements;
    }

    static fillShape(object, node) {
        let shape = node.shape;
        if (shape) {
            if (shape.colorSchemes === undefined) {
                let shapeType;
                if (shape.type === undefined) {
                    shapeType = node.kind;
                } else {
                    shapeType = shape.type;
                }
                $.ajax({
                    url: basePath + "/api/catalogs/element-types/" + shapeType + "/shape",
                    type: 'GET',
                    async: false,
                    crossDomain: true,
                    contentType: "application/json",
                    converters: {
                        "text json": function (response) {
                            return (response === "") ? null : JSON.parse(response);
                        }
                    }
                }).done((shapeText, textStatus, response) => {
                    if (response.status === 200) {
                        shape = JSON.parse(shapeText);
                        object.category = shape.type;
                        object.shape = shape;
                        object.figure = shape.figure;
                        let colorScheme = OpenArchiWrapper.getPrimaryColorScheme(shape);
                        object.fill = colorScheme.fillColor;
                        object.stroke = colorScheme.strokeColor;
                    }
                }).fail((jqXHR, textStatus, errorThrown) => alert(errorThrown));
            } else {
                object.category = shape.type;
                object.shape = shape;
                object.figure = shape.figure;
                let colorScheme = OpenArchiWrapper.getPrimaryColorScheme(shape);
                object.fill = colorScheme.fillColor;
                object.stroke = colorScheme.strokeColor;
            }
            object.isGroup = OpenArchiWrapper.toIsGroup(node);
        }
        return object;
    }
}