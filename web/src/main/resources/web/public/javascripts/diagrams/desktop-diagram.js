'use strict';

let diagrams = {};

function DesktopDiagram(options) {
    if (options === undefined || !_.isObject(options) || _.isEmpty(options)) {
        options = {
            id: Commons.prototype.random(),
            diagramType: {
                type: 'DEFAULT'
            },
            nodeDataArray: [],
            linkDataArray: [],
            isPrototyper: true,
            meta: {tempKey: Commons.prototype.random()}
        }
    } else {
        if (options.diagramType === undefined || _.isEmpty(options.diagramType)) {
            throw new Error("It's not possible to create a diagram without a valid diagramType")
        }
        if (options.meta === undefined) {
            options.meta = {tempKey: Commons.prototype.random()};
        }
    }
    //Check for Diagram object existence as "diagram" element in DOM

    this.id = options.id;
    this.key = options.key;
    this.suffix = "-" + this.id;
    this.diagramType = options.diagramType;
    this.paletteBasic = undefined;
    this.paletteGeneral = undefined;
    this.diagram = undefined;
    this.linksSubDiagrams = {};
    this.nodeDataArray = options.nodeDataArray;
    this.linkDataArray = options.linkDataArray;
    this.isPrototyper = options.isPrototyper;
    this.meta = options.meta;
    this.fillPalettes = function () {
        retrievePalette(this, fixPalette);
    };
    this.addNode = function (e, node) {
        externalObjectsDropped(e, node, this, true);
    };
    this.addSubDiagram = function (type, diagram, fromId, toId) {
        addSubDiagram(this.id, type, diagram, fromId, toId);
    };
    this.findSubDiagram = function (type, fromId, toId) {
        return findSubDiagram(this, type, fromId, toId);
    };
    this.removeSubDiagram = function (type, fromId, toId) {
        return removeSubDiagram(this, type, fromId, toId);
    };

    function init(desktopDiagram) {
        const suffix = desktopDiagram.suffix;
        const type = desktopDiagram.diagramType.type;
        switch (type) {
            case "ARCHITECTURE_MODEL":
                initArchitectureModel(desktopDiagram);
                //Adding this into global diagrams variable
                diagrams[desktopDiagram.id] = desktopDiagram;
                break;
            case "COMPONENT_MODEL":
                initComponentModel(desktopDiagram);
                //Adding this into global diagrams variable
                diagrams[desktopDiagram.id] = desktopDiagram;
                break;
            case "VIEW":
                break;
            default:
                console.log("Still not implemented");
                break;
        }
        if (_.isEmpty(desktopDiagram.diagram)) {
            return null;
        }
        desktopDiagram.diagram.desktopId = desktopDiagram.id;
        desktopDiagram.paletteBasic.desktopId = desktopDiagram.id;
        desktopDiagram.paletteGeneral.desktopId = desktopDiagram.id;
        desktopDiagram.diagram.requestUpdate();
        // when the document is modified, add a "*" to the title and enable the "Save" button
        desktopDiagram.diagram.addDiagramListener("Modified", function (e) {
            let button = $("#SaveButton" + suffix);
            button.attr('disabled', !desktopDiagram.diagram.isModified);
            let idx = document.title.indexOf("*");
            if (desktopDiagram.diagram.isModified) {
                if (idx < 0) document.title += "*";
            } else {
                if (idx >= 0) document.title = document.title.substr(0, idx);
            }
        });

        const $body = $('body');
        const windowHeight = $(window).height();
        $body.attr({style: 'height: ' + windowHeight + 'px; min-height: ' + windowHeight + 'px;'});

        desktopDiagram.fillPalettes();
        desktopDiagram.paletteBasic.requestUpdate();
        desktopDiagram.paletteGeneral.requestUpdate();
        $.extend({}, desktopDiagram);
        fixMeta(desktopDiagram);
    }

    function initArchitectureModel(desktopDiagram) {

        const suffix = desktopDiagram.suffix;
        let diagram = desktopDiagram.diagram;
        let paletteBasic = desktopDiagram.paletteBasic;
        let paletteGeneral = desktopDiagram.paletteGeneral;
        const desktopDiagramId = desktopDiagram.id;
        //Basic elements palette
        if (paletteBasic !== undefined) {
            paletteBasic.clear();
            paletteBasic.div = null;
        }
        // initialize the Palette that is on the left side of the page
        // noinspection JSUndeclaredVariable
        paletteBasic = createPaletteBasic(suffix);
        paletteBasic.nodeTemplateMap.add("DEFAULT", newElementTemplate);
        paletteBasic.nodeTemplateMap.add("PERSON", personTemplate);
        paletteBasic.nodeTemplateMap.add("CONSUMER", consumerTemplate);
        paletteBasic.nodeTemplateMap.add("", defaultTemplate);
        desktopDiagram.paletteBasic = paletteBasic;
        //General elements palette
        if (paletteGeneral !== undefined) {
            paletteGeneral.clear();
            paletteGeneral.div = null;
        }
        // initialize the Palette that is on the left side of the page
        // noinspection JSUndeclaredVariable
        paletteGeneral = createPaletteGeneral(suffix);
        paletteGeneral.nodeTemplateMap.add("DEFAULT", newElementTemplate);
        paletteGeneral.nodeTemplateMap.add("PERSON", personTemplate);
        paletteGeneral.nodeTemplateMap.add("CONSUMER", consumerTemplate);
        paletteGeneral.nodeTemplateMap.add("", defaultTemplate);
        desktopDiagram.paletteGeneral = paletteGeneral;
        if (diagram !== undefined) {
            diagram.clear();
            diagram.div = null;
        }
        diagram =
            gojs(go.Diagram, "diagramDiv" + suffix,  // create a Diagram for the DIV HTML element
                {
                    // position the graph in the middle of the diagram
                    initialContentAlignment: go.Spot.Center,
                    hoverDelay: 300,
                    // use a custom ResizingTool (along with a custom ResizeAdornment on each Group)
                    resizingTool: new LaneResizingTool(),
                    // use a simple layout that ignores links to stack the top-level Pool Groups next to each other
                    layout: gojs(PoolLayout),
                    // a clipboard copied node is pasted into the original node's group (i.e. lane).
                    "commandHandler.copiesGroupKey": true,
                    // automatically re-layout the swim lanes after dragging the selection
                    //"SelectionMoved": relayoutDiagram,  // this DiagramEvent listener is
                    //"SelectionCopied": relayoutDiagram, // defined above
                    "animationManager.isEnabled": false,

                    mouseDrop: function (e) {
                        finishDrop(e, null);
                    },
                    /*                layout:  // Diagram has simple horizontal layout
                                        gojs(go.GridLayout,
                                            { wrappingWidth: Infinity, alignment: go.GridLayout.Position, cellSize: new go.Size(1, 1) }),*/
                    // allow Ctrl-G to call groupSelection()
                    "commandHandler.archetypeGroupData": {name: "Group", isGroup: true, color: "blue"},
                    "LinkDrawn": showLinkLabel,  // this DiagramEvent listener is defined below
                    "LinkRelinked": showLinkLabel,
                    scrollsPageOnFocus: false,
                    allowDrop: true,  // must be true to accept drops from the Palette
                    // enable undo & redo
                    "undoManager.isEnabled": true,
                    initialAutoScale: go.Diagram.Uniform,
                    click: hideAllContextualMenu
                });
        const newElementTemplate_ = newElementTemplate;
        newElementTemplate_.mouseEnter = function (e, obj) {
            showPorts(obj.part, true);
        };
        newElementTemplate_.mouseLeave = function (e, obj) {
            showPorts(obj.part, false);
        };
        diagram.nodeTemplateMap.add("DEFAULT", newElementTemplate_);
        const personTemplate_ = personTemplate;
        personTemplate_.mouseEnter = function (e, obj) {
            showPorts(obj.part, true);
        };
        personTemplate_.mouseLeave = function (e, obj) {
            showPorts(obj.part, false);
        };
        diagram.nodeTemplateMap.add("PERSON", personTemplate_);
        const consumerTemplate_ = consumerTemplate;
        consumerTemplate_.mouseEnter = function (e, obj) {
            showPorts(obj.part, true);
        };
        consumerTemplate_.mouseLeave = function (e, obj) {
            showPorts(obj.part, false);
        };
        diagram.nodeTemplateMap.add("CONSUMER", consumerTemplate_);
        diagram.groupTemplateMap.add("ARCHITECTURE_MODEL", architectureModelTemplate);
        diagram.groupTemplateMap.add("LAYER", layerTemplate);
        diagram.groupTemplateMap.add("SYSTEM", systemTemplate);
        diagram.groupTemplateMap.add("CONTAINER", containerTemplate);
        diagram.nodeTemplateMap.add("COMPONENT", componentTemplate);
        diagram.nodeTemplateMap.add("", basicElement);

        // The link shape and arrowhead have their stroke brush data bound to the "color" property
        diagram.linkTemplate =
            gojs(go.Link,  // the whole link panel
                {
                    routing: go.Link.AvoidsNodes,
                    curve: go.Link.JumpOver,
                    corner: 5,
                    toShortLength: 4,
                    relinkableFrom: true,
                    relinkableTo: true,
                    reshapable: true,
                    resegmentable: true,
                    // mouse-overs subtly highlight links:
                    mouseEnter: function (e, link) {
                        link.findObject("HIGHLIGHT").stroke = "rgba(30,144,255,0.2)";
                    },
                    mouseLeave: function (e, link) {
                        link.findObject("HIGHLIGHT").stroke = "transparent";
                    },
                    doubleClick: function(e, link) {
                        const data = link.data;
                        const fromId = data.from;
                        const toId = data.to;
                        const model = link.diagram.model;
                        const fromName = model.findNodeDataForKey(fromId).name;
                        const toName = model.findNodeDataForKey(toId).name;
                        const desktopDiagram = diagrams[desktopDiagramId];
                        const featuresModel = getFeaturesModel(desktopDiagram, fromId, data.to)
                        const nodeDataArray = [
                            {
                                id: fromId,
                                key: fromName,
                                fields: featuresModel.nodeDataFromArray,
                                loc: "0 0"
                            },
                            {
                                id: toId,
                                key: toName,
                                fields: featuresModel.nodeDataToArray,
                                loc: "250 0"
                            }
                        ];

                        createFeaturesDialog(desktopDiagramId, nodeDataArray, featuresModel.linkDataArray, data.from, data.to, link);
                    }
                },
                new go.Binding("points").makeTwoWay(),
                gojs(go.Shape,  // the highlight shape, normally transparent
                    {isPanelMain: true, strokeWidth: 8, stroke: "transparent", name: "HIGHLIGHT"}),
                gojs(go.Shape,  // the link path shape
                    {isPanelMain: true, stroke: "gray", strokeWidth: 2}),
                gojs(go.Shape,  // the arrowhead
                    {toArrow: "standard", stroke: null, fill: "gray"}),
                gojs(go.Panel, "Auto",  // the link label, normally not visible
                    {visible: false, name: "LABEL", segmentIndex: 2, segmentFraction: 0.5},
                    new go.Binding("visible", "visible").makeTwoWay(),
                    gojs(go.Shape, "RoundedRectangle",  // the label shape
                        {fill: "#F8F8F8", stroke: null}),
                    gojs(go.TextBlock, "Yes",  // the label
                        {
                            textAlign: "center",
                            font: "10pt helvetica, arial, sans-serif",
                            stroke: "#333333",
                            editable: true
                        },
                        new go.Binding("text", "", OpenArchiWrapper.toName).makeTwoWay(OpenArchiWrapper.fromName))
                ),
                { // this tooltip Adornment is shared by all links
                    toolTip:
                        gojs(go.Adornment, "Auto",
                            gojs(go.Shape, {fill: "#FFFFCC"}),
                            gojs(go.TextBlock, {margin: 4},  // the tooltip shows the result of calling linkInfo(data)
                                new go.Binding("text", "", linkInfo))
                        ),
                    // the same context menu Adornment is shared by all links
                    contextMenu: partContextMenu
                }
            );

        diagram.toolManager.linkingTool.temporaryLink =
          gojs(go.Link,
            { layerName: "Tool", routing: go.Link.AvoidsNodes, corner: 5 },
            gojs(go.Shape,
              {
                stroke: "red",
                strokeWidth: 2,
                strokeDashArray: [4, 2]
              })
          );

        diagram.toolManager.relinkingTool.temporaryLink.routing = go.Link.AvoidsNodes;

        const tempfromnode =
          gojs(go.Node,
            { layerName: "Tool" },
            gojs(go.Shape, "RoundedRectangle",
              {
                stroke: "chartreuse",
                strokeWidth: 3,
                fill: null,
                portId: "",
                width: 1,
                height: 1
              })
          );

        diagram.toolManager.linkingTool.temporaryFromNode = tempfromnode;
        diagram.toolManager.linkingTool.temporaryFromPort = tempfromnode.port;

        const temptonode =
          gojs(go.Node,
            { layerName: "Tool" },
            gojs(go.Shape, "RoundedRectangle",
              {
                stroke: "cyan",
                strokeWidth: 3,
                fill: null,
                portId: "",
                width: 1,
                height: 1
              })
          );

        diagram.toolManager.linkingTool.temporaryToNode = temptonode;
        diagram.toolManager.linkingTool.temporaryToPort = temptonode.port;

        desktopDiagram.diagram = diagram;

        // Groups consist of a title in the color given by the group node data
        // above a translucent gray rectangle surrounding the member parts
        diagram.groupTemplate =
            gojs(go.Group, "Vertical",
                {
                    selectionObjectName: "PANEL",  // selection handle goes around shape, not label
                    ungroupable: true
                },  // enable Ctrl-Shift-G to ungroup a selected Group
                gojs(go.TextBlock,
                    {
                        name: "text",
                        font: "bold 19px sans-serif",
                        isMultiline: false,  // don't allow newlines in text
                        editable: true  // allow in-place editing by user
                    },
                    new go.Binding("text", "", OpenArchiWrapper.toName).makeTwoWay(OpenArchiWrapper.fromName),
                    new go.Binding("stroke", "", OpenArchiWrapper.toFillSecondary)),
                gojs(go.Panel, "Auto",
                    {name: "PANEL"},
                    gojs(go.Shape, "Rectangle",  // the rectangular shape around the members
                        {
                            fill: "rgba(128,128,128,0.2)",
                            stroke: "gray",
                            strokeWidth: 3,
                            portId: "",
                            cursor: "pointer",  // the Shape is the port, not the whole Node
                            // allow all kinds of links from and to this port
                            fromLinkable: true,
                            fromLinkableSelfNode: true,
                            fromLinkableDuplicates: true,
                            toLinkable: true,
                            toLinkableSelfNode: true,
                            toLinkableDuplicates: true
                        },
                        new go.Binding("fill", "", OpenArchiWrapper.toFillPrimary).makeTwoWay(OpenArchiWrapper.fromFillPrimary),
                        new go.Binding("stroke", "", OpenArchiWrapper.toStrokePrimary).makeTwoWay(OpenArchiWrapper.fromStrokePrimary)),
                    gojs(go.Placeholder, {margin: 10, background: "transparent"})  // represents where the members are
                ),
                { // this tooltip Adornment is shared by all groups
                    toolTip:
                        gojs(go.Adornment, "Auto",
                            gojs(go.Shape, {fill: "#FFFFCC"}),
                            gojs(go.TextBlock, {margin: 4},
                                // bind to tooltip, not to Group.data, to allow access to Group properties
                                new go.Binding("text", "", groupInfo).ofObject())
                        ),
                    // the same context menu Adornment is shared by all groups
                    contextMenu: partContextMenu
                }
            );

        // define a custom resize adornment that has two resize handles if the group is expanded
        diagram.groupTemplate.resizeAdornmentTemplate = commonGroupResizeAdornmentTemplate;

        // Define the behavior for the Diagram background:
        diagram.toolTip = commonToolTip;

        // provide a context menu for the background of the Diagram, when not over any Part
        diagram.contextMenu = commonContextMenu;

        fillDiagram(desktopDiagram);

        // this DiagramEvent is raised when the user has drag-and-dropped something from another Diagram into this one
        diagram.addDiagramListener("ExternalObjectsDropped", function (e) {
            externalObjectsDropped(e, undefined, desktopDiagram);
        });
        diagram.addDiagramListener("BackgroundDoubleClicked", function (e) {
            backgroundDoubleClicked(e, undefined, desktopDiagram);
        });
        diagram.addDiagramListener("LinkDrawn", function(e) {
            const link = e.subject;
            const from = link.fromNode;
            const to = link.toNode;
            const fromData = from.data;
            const toData = to.data;
            const fromName = fromData.name;
            const toName = toData.name;
            let fromId = fromData.id;
            if (fromId === undefined) {
                fromId = fromData.key;
            }
            let toId = toData.id;
            if (toId === undefined) {
                toId = toData.key;
            }
            const featuresModel = getFeaturesModel(desktopDiagram, fromId, toId)
            const nodeDataArray = [
                {
                    id: fromId,
                    key: fromName,
                    fields: featuresModel.nodeDataFromArray,
                    loc: "0 0"
                },
                {
                    id: toId,
                    key: toName,
                    fields: featuresModel.nodeDataToArray,
                    loc: "250 0"
                }
            ];
            createFeaturesDialog(desktopDiagramId, nodeDataArray, featuresModel.linkDataArray, fromId, toId, link);
        });
        diagram.addModelChangedListener(function(e) {

            if (e.change === go.ChangedEvent.Remove && e.propertyName === "linkDataArray") {
                const data = e.oldValue;
                const fromId = data.from;
                const toId = data.to;
                desktopDiagram.removeSubDiagram("FEATURE", fromId, toId)
            }
        })
        diagram.toolManager.linkingTool.linkValidation = uniqueLink;
        diagram.toolManager.relinkingTool.linkValidation = uniqueLink;
    }

    function initComponentModel(desktopDiagram) {
        const suffix = desktopDiagram.suffix;
        let diagram = desktopDiagram.diagram;
        let paletteBasic = desktopDiagram.paletteBasic;
        let paletteGeneral = desktopDiagram.paletteGeneral;

        //Basic elements palette
        if (paletteBasic !== undefined) {
            paletteBasic.clear();
            paletteBasic.div = null;
        }
        // initialize the Palette that is on the left side of the page
        // noinspection JSUndeclaredVariable
        paletteBasic = createPaletteBasic(suffix);
        paletteBasic.nodeTemplateMap.add("DEFAULT", newElementTemplate);
        paletteBasic.nodeTemplateMap.add("", defaultTemplate);
        desktopDiagram.paletteBasic = paletteBasic;

        //Basic elements palette
        if (paletteGeneral !== undefined) {
            paletteGeneral.clear();
            paletteGeneral.div = null;
        }
        // initialize the Palette that is on the left side of the page
        // noinspection JSUndeclaredVariable
        paletteGeneral = createPaletteGeneral(suffix);
        paletteGeneral.nodeTemplateMap.add("DEFAULT", newElementTemplate);
        paletteGeneral.nodeTemplateMap.add("", defaultTemplate);
        desktopDiagram.paletteGeneral = paletteGeneral;

        if (diagram !== undefined) {
            diagram.clear();
            diagram.div = null;
        }
        diagram =
            gojs(go.Diagram, "diagramDiv" + suffix,  // create a Diagram for the DIV HTML element
                {
                    // position the graph in the middle of the diagram
                    initialContentAlignment: go.Spot.Center,
                    hoverDelay: 300,
                    // use a custom ResizingTool (along with a custom ResizeAdornment on each Group)
                    resizingTool: new LaneResizingTool(),
                    // use a simple layout that ignores links to stack the top-level Pool Groups next to each other
                    layout: gojs(PoolLayout),
                    // a clipboard copied node is pasted into the original node's group (i.e. lane).
                    "commandHandler.copiesGroupKey": true,
                    // automatically re-layout the swim lanes after dragging the selection
                    //"SelectionMoved": relayoutDiagram,  // this DiagramEvent listener is
                    //"SelectionCopied": relayoutDiagram, // defined above
                    "animationManager.isEnabled": false,

                    mouseDrop: function (e) {
                        finishDrop(e, null);
                    },
                    /*                layout:  // Diagram has simple horizontal layout
                                        gojs(go.GridLayout,
                                            { wrappingWidth: Infinity, alignment: go.GridLayout.Position, cellSize: new go.Size(1, 1) }),*/
                    // allow Ctrl-G to call groupSelection()
                    "commandHandler.archetypeGroupData": {name: "Group", isGroup: true, color: "blue"},
                    "LinkDrawn": showLinkLabel,  // this DiagramEvent listener is defined below
                    "LinkRelinked": showLinkLabel,
                    scrollsPageOnFocus: false,
                    allowDrop: true,  // must be true to accept drops from the Palette
                    // enable undo & redo
                    "undoManager.isEnabled": true,
                    initialAutoScale: go.Diagram.Uniform
                });
        diagram.groupTemplateMap.add("COMPONENT_MODEL", componentModelTemplate);
        diagram.groupTemplateMap.add("LAYER", layerTemplate);
        diagram.groupTemplateMap.add("GROUP", groupTemplate);

        diagram.nodeTemplateMap.add("DEFAULT", newElementTemplate);
        diagram.nodeTemplateMap.add("ELEMENT", elementTemplate);
        diagram.nodeTemplateMap.add("", basicElement);

        // Groups consist of a title in the color given by the group node data
        // above a translucent gray rectangle surrounding the member parts
        diagram.groupTemplate =
            gojs(go.Group, "Vertical",
                {
                    selectionObjectName: "PANEL",  // selection handle goes around shape, not label
                    ungroupable: true
                },  // enable Ctrl-Shift-G to ungroup a selected Group
                gojs(go.TextBlock,
                    {
                        name: "text",
                        font: "bold 19px sans-serif",
                        isMultiline: false,  // don't allow newlines in text
                        editable: true  // allow in-place editing by user
                    },
                    new go.Binding("text", "", OpenArchiWrapper.toName).makeTwoWay(OpenArchiWrapper.fromName),
                    new go.Binding("stroke", "", OpenArchiWrapper.toFillSecondary)),
                gojs(go.Panel, "Auto",
                    {name: "PANEL"},
                    gojs(go.Shape, "Rectangle",  // the rectangular shape around the members
                        {
                            fill: "rgba(128,128,128,0.2)",
                            stroke: "gray",
                            strokeWidth: 3,
                            portId: "",
                            cursor: "pointer",  // the Shape is the port, not the whole Node
                            // allow all kinds of links from and to this port
                            fromLinkable: true,
                            fromLinkableSelfNode: true,
                            fromLinkableDuplicates: true,
                            toLinkable: true,
                            toLinkableSelfNode: true,
                            toLinkableDuplicates: true
                        },
                        new go.Binding("fill", "", OpenArchiWrapper.toFillPrimary).makeTwoWay(OpenArchiWrapper.fromFillPrimary),
                        new go.Binding("stroke", "", OpenArchiWrapper.toStrokePrimary).makeTwoWay(OpenArchiWrapper.fromStrokePrimary)),
                    gojs(go.Placeholder, {margin: 10, background: "transparent"})  // represents where the members are
                ),
                { // this tooltip Adornment is shared by all groups
                    toolTip:
                        gojs(go.Adornment, "Auto",
                            gojs(go.Shape, {fill: "#FFFFCC"}),
                            gojs(go.TextBlock, {margin: 4},
                                // bind to tooltip, not to Group.data, to allow access to Group properties
                                new go.Binding("text", "", groupInfo).ofObject())
                        ),
                    // the same context menu Adornment is shared by all groups
                    contextMenu: partContextMenu
                }
            );

        // define a custom resize adornment that has two resize handles if the group is expanded
        diagram.groupTemplate.resizeAdornmentTemplate = commonGroupResizeAdornmentTemplate;
        diagram.toolTip = commonToolTip;

        // provide a context menu for the background of the Diagram, when not over any Part
        diagram.contextMenu = commonContextMenu;

        fillDiagram(desktopDiagram);

        // temporary links used by LinkingTool and RelinkingTool are also orthogonal:
        diagram.toolManager.linkingTool.temporaryLink.routing = go.Link.Orthogonal;
        diagram.toolManager.relinkingTool.temporaryLink.routing = go.Link.Orthogonal;

        // this DiagramEvent is raised when the user has drag-and-dropped something from another Diagram into this one
        diagram.addDiagramListener("ExternalObjectsDropped", function (e) {
            externalObjectsDropped(e, undefined, desktopDiagram);
        });
        diagram.addDiagramListener("BackgroundDoubleClicked", backgroundDoubleClicked);
        desktopDiagram.diagram = diagram;
    }

    function externalObjectsDropped(e, obj, desktopDiagram, isObjectComplete) {
        // stop any ongoing text editing

        const subject = e.subject;
        const diagram = e.diagram;
        const divId = diagram.div.id;
        const splittedIds = divId.split("-");
        let suffix = splittedIds.length > 1 ? "-" + splittedIds[1] : divId;
        let node;
        if (obj !== undefined) {
            node = obj;
        } else {
            node = diagram.findNodeForKey(subject.first().key);
        }
        const data = node.data;
        getPointedObject(e, function (targetObject) {
            if (targetObject !== undefined && targetObject !== null) {
                data.group = targetObject.key;
            }
        });
        if (!data.kind) {
            if (diagram.currentTool instanceof go.TextEditingTool) {
                diagram.currentTool.acceptText(go.TextEditingTool.LostFocus);
            }
            let elementTypesOptions = "";
            const elementTypes = desktopDiagram.diagramType.elementShapes;
            if (elementTypes !== undefined && !_.isEmpty(elementTypes)) {
                elementTypes.forEach(elementType => {
                    elementTypesOptions = elementTypesOptions + "<option value='" + JSON.stringify(elementType) + "'>" + elementType.type + "</option>\n";
                });
            }
            const content = "<div>\n" +
                "            <form data-role='validator' action='javascript:' novalidate='novalidate' data-role-validator='true' name='basic-element-form'>\n" +
                "                <input id='element-name" + suffix + "' class=mt-2 type='text' data-role='input' data-prepend='Name'/>\n" +
                "                <select id='element-types" + suffix + "' class=mt-2 data-prepend='Type ' data-role='select'>" +
                elementTypesOptions +
                "                </select>\n" +
                "                <input id='image" + suffix + "' class=mt-2 type='file' data-role='file' data-prepend='Image' data-button-title='<span class=mif-folder></span>' accept='image/svg+xml'/>\n" +
                "            </form>\n" +
                "         </div>";
            const dialog = Metro.dialog.create({
                title: "Basic element info",
                modal: true,
                show: true,
                content: content,
                actions: [
                    {
                        caption: "Continue",
                        cls: "primary js-dialog-close",
                        onclick: function (e) {
                            checkAndSave(suffix, desktopDiagram);
                        }
                    },
                    {
                        caption: "Dismiss",
                        cls: "js-dialog-close",
                        onclick: function (e) {
                            diagram.model.removeNodeData(data);
                        }
                    }
                ]
            });
            $("#image" + suffix).on("change", function (e) {
                handleImageSelect(e, desktopDiagram);
            });
            dialog.attr("id", "dialog" + suffix);
            desktopDiagram.key = data.key;
        } else {
            if (data.id) {
                if (!isObjectComplete) {
                    let param = {};
                    let uri = basePath + "/api/models/" + data.id;

                    if (!desktopDiagram.isPrototyper) {
                        param = {suffix: "cloned"};
                        uri = uri + "/clone";
                    }
                    $.get(uri, param)
                        .done(function (completeModel) {
                            expand(completeModel, desktopDiagram.diagram);
                            diagram.model.removeNodeData(data);
                        });
                } else {
                    expand(data, desktopDiagram.diagram);
                }
            }
        }
        fixMeta(desktopDiagram);
    }

    function backgroundDoubleClicked(e, obj, desktopDiagram) {
        const nodedata = {
            name: "New Element",
            shape: {type: "DEFAULT"}
        };
        let diagram;
        if (obj !== undefined) {
            diagram = obj.diagram || e.diagram;
        } else {
            diagram = e.diagram;
        }
        diagram.model.addNodeData(nodedata);
        const node = diagram.findNodeForData(nodedata);
        externalObjectsDropped(e, node, desktopDiagram);
    }

    function fillDiagram(desktopDiagram) {
        let nodeDataArray_ = desktopDiagram.nodeDataArray;
        let linkDataArray_ = desktopDiagram.linkDataArray;
        if (!Commons.prototype.isEmpty(nodeDataArray_)) {
            desktopDiagram.diagram.model = new go.GraphLinksModel(nodeDataArray_, linkDataArray_);
        }
    }

    init(this);
}

function addSubDiagram(desktopDiagramId, type, diagram, fromId, toId) {
    const desktopDiagram = diagrams[desktopDiagramId];
    const linkSubDiagram = findSubDiagram(desktopDiagram, type, fromId, toId);
    if (linkSubDiagram === undefined) {
        if (desktopDiagram.linksSubDiagrams[type] === undefined) {
            desktopDiagram.linksSubDiagrams[type] = []
        }
        desktopDiagram.linksSubDiagrams[type].push({fromId: fromId, toId: toId, diagram: diagram});
    } else {
        linkSubDiagram.diagram = diagram;
    }
}

function findSubDiagram(desktopDiagram, type, fromId, toId) {
    if (desktopDiagram === undefined) {
        throw new Error("Diagram with id '" + diagramId+ "' not found. It's not possible to add a sub diagram to null parent desktop diagram");
    }
    let linksSubDiagram = desktopDiagram.linksSubDiagrams[type];
    if (linksSubDiagram === undefined) {
        return;
    }
    return linksSubDiagram.find(
        function(element) {
            return element.fromId ===  fromId && element.toId ===  toId;
        }
    );
}

function removeSubDiagram(desktopDiagram, type, fromId, toId) {
    if (desktopDiagram === undefined) {
        throw new Error("Diagram with id '" + diagramId+ "' not found. It's not possible to remove a sub diagram to null parent desktop diagram");
    }
    let linksSubDiagram = desktopDiagram.linksSubDiagrams[type];
    if (linksSubDiagram === undefined) {
        return;
    }
    const linkSubDiagram = linksSubDiagram.find(
        function(element) {
            return element.fromId ===  fromId && element.toId ===  toId;
        }
    );
    for( let i = 0; i < linksSubDiagram.length; i++){
        if ( linksSubDiagram[i] === linkSubDiagram) {
            linksSubDiagram.splice(i, 1);
        }
    }
}

function getPaletteUri(type) {
    const prefix = basePath + "/api/palette/";
    switch (type) {
        case "ARCHITECTURE_MODEL":
            return prefix + "architectures";
        case "FLOWCHART_MODEL":
            return prefix + "flowcharts";
        case "SEQUENCE_MODEL":
            return prefix + "sequences";
        case "GANTT_MODEL":
            return prefix + "gantts";
        case "ENTITY_RELATIONSHIP_MODEL":
            return prefix + "entity-relationships";
        case "UML_CLASS_MODEL":
            return prefix + "uml-classes";
        case "BPM_MODEL":
            return prefix + "bpms";
        case "COMPONENT_MODEL":
            return prefix + "components";
        default:
            return prefix + "architectures";
    }
}

function retrievePalette(diagram, callback) {
    const type = diagram.diagramType.type;
    const url = getPaletteUri(type);
    $.ajax({
        url: url,
        type: 'GET',
        crossDomain: true,
        contentType: "application/json",
        converters: {
            "text json": function (response) {
                return (response === "") ? null : JSON.parse(response);
            }
        }
    }).done((data, textStatus, response) => {
            if (response.status === 200) {
                if (_.isFunction(callback)) {
                    callback(diagram, data);
                }
            }
        }
    );
}

function editText(e, button) {
    const node = button.part.adornedPart;
    e.diagram.commandHandler.editTextBlock(node.findObject("TEXTBLOCK"));
}

// used by nextColor as the list of colors through which we rotate
const myColors = ["lightgray", "lightblue", "lightgreen", "yellow", "orange", "pink"];

// used by both the Button Binding and by the changeColor click function
function nextColor(c) {
    let idx = myColors.indexOf(c);
    if (idx < 0) return "lightgray";
    if (idx >= myColors.length - 1) idx = 0;
    return myColors[idx + 1];
}

function changeColor(e, button) {
    const node = button.part.adornedPart;
    const shape = node.findObject("SHAPE");
    if (shape === null) return;
    node.diagram.startTransaction("Change color");
    shape.fill = nextColor(shape.fill);
    button["_buttonFillNormal"] = nextColor(shape.fill);  // update the button too
    node.diagram.commitTransaction("Change color");
}

function hideElement(e, button) {
    const node = button.part.adornedPart;
    node.diagram.startTransaction("Toggle visible");
    node.visible = !node.isVisible();
    node.diagram.commitTransaction("Toggle visible");
}

function drawLink(e, button) {
    const node = button.part.adornedPart;
    const tool = e.diagram.toolManager.linkingTool;
    tool.startObject = node.port;
    e.diagram.currentTool = tool;
    tool.doActivate();
}


function insertIntoArray(e, button) {
    var myDiagram =  button.part.adornedPart.diagram;
    var n = myDiagram.selection.first();
    if (n === null) return;
    var d = n.data;
    myDiagram.startTransaction("insertIntoTable");
    // add item as second in the list, at index #1
    // of course this new data could be more realistic:
    myDiagram.model.insertArrayItem(d.people, 1, {
        columns: [{ attr: "name", text: "Elena" },
            { attr: "phone", text: "456" },
            { attr: "office", text: "LA" }]
    });
    myDiagram.commitTransaction("insertIntoTable");
}

function removeFromArray(e, button) {
    var myDiagram =  button.part.adornedPart.diagram;
    var n = myDiagram.selection.first();
    if (n === null) return;
    var d = n.data;
    myDiagram.startTransaction("removeFromTable");
    // remove second item of list, at index #1
    myDiagram.model.removeArrayItem(d.people, 1);
    myDiagram.commitTransaction("removeFromTable");
}

// add or remove a column from the selected node's table of people

function findColumnDefinitionForName(nodedata, attrname) {
    var columns = nodedata.columnDefinitions;
    for (var i = 0; i < columns.length; i++) {
        if (columns[i].attr === attrname) return columns[i];
    }
    return null;
}

function findColumnDefinitionForColumn(nodedata, idx) {
    var columns = nodedata.columnDefinitions;
    for (var i = 0; i < columns.length; i++) {
        if (columns[i].column === idx) return columns[i];
    }
    return null;
}

function addColumn(e, button, attrname) {
    var myDiagram =  button.part.adornedPart.diagram;
    var n = myDiagram.selection.first();
    if (n === null) return;
    var d = n.data;
    // if name is not given, find an unused column name
    if (attrname === undefined || attrname === "") {
        attrname = "new";
        var count = 1;
        while (findColumnDefinitionForName(d, attrname) !== null) {
            attrname = "new" + (count++).toString();
        }
    }
    // find an unused column #
    var col = 3;
    while (findColumnDefinitionForColumn(d, col) !== null) {
        col++;
    }
    myDiagram.startTransaction("addColumn");
    var model = myDiagram.model;
    // add a column definition for the node's whole table
    model.addArrayItem(d.columnDefinitions, {
        attr: attrname,
        text: attrname,
        column: col
    });
    // add cell to each person in the node's table of people
    var people = d.people;
    for (var j = 0; j < people.length; j++) {
        var person = people[j];
        model.addArrayItem(person.columns, {
            attr: attrname,
            text: Math.floor(Math.random() * 1000).toString()
        });
    }
    myDiagram.commitTransaction("addColumn");
}

function removeColumn(e, button) {
    var myDiagram =  button.part.adornedPart.diagram;
    var n = myDiagram.selection.first();
    if (n === null) return;
    var d = n.data;
    var coldef = d.columnDefinitions[3];  // get the fourth column
    if (coldef === undefined) return;
    var attrname = coldef.attr;
    myDiagram.startTransaction("removeColumn");
    var model = myDiagram.model;
    model.removeArrayItem(d.columnDefinitions, 3);
    // update columns for each person in this table
    var people = d.people;
    for (var j = 0; j < people.length; j++) {
        var person = people[j];
        var columns = person.columns;
        for (var k = 0; k < columns.length; k++) {
            var cell = columns[k];
            if (cell.attr === attrname) {
                // get rid of this attribute cell from the person.columns Array
                model.removeArrayItem(columns, k);
                break;
            }
        }
    }
    myDiagram.commitTransaction("removeColumn");
}


// used by both clickNewNode and dragNewNode to create a node and a link
// from a given node to the new node
function createNodeAndLink(data, fromnode) {
    const diagram = fromnode.diagram;
    const model = diagram.model;
    const nodedata = model.copyNodeData(data);
    model.addNodeData(nodedata);
    const newnode = diagram.findNodeForData(nodedata);
    const linkdata = model.copyLinkData({});
    model.setFromKeyForLinkData(linkdata, model.getKeyForNodeData(fromnode.data));
    model.setToKeyForLinkData(linkdata, model.getKeyForNodeData(newnode.data));
    model.addLinkData(linkdata);
    diagram.select(newnode);
    return newnode;
}

// the Button.click event handler, called when the user clicks the "N" button
function clickNewNode(e, button) {
    let data = button._dragData;
    if (!data) return;
    e.diagram.startTransaction("Create Node and Link");
    const fromnode = button.part.adornedPart;
    const newnode = createNodeAndLink(button._dragData, fromnode);
    newnode.location = new go.Point(fromnode.location.x + 200, fromnode.location.y);
    e.diagram.commitTransaction("Create Node and Link");
}

// the Button.actionMove event handler, called when the user drags within the "N" button
function dragNewNode(e, button) {
    const tool = e.diagram.toolManager.draggingTool;
    if (tool.isBeyondDragSize()) {
        let data = button._dragData;
        if (!data) return;
        e.diagram.startTransaction("button drag");  // see doDeactivate, below
        const newnode = createNodeAndLink(data, button.part.adornedPart);
        newnode.location = e.diagram.lastInput.documentPoint;
        // don't commitTransaction here, but in tool.doDeactivate, after drag operation finished
        // set tool.currentPart to a selected movable Part and then activate the DraggingTool
        tool.currentPart = newnode;
        e.diagram.currentTool = tool;
        tool.doActivate();
    }
}

function diagramInfo(model) {  // Tooltip info for the diagram's model
    return "Model:\n" + model.nodeDataArray.length + " nodes, " + model.linkDataArray.length + " links";
}

function createPaletteBasic(suffix) {
    return gojs(go.Palette, "paletteDivBasic" + suffix, commonPaletteBasic);
}

function createPaletteGeneral(suffix) {
    return gojs(go.Palette, "paletteDivGeneral" + suffix, commonPaletteGeneral);
}

function getDiagramById(diagramId) {
    return diagrams[diagramId];
}

function loadChildren(diagramId) {
    const diagram = getDiagramById(diagramId);
    alert(diagram.id);
}

function loadParents(diagramId) {
    const diagram = getDiagramById(diagramId);
    alert(diagram.id);
}

function fixPalette(diagram, data) {
    diagram.paletteBasic.model.nodeDataArray = [];
    diagram.paletteBasic.model = new go.GraphLinksModel(OpenArchiWrapper.fixCategory(data.basicElements));
    diagram.paletteBasic.requestUpdate();
    diagram.paletteGeneral.model.nodeDataArray = [];
    diagram.paletteGeneral.model = new go.GraphLinksModel(OpenArchiWrapper.fixCategory(data.generalElements));
    diagram.paletteGeneral.requestUpdate();
}

const emptyAdornment = gojs(
    go.Adornment, "Auto",
    gojs(go.Shape, "RoundedRectangle",
        {
            fill: null,
            stroke: null
        }),
    gojs(go.Placeholder)
);

// this is shown by the mouseHover event handler
const nodeHoverAdornment = gojs(
    go.Adornment, "Spot",
    {
        background: "transparent",
        // hide the Adornment when the mouse leaves it
        zOrder: 2800
    },
    gojs(go.Panel, "Auto",
        gojs(go.Shape, {
            stroke: "#EF9B0F",
            strokeWidth: 4,
            fill: null
        }),
        gojs(go.Placeholder, {
            margin: 10
        })
    ),
    gojs(go.Panel, "Horizontal",
        {
            alignment: go.Spot.Top,
            alignmentFocus: go.Spot.Bottom
        },
        gojs("Button",
            {
                click: editText,
                margin: 5
            },  // defined below, to support editing the text of the node
            gojs(go.TextBlock, "t",
                {
                    font: "bold 10pt sans-serif",
                    desiredSize: new go.Size(15, 15),
                    textAlign: "center"
                })
        ),
        gojs("Button",
            {
                click: changeColor,
                "_buttonFillOver": "transparent",
                margin: 5
            },  // defined below, to support changing the color of the node
            new go.Binding("ButtonBorder.fill", "color", nextColor),
            gojs(go.Shape,
                {
                    fill: null,
                    stroke: null,
                    desiredSize: new go.Size(14, 14)
                })
        ),
        gojs("Button",
            {
                click: hideElement,
                margin: 5
            },  // defined below, to support editing the text of the node
            gojs(go.TextBlock, "Hide",
                {
                    font: "bold 10pt sans-serif",
                    desiredSize: new go.Size(15, 15),
                    textAlign: "center"
                })
        )),
    gojs(go.Panel, "Vertical",
        {
            alignment: go.Spot.Right,
            alignmentFocus: go.Spot.Left
        },
        gojs("Button",
            { // drawLink is defined below, to support interactively drawing new links
                click: drawLink,  // click on Button and then click on target node
                actionMove: drawLink,  // drag from Button to the target node
                margin: 5
            },
            gojs(go.Shape,
                {geometryString: "M0 0 L8 0 8 12 14 12 M12 10 L14 12 12 14"})
        ),
        gojs("Button",
            {
                actionMove: dragNewNode,  // defined below, to support dragging from the button
                _dragData: {text: "a Node", color: "lightgray"},  // node data to copy
                click: clickNewNode  // defined below, to support a click on the button
            },
            gojs(go.Shape,
                {geometryString: "M0 0 L3 0 3 10 6 10 x F1 M6 6 L14 6 14 14 6 14z", fill: "gray"})
        ),
    ),
    gojs(go.Panel, "Horizontal",
        {
            alignment: go.Spot.Bottom,
            alignmentFocus: go.Spot.Top
        },
        gojs("Button",
            {
                click: editText,
                margin: 5
            },  // defined below, to support editing the text of the node
            gojs(go.TextBlock, "t",
                {
                    font: "bold 10pt sans-serif",
                    desiredSize: new go.Size(15, 15),
                    textAlign: "center"
                })
        ),
        gojs("Button",
            {
                click: changeColor,
                "_buttonFillOver": "transparent",
                margin: 5
            },  // defined below, to support changing the color of the node
            new go.Binding("ButtonBorder.fill", "color", nextColor),
            gojs(go.Shape,
                {
                    fill: null,
                    stroke: null,
                    desiredSize: new go.Size(14, 14)
                })
        )),
    gojs(go.Panel, "Vertical",
        {
            alignment: go.Spot.Left,
            alignmentFocus: go.Spot.Right
        },
        gojs("Button",
            { // drawLink is defined below, to support interactively drawing new links
                click: drawLink,  // click on Button and then click on target node
                actionMove: drawLink,  // drag from Button to the target node
                margin: 5
            },
            gojs(go.Shape,
                {geometryString: "M0 0 L8 0 8 12 14 12 M12 10 L14 12 12 14"})
        ),
        gojs("Button",
            {
                actionMove: dragNewNode,  // defined below, to support dragging from the button
                _dragData: {text: "a Node", color: "lightgray"},  // node data to copy
                click: clickNewNode,  // defined below, to support a click on the button
                margin: 5
            },
            gojs(go.Shape,
                {geometryString: "M0 0 L3 0 3 10 6 10 x F1 M6 6 L14 6 14 14 6 14z", fill: "gray"})
        ),
    ),
);


// this is shown by the mouseHover event handler
const addColumnsHoverAdornment = gojs(
    go.Adornment, "Spot",
    {
        background: "transparent",
        // hide the Adornment when the mouse leaves it
        zOrder: 2800
    },
    gojs(go.Panel, "Auto",
        gojs(go.Shape, {
            stroke: "#EF9B0F",
            strokeWidth: 4,
            fill: null
        }),
        gojs(go.Placeholder, {
            margin: 10
        })
    ),

    gojs(go.Panel, "Vertical",
        {
            alignment: go.Spot.Right,
            alignmentFocus: go.Spot.Left
        },
        gojs("Button",
            { // drawLink is defined below, to support interactively drawing new links
                click: addColumn,
                margin: 5
            },
            gojs(go.Shape,
                {   fill: "green",
                    geometryString: "M666.9356889828118,320.02346922088 C665.2261481565545,317.0961732855076 662.9038267144923,314.77385184344564 659.9765307791201,313.0643110171882 C657.0453317825006,311.3547701909308 653.8448215598269,310.49999977780203 650.375000111099,310.49999977780203 c-3.469821448727955,0 -6.6703316714016605,0.8547704131287088 -9.60153066802111,2.564311239386127 C637.8461735077055,314.77385184344564 635.5238520656435,317.0961732855076 633.8143112393861,320.02346922088 C632.1047704131287,322.9546682174994 631.25,326.15517844017313 631.25,329.624999888901 c0,3.469821448727955 0.8547704131287088,6.6703316714016605 2.564311239386127,9.60153066802111 C635.5238520656435,342.15382649229446 637.8461735077055,344.4761479343565 640.7734694430778,346.18568876061386 c2.9311989966194534,1.7095408262574177 6.131709219293157,2.564311239386127 9.60153066802111,2.564311239386127 s6.6703316714016605,-0.8547704131287088 9.60153066802111,-2.564311239386127 C662.9038267144923,344.4761479343565 665.2261481565545,342.15382649229446 666.9356889828118,339.22653055692217 c1.7095408262574177,-2.9311989966194534 2.564311239386127,-6.131709219293157 2.564311239386127,-9.60153066802111 C669.500000222198,326.15517844017313 668.6452298090693,322.9546682174994 666.9356889828118,320.02346922088 zM661.5299491554911,331.21744887774355 c0,0.43323979843509924 -0.15612244988652224,0.8040306169155892 -0.47227041090672967,1.1201785779357967 c-0.31614796102020737,0.31614796102020737 -0.6908418407478605,0.47227041090672967 -1.1201785779357967,0.47227041090672967 H653.5638011500312 V339.18749994445056 c0,0.43323979843509924 -0.16002551113368507,0.8040306169155892 -0.47227041090672967,1.1201785779357967 c-0.31614796102020737,0.31614796102020737 -0.6869387795006976,0.47227041090672967 -1.1201785779357967,0.47227041090672967 h-3.1888010389322154 c-0.43323979843509924,0 -0.8040306169155892,-0.15612244988652224 -1.1201785779357967,-0.47227041090672967 c-0.31614796102020737,-0.31614796102020737 -0.47227041090672967,-0.6908418407478605 -0.47227041090672967,-1.1201785779357967 V332.8138009278332 H640.8125000555494 c-0.43323979843509924,0 -0.8040306169155892,-0.16002551113368507 -1.1201785779357967,-0.47227041090672967 c-0.31614796102020737,-0.31614796102020737 -0.47227041090672967,-0.6869387795006976 -0.47227041090672967,-1.1201785779357967 v-3.1888010389322154 c0,-0.43323979843509924 0.15612244988652224,-0.8040306169155892 0.47227041090672967,-1.1201785779357967 c0.31614796102020737,-0.31614796102020737 0.6869387795006976,-0.47227041090672967 1.1201785779357967,-0.47227041090672967 h6.373699016617268 V320.0624998333516 c0,-0.43323979843509924 0.15612244988652224,-0.8040306169155892 0.47227041090672967,-1.1201785779357967 c0.31614796102020737,-0.31614796102020737 0.6869387795006976,-0.47227041090672967 1.1201785779357967,-0.47227041090672967 h3.1888010389322154 c0.43323979843509924,0 0.8040306169155892,0.15612244988652224 1.1201785779357967,0.47227041090672967 c0.31614796102020737,0.31614796102020737 0.47227041090672967,0.6869387795006976 0.47227041090672967,1.1201785779357967 v6.373699016617268 H659.9375001666484 c0.43323979843509924,0 0.8040306169155892,0.15612244988652224 1.1201785779357967,0.47227041090672967 c0.31614796102020737,0.31614796102020737 0.47227041090672967,0.6869387795006976 0.47227041090672967,1.1201785779357967 V331.21744887774355 z"
                })
        )
    ),
    gojs(go.Panel, "Vertical",
        {
            alignment: go.Spot.Left,
            alignmentFocus: go.Spot.Right
        },
        gojs("Button",
            { // drawLink is defined below, to support interactively drawing new links
                click: addColumn,
                margin: 5
            },
            gojs(go.Shape,
                {   fill: "green",
                    geometryString: "M666.9356889828118,320.02346922088 C665.2261481565545,317.0961732855076 662.9038267144923,314.77385184344564 659.9765307791201,313.0643110171882 C657.0453317825006,311.3547701909308 653.8448215598269,310.49999977780203 650.375000111099,310.49999977780203 c-3.469821448727955,0 -6.6703316714016605,0.8547704131287088 -9.60153066802111,2.564311239386127 C637.8461735077055,314.77385184344564 635.5238520656435,317.0961732855076 633.8143112393861,320.02346922088 C632.1047704131287,322.9546682174994 631.25,326.15517844017313 631.25,329.624999888901 c0,3.469821448727955 0.8547704131287088,6.6703316714016605 2.564311239386127,9.60153066802111 C635.5238520656435,342.15382649229446 637.8461735077055,344.4761479343565 640.7734694430778,346.18568876061386 c2.9311989966194534,1.7095408262574177 6.131709219293157,2.564311239386127 9.60153066802111,2.564311239386127 s6.6703316714016605,-0.8547704131287088 9.60153066802111,-2.564311239386127 C662.9038267144923,344.4761479343565 665.2261481565545,342.15382649229446 666.9356889828118,339.22653055692217 c1.7095408262574177,-2.9311989966194534 2.564311239386127,-6.131709219293157 2.564311239386127,-9.60153066802111 C669.500000222198,326.15517844017313 668.6452298090693,322.9546682174994 666.9356889828118,320.02346922088 zM661.5299491554911,331.21744887774355 c0,0.43323979843509924 -0.15612244988652224,0.8040306169155892 -0.47227041090672967,1.1201785779357967 c-0.31614796102020737,0.31614796102020737 -0.6908418407478605,0.47227041090672967 -1.1201785779357967,0.47227041090672967 H653.5638011500312 V339.18749994445056 c0,0.43323979843509924 -0.16002551113368507,0.8040306169155892 -0.47227041090672967,1.1201785779357967 c-0.31614796102020737,0.31614796102020737 -0.6869387795006976,0.47227041090672967 -1.1201785779357967,0.47227041090672967 h-3.1888010389322154 c-0.43323979843509924,0 -0.8040306169155892,-0.15612244988652224 -1.1201785779357967,-0.47227041090672967 c-0.31614796102020737,-0.31614796102020737 -0.47227041090672967,-0.6908418407478605 -0.47227041090672967,-1.1201785779357967 V332.8138009278332 H640.8125000555494 c-0.43323979843509924,0 -0.8040306169155892,-0.16002551113368507 -1.1201785779357967,-0.47227041090672967 c-0.31614796102020737,-0.31614796102020737 -0.47227041090672967,-0.6869387795006976 -0.47227041090672967,-1.1201785779357967 v-3.1888010389322154 c0,-0.43323979843509924 0.15612244988652224,-0.8040306169155892 0.47227041090672967,-1.1201785779357967 c0.31614796102020737,-0.31614796102020737 0.6869387795006976,-0.47227041090672967 1.1201785779357967,-0.47227041090672967 h6.373699016617268 V320.0624998333516 c0,-0.43323979843509924 0.15612244988652224,-0.8040306169155892 0.47227041090672967,-1.1201785779357967 c0.31614796102020737,-0.31614796102020737 0.6869387795006976,-0.47227041090672967 1.1201785779357967,-0.47227041090672967 h3.1888010389322154 c0.43323979843509924,0 0.8040306169155892,0.15612244988652224 1.1201785779357967,0.47227041090672967 c0.31614796102020737,0.31614796102020737 0.47227041090672967,0.6869387795006976 0.47227041090672967,1.1201785779357967 v6.373699016617268 H659.9375001666484 c0.43323979843509924,0 0.8040306169155892,0.15612244988652224 1.1201785779357967,0.47227041090672967 c0.31614796102020737,0.31614796102020737 0.47227041090672967,0.6869387795006976 0.47227041090672967,1.1201785779357967 V331.21744887774355 z"
                })
        )
    ),
);

// provide a tooltip for the background of the Diagram, when not over any Part
const commonToolTip = gojs(go.Adornment, "Auto",
    gojs(go.Shape, {fill: "#FFFFCC"}),
    gojs(go.TextBlock, {margin: 4},
        new go.Binding("text", "", diagramInfo))
);

const commonPaletteGeneral = {
    scrollsPageOnFocus: false,
    layout: gojs(PoolLayout, {
        sorting: go.GridLayout.Forward
    }),
    hoverDelay: 100,
    scale: 0.75,
    allowHorizontalScroll: false,
    allowVerticalScroll: true,
    "draggingTool.dragsTree": false,
    "draggingTool.isGridSnapEnabled": false,
    nodeSelectionAdornmentTemplate: emptyAdornment,
    groupSelectionAdornmentTemplate: emptyAdornment
};

const commonPaletteBasic = {
    scrollsPageOnFocus: false,
    layout: gojs(PoolLayout, {
        sorting: go.GridLayout.Forward
    }),
    hoverDelay: 100,
    scale: 0.75,
    allowHorizontalScroll: false,
    allowVerticalScroll: false,
    "draggingTool.dragsTree": false,
    "draggingTool.isGridSnapEnabled": true,
    nodeSelectionAdornmentTemplate: emptyAdornment,
    groupSelectionAdornmentTemplate: emptyAdornment
};

const commonContextMenu = gojs(go.Adornment, "Vertical",
    makeButton("Paste",
        function (e, obj) {
            e.diagram.commandHandler.pasteSelection(e.diagram.lastInput.documentPoint);
        },
        function (o) {
            return o.diagram.commandHandler.canPasteSelection();
        }),
    makeButton("Undo",
        function (e, obj) {
            e.diagram.commandHandler.undo();
        },
        function (o) {
            return o.diagram.commandHandler.canUndo();
        }),
    makeButton("Redo",
        function (e, obj) {
            e.diagram.commandHandler.redo();
        },
        function (o) {
            return o.diagram.commandHandler.canRedo();
        })
);

const commonGroupResizeAdornmentTemplate = gojs(go.Adornment, "Spot",
    gojs(go.Placeholder),
    gojs(go.Shape,  // for changing the length of a lane
        {
            alignment: go.Spot.Right,
            desiredSize: new go.Size(7, 50),
            fill: "lightblue", stroke: "dodgerblue",
            cursor: "col-resize"
        },
        new go.Binding("visible", "", function (ad) {
            if (ad.adornedPart === null) return false;
            return ad.adornedPart.isSubGraphExpanded;
        }).ofObject()),
    gojs(go.Shape,  // for changing the breadth of a lane
        {
            alignment: go.Spot.Bottom,
            desiredSize: new go.Size(50, 7),
            fill: "lightblue", stroke: "dodgerblue",
            cursor: "row-resize"
        },
        new go.Binding("visible", "", function (ad) {
            if (ad.adornedPart === null) return false;
            return ad.adornedPart.isSubGraphExpanded;
        }).ofObject())
);
