function storeNodes(node, nodes, result) {
    if (node === undefined) {
        return;
    }
    if (node.group === undefined) {
        result.push(node);
        nodes.remove(node);
    } else {
        let parent = findParent(node.group, result);
        if (parent === undefined) {
            nodes.forEach(function (node_) {
                let parent = findParent(node.group, result);
                if (parent === undefined) {
                    parent = findParent(node.group, nodes);
                    if (parent !== undefined) {
                        storeNodes(parent, nodes, result);
                    } else {
                        result.push(node_);
                        nodes.remove(node_);
                    }
                }
            });
        }
        result.push(node);
        nodes.remove(node);
    }
}

function sortNodes(nodes, result) {
    let nodesCloned = nodes.clone();
    nodes.forEach(function (node_) {
        let storedNode = findParent(node_.key, result);
        if (storedNode === undefined) {
            storeNodes(node_, nodesCloned, result);
        }
    });
    return result;
}

class OpenArchiFromDiagram {

    static _innerProcess(node, nodes, links, parent, isPrototyper) {
        switch (node.kind) {
            case "FLOWCHART_MODEL":
                OpenArchiFromDiagram.flowchartModel(parent, node, links);
                break;
            case "SEQUENCE_MODEL":
                OpenArchiFromDiagram.sequenceModel(parent, node, links);
                break;
            case "GANTT_MODEL":
                OpenArchiFromDiagram.ganttModel(parent, node, links);
                break;
            case "ENTITY_RELATIONSHIP_MODEL":
                OpenArchiFromDiagram.entityRelationshipModel(parent, node, links);
                break;
            case "UML_CLASS_MODEL":
                OpenArchiFromDiagram.umlModel(parent, node, links);
                break;
            case "BPM_MODEL":
                OpenArchiFromDiagram.bpmModel(parent, node, links);
                break;
            case "ARCHITECTURE_MODEL":
                OpenArchiFromDiagram.architectureModel(parent, node, links, nodes, isPrototyper);
                break;
            case "LAYER":
            case "SYSTEM":
            case "CONTAINER":
            case "COMPONENT":
                OpenArchiFromDiagram.architectureModel(parent, node, links, nodes, isPrototyper);
                break;
            default:
                console.log("Still not implemented");
        }
        return parent;
    }

    static innerProcess(nodes, links, parent, isPrototyper) {
        if (nodes !== undefined && nodes !== null && !_.isEmpty(nodes)) {
            const node = nodes.shift();
            if (!alreadyProcessedNodes.includes(node.key)) {
                parent = OpenArchiFromDiagram._innerProcess(node, nodes, links, parent, isPrototyper);
            }
            parent = OpenArchiFromDiagram.innerProcess(nodes, links, parent, isPrototyper);
        }
        return parent;
    }

    static process(diagram_) {
        let model = {};
        let diagram = diagram_.diagram;
        let meta = diagram_.meta;
        delete diagram.class;
        let nodes = diagram.model.nodeDataArray;
        let links = diagram.model.linkDataArray;
        if (Commons.prototype.isEmpty(nodes)) {
            return model;
        }
        const isPrototyper = diagram_.isPrototyper;
        model.status = meta.status || "INITIAL";
        if (meta.id) {
            model.id = meta.id;
        }
        model.name = meta.name;
        model.kind = meta.kind;
        model.description = meta.description;
        model.prototype = diagram_.isPrototyper;
        model.shape = {type: model.kind};
        alreadyProcessedNodes = [];
        alreadyProcessedLinks = [];
        if (!Commons.prototype.isEmpty(nodes)) {
            let fixedNodes = [];
            sortNodes(nodes, fixedNodes);
            nodes = fixedNodes;
            let node = nodes.shift();
            const rootNodes = diagram.findTreeRoots();
            if (rootNodes !== undefined) {
                let count = 0;
                rootNodes.each(function () {
                    count++;
                });
                if (count === 1) {
                    model = OpenArchiFromDiagram.common(node, isPrototyper);
                    model.rank = {};
                    model.rank[meta.tempKey] = 1;
                    alreadyProcessedNodes.push(model.key);
                } else {
                    model.name = diagram_.meta.name || "New Model";
                    model.shape = diagram_.diagramType;
                    model.kind = model.shape.type;
                    model.key = Commons.prototype.random();
                    nodes.unshift(node);
                    let rankCount = 1;
                    rootNodes.each(function (node_) {
                        nodes.find(function (element) {
                            if (node_.key === element.key) {
                                element.group = model.key;
                                element.rank = {};
                                element.rank["'" + meta.tempKey + "'"] = rankCount;
                                rankCount++;
                            }
                        });
                    });
                }
            }
            model = OpenArchiFromDiagram.innerProcess(nodes, links, model, isPrototyper);
        }
        return model;
    }

    static common(node, isPrototyper) {
        let object = {};
        object.id = node.id;
        object.key = node.key === undefined ? undefined : node.key.toString();
        if ((object.key < 0 || object.key === undefined) && object.id !== undefined) {
            object.key = object.id;
        }
        object.meta = node.meta;
        object.status = node.status || "INITIAL";
        object.name = node.name;
        object.kind = node.kind;
        object.description = node.description;
        object.prototype = isPrototyper;
        object = OpenArchiFromDiagram.fillShape(object, node);
        let image = node.image;
        if (image) {
            let raw = image.raw;
            try {
                raw = window.atob(raw.replace(/^data:image\/svg\+xml;base64,/, ""));
            } catch (err) {
                //Do nothing.
            }
            object.image = {raw: raw, type: image.type};
            if (image.id) {
                object.image.id = image.id;
            }
        }
        object.metaData = node.metaData;
        if (object.metaData !== undefined) {
            let views = object.metaData.views;
            if (views === undefined || _.isEmpty(views)) {
                views = [];
                views.push({parentId: node.id});
                object.metaData.views = views;
            } else {
                let found = false;
                for (let i = 0; i < views.length; i++) {
                    const view = views[i];
                    if (view.parentId === node.id) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    views.push({parentId: node.id});
                }
            }
        }
        delete object.group;
        delete object.isGroup;
        return object;
    }

    static addLinks(element, links) {
        if (element !== undefined && links !== undefined && Array.isArray(links) && links.length > 0) {
            if (element.relationships === undefined) {
                element.relationships = [];
            }
            links.forEach(link => {
                if (link.from.toString() === element.key.toString() || link.to.toString() === element.key.toString()) {
                    let relationship = {};
                    relationship.source = {};
                    relationship.destination = {};
                    relationship.source.id = link.from.toString();
                    relationship.destination.id = link.to.toString();
                    element.relationships.push(relationship);
                    alreadyProcessedLinks.push(relationship);
                }
            });
            if (element.relationships.length === 0) {
                delete element.relationships;
            }
        }
    }

    static processBasic(node, links, isPrototyper) {
        let element = OpenArchiFromDiagram.common(node, isPrototyper);
        OpenArchiFromDiagram.addLinks(element, links);
        alreadyProcessedNodes.push(element.key);
        return element;
    }

    static processInner(node, parent, links, child, isPrototyper) {
        if (parent !== undefined) {
            let element = OpenArchiFromDiagram.processBasic(node, links, isPrototyper);
            if (!parent[child]) {
                parent[child] = [];
            }
            parent[child].push(element);
        }
    }

    static architectureModel(model, node, links, nodes, isPrototyper) {
        let parent;
        if (node !== undefined) {
            parent = findParent(node.group, model);
            if (parent === undefined) {
                parent = findParent(node.group, nodes);
                if (parent !== undefined) {
                    parent = OpenArchiFromDiagram.processBasic(parent, links, isPrototyper);
                    OpenArchiFromDiagram._innerProcess(parent, nodes, links, model, isPrototyper);
                } else {
                    if (model.id !== undefined && model.id === node.id) {
                        model = OpenArchiFromDiagram.processBasic(node, links, isPrototyper);
                    } else {
                        model = OpenArchiFromDiagram.processBasic(model, links, isPrototyper);
                    }
                    parent = model;
                }
            }
            if (node.kind === "LAYER") {
                OpenArchiFromDiagram.processInner(node, parent, links, "layers", isPrototyper);
            } else if (node.kind === "SYSTEM") {
                OpenArchiFromDiagram.processInner(node, parent, links, "systems", isPrototyper);
            } else if (node.kind === "CONTAINER") {
                OpenArchiFromDiagram.processInner(node, parent, links, "containers", isPrototyper);
            } else if (node.kind === "COMPONENT") {
                OpenArchiFromDiagram.processInner(node, parent, links, "components", isPrototyper);
            }
            return parent;
        }
    }

    static flowchartModel(model, node, links) {
    }

    static sequenceModel(model, node, links) {
    }

    static ganttModel(model, node, links) {
    }

    static entityRelationshipModel(model, node, links) {
    }

    static umlModel(model, node, links) {
    }

    static bpmModel(model, node, links) {
    }


    static toEndpoint(model) {
        switch (model.kind) {
            case "PERSON":
                return "/api/persons";
                break;
            case "ARCHITECTURE_MODEL":
                return "/api/models";
                break;
            case "FLOWCHART_MODEL":
                return "/api/flowchart-models";
                break;
            case "SEQUENCE_MODEL":
                return "/api/sequence-models";
                break;
            case "GANTT_MODEL":
                return "/api/gantt-models";
                break;
            case "ENTITY_RELATIONSHIP_MODEL":
                return "/api/entity-relationship-models";
                break;
            case "UML_CLASS_MODEL":
                return "/api/class-models";
                break;
            case "BPM_MODEL":
                return "/api/diagrams/architectures/bpms";
                break;
            case "COMPONENT":
                return "/api/diagrams/architectures/components";
                break;
            case "CONSUMER":
                return "/api/diagrams/architectures/consumers";
                break;
            case "CONTAINER":
                return "/api/diagrams/architectures/containers";
                break;
            case "SYSTEM":
                return "/api/diagrams/architectures/systems";
                break;
            case "LAYER":
                return "/api/diagrams/architectures/layers";
                break;
            default:
                break;
        }
    }

    static fillShape(object, node) {
        let shape = node.shape;
        if (shape) {
            if (shape.colorSchemes === undefined) {
                $.ajax({
                    url: basePath + "/api/catalogs/element-types/" + shape.type + "/shape",
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
                            object.shape = shape;
                        }
                    }
                ).fail((jqXHR, textStatus, errorThrown) => alert(errorThrown));
            } else {
                object.shape = shape;
            }
        }
        delete object.group;
        delete object.isGroup;
        return object;
    }
}