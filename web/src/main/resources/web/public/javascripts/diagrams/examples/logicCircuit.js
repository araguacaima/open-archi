red = "orangered";  // 0 or false
green = "forestgreen";  // 1 or true
var logicCircuitDiagram = {};

function initLogicCircuit(diagramID) {
    const incomingModel = {
        "class": "go.GraphLinksModel",
        linkFromPortIdProperty: "fromPort",
        linkToPortIdProperty: "toPort",
        nodeDataArray: [
            {"category": "input", "key": "input1", "loc": "-150 -80"},
            {"category": "or", "key": "or1", "loc": "-70 0"},
            {"category": "not", "key": "not1", "loc": "10 0"},
            {"category": "xor", "key": "xor1", "loc": "100 0"},
            {"category": "or", "key": "or2", "loc": "200 0"},
            {"category": "output", "key": "output1", "loc": "200 -100"}
        ],
        linkDataArray: [
            {"from": "input1", "fromPort": "out", "to": "or1", "toPort": "in1"},
            {"from": "or1", "fromPort": "out", "to": "not1", "toPort": "in"},
            {"from": "not1", "fromPort": "out", "to": "or1", "toPort": "in2"},
            {"from": "not1", "fromPort": "out", "to": "xor1", "toPort": "in1"},
            {"from": "xor1", "fromPort": "out", "to": "or2", "toPort": "in1"},
            {"from": "or2", "fromPort": "out", "to": "xor1", "toPort": "in2"},
            {"from": "xor1", "fromPort": "out", "to": "output1", "toPort": ""}
        ]
    };
    logicCircuitDiagram =
        gojs(go.Diagram, "diagramDiv-" + diagramID,  // create a new Diagram in the HTML DIV element diagramDiv
            {
                initialContentAlignment: go.Spot.Center,
                allowDrop: true,  // Nodes from the Palette can be dropped into the Diagram
                "draggingTool.isGridSnapEnabled": true,  // dragged nodes will snap to a grid of 10x10 cells
                "undoManager.isEnabled": true
            });

    // when the document is modified, add a "*" to the title and enable the "Save" button
    logicCircuitDiagram.addDiagramListener("Modified", function (e) {
        const button = document.getElementById("saveModel");
        if (button) button.disabled = !logicCircuitDiagram.isModified;
        const idx = document.title.indexOf("*");
        if (logicCircuitDiagram.isModified) {
            if (idx < 0) document.title += "*";
        } else {
            if (idx >= 0) document.title = document.title.substr(0, idx);
        }
    });

    // initialize the Palette that is on the left side of the page
    // noinspection JSUndeclaredVariable
    let myPaletteBasic =
        gojs(go.Palette, "paletteDiv-" + diagramID);

    // creates relinkable Links that will avoid crossing Nodes when possible and will jump over other Links in their paths
    logicCircuitDiagram.linkTemplate =
        gojs(go.Link,
            {
                routing: go.Link.AvoidsNodes,
                curve: go.Link.JumpOver,
                corner: 3,
                relinkableFrom: true, relinkableTo: true,
                selectionAdorned: false, // Links are not adorned when selected so that their color remains visible.
                shadowOffset: new go.Point(0, 0), shadowBlur: 5, shadowColor: "blue",
            },
            new go.Binding("isShadowed", "isSelected").ofObject(),
            gojs(go.Shape,
                {name: "SHAPE", strokeWidth: 2, stroke: red}));

    // node template helpers
    const sharedToolTip =
        gojs(go.Adornment, "Auto",
            gojs(go.Shape, "RoundedRectangle", {fill: "lightyellow"}),
            gojs(go.TextBlock, {margin: 2},
                new go.Binding("text", "", function (d) {
                    return d.category;
                })));

    // define some common property settings
    function nodeStyle() {
        return [new go.Binding("location", "loc", go.Point.parse).makeTwoWay(go.Point.stringify),
            new go.Binding("isShadowed", "isSelected").ofObject(),
            {
                selectionAdorned: false,
                shadowOffset: new go.Point(0, 0),
                shadowBlur: 15,
                shadowColor: "blue",
                toolTip: sharedToolTip
            }];
    }

    function shapeStyle() {
        return {
            name: "NODESHAPE",
            fill: "lightgray",
            stroke: "darkslategray",
            desiredSize: new go.Size(40, 40),
            strokeWidth: 2
        };
    }

    function portStyle(input) {
        return {
            desiredSize: new go.Size(6, 6),
            fill: "black",
            fromSpot: go.Spot.Right,
            fromLinkable: !input,
            toSpot: go.Spot.Left,
            toLinkable: input,
            toMaxLinks: 1,
            cursor: "pointer"
        };
    }

    // define templates for each type of node
    const inputTemplate =
        gojs(go.Node, "Spot", nodeStyle(),
            gojs(go.Shape, "Circle", shapeStyle(),
                {fill: red}),  // override the default fill (from shapeStyle()) to be red
            gojs(go.Shape, "Rectangle", portStyle(false),  // the only port
                {portId: "", alignment: new go.Spot(1, 0.5)}),
            { // if double-clicked, an input node will change its value, represented by the color.
                doubleClick: function (e, obj) {
                    e.diagram.startTransaction("Toggle Input");
                    const shp = obj.findObject("NODESHAPE");
                    shp.fill = (shp.fill === green) ? red : green;
                    updateStates();
                    e.diagram.commitTransaction("Toggle Input");
                }
            }
        );

    const outputTemplate =
        gojs(go.Node, "Spot", nodeStyle(),
            gojs(go.Shape, "Rectangle", shapeStyle(),
                {fill: green}),  // override the default fill (from shapeStyle()) to be green
            gojs(go.Shape, "Rectangle", portStyle(true),  // the only port
                {portId: "", alignment: new go.Spot(0, 0.5)})
        );

    const andTemplate =
        gojs(go.Node, "Spot", nodeStyle(),
            gojs(go.Shape, "AndGate", shapeStyle()),
            gojs(go.Shape, "Rectangle", portStyle(true),
                {portId: "in1", alignment: new go.Spot(0, 0.3)}),
            gojs(go.Shape, "Rectangle", portStyle(true),
                {portId: "in2", alignment: new go.Spot(0, 0.7)}),
            gojs(go.Shape, "Rectangle", portStyle(false),
                {portId: "out", alignment: new go.Spot(1, 0.5)})
        );

    const orTemplate =
        gojs(go.Node, "Spot", nodeStyle(),
            gojs(go.Shape, "OrGate", shapeStyle()),
            gojs(go.Shape, "Rectangle", portStyle(true),
                {portId: "in1", alignment: new go.Spot(0.16, 0.3)}),
            gojs(go.Shape, "Rectangle", portStyle(true),
                {portId: "in2", alignment: new go.Spot(0.16, 0.7)}),
            gojs(go.Shape, "Rectangle", portStyle(false),
                {portId: "out", alignment: new go.Spot(1, 0.5)})
        );

    const xorTemplate =
        gojs(go.Node, "Spot", nodeStyle(),
            gojs(go.Shape, "XorGate", shapeStyle()),
            gojs(go.Shape, "Rectangle", portStyle(true),
                {portId: "in1", alignment: new go.Spot(0.26, 0.3)}),
            gojs(go.Shape, "Rectangle", portStyle(true),
                {portId: "in2", alignment: new go.Spot(0.26, 0.7)}),
            gojs(go.Shape, "Rectangle", portStyle(false),
                {portId: "out", alignment: new go.Spot(1, 0.5)})
        );

    const norTemplate =
        gojs(go.Node, "Spot", nodeStyle(),
            gojs(go.Shape, "NorGate", shapeStyle()),
            gojs(go.Shape, "Rectangle", portStyle(true),
                {portId: "in1", alignment: new go.Spot(0.16, 0.3)}),
            gojs(go.Shape, "Rectangle", portStyle(true),
                {portId: "in2", alignment: new go.Spot(0.16, 0.7)}),
            gojs(go.Shape, "Rectangle", portStyle(false),
                {portId: "out", alignment: new go.Spot(1, 0.5)})
        );

    const xnorTemplate =
        gojs(go.Node, "Spot", nodeStyle(),
            gojs(go.Shape, "XnorGate", shapeStyle()),
            gojs(go.Shape, "Rectangle", portStyle(true),
                {portId: "in1", alignment: new go.Spot(0.26, 0.3)}),
            gojs(go.Shape, "Rectangle", portStyle(true),
                {portId: "in2", alignment: new go.Spot(0.26, 0.7)}),
            gojs(go.Shape, "Rectangle", portStyle(false),
                {portId: "out", alignment: new go.Spot(1, 0.5)})
        );

    const nandTemplate =
        gojs(go.Node, "Spot", nodeStyle(),
            gojs(go.Shape, "NandGate", shapeStyle()),
            gojs(go.Shape, "Rectangle", portStyle(true),
                {portId: "in1", alignment: new go.Spot(0, 0.3)}),
            gojs(go.Shape, "Rectangle", portStyle(true),
                {portId: "in2", alignment: new go.Spot(0, 0.7)}),
            gojs(go.Shape, "Rectangle", portStyle(false),
                {portId: "out", alignment: new go.Spot(1, 0.5)})
        );

    const notTemplate =
        gojs(go.Node, "Spot", nodeStyle(),
            gojs(go.Shape, "Inverter", shapeStyle()),
            gojs(go.Shape, "Rectangle", portStyle(true),
                {portId: "in", alignment: new go.Spot(0, 0.5)}),
            gojs(go.Shape, "Rectangle", portStyle(false),
                {portId: "out", alignment: new go.Spot(1, 0.5)})
        );

    // add the templates created above to logicCircuitDiagram and palette
    logicCircuitDiagram.nodeTemplateMap.add("input", inputTemplate);
    logicCircuitDiagram.nodeTemplateMap.add("output", outputTemplate);
    logicCircuitDiagram.nodeTemplateMap.add("and", andTemplate);
    logicCircuitDiagram.nodeTemplateMap.add("or", orTemplate);
    logicCircuitDiagram.nodeTemplateMap.add("xor", xorTemplate);
    logicCircuitDiagram.nodeTemplateMap.add("not", notTemplate);
    logicCircuitDiagram.nodeTemplateMap.add("nand", nandTemplate);
    logicCircuitDiagram.nodeTemplateMap.add("nor", norTemplate);
    logicCircuitDiagram.nodeTemplateMap.add("xnor", xnorTemplate);

    // share the template map with the Palette
    myPaletteBasic.nodeTemplateMap = logicCircuitDiagram.nodeTemplateMap;

    myPaletteBasic.model.nodeDataArray = [
        {category: "input"},
        {category: "output"},
        {category: "and"},
        {category: "or"},
        {category: "xor"},
        {category: "not"},
        {category: "nand"},
        {category: "nor"},
        {category: "xnor"}
    ];

    logicCircuitDiagram.model = go.Model.fromJson(incomingModel);
    const desktopDiagram = getDiagramById(diagramID);
    desktopDiagram.diagram = logicCircuitDiagram;


    // load the initial diagram

    // continually update the diagram
    loop();
}

// update the diagram every 250 milliseconds
function loop() {
    setTimeout(function () {
        updateStates();
        loop();
    }, 250);
}

// update the value and appearance of each node according to its type and input values
function updateStates() {
    const oldskip = logicCircuitDiagram.skipsUndoManager;
    logicCircuitDiagram.skipsUndoManager = true;
    // do all "input" nodes first
    logicCircuitDiagram.nodes.each(function (node) {
        if (node.category === "input") {
            doInput(node);
        }
    });
    // now we can do all other kinds of nodes
    logicCircuitDiagram.nodes.each(function (node) {
        switch (node.category) {
            case "and":
                doAnd(node);
                break;
            case "or":
                doOr(node);
                break;
            case "xor":
                doXor(node);
                break;
            case "not":
                doNot(node);
                break;
            case "nand":
                doNand(node);
                break;
            case "nor":
                doNor(node);
                break;
            case "xnor":
                doXnor(node);
                break;
            case "output":
                doOutput(node);
                break;
            case "input":
                break;  // doInput already called, above
        }
    });
    logicCircuitDiagram.skipsUndoManager = oldskip;
}

// helper predicate
function linkIsTrue(link) {  // assume the given Link has a Shape named "SHAPE"
    return link.findObject("SHAPE").stroke === green;
}

// helper function for propagating results
function setOutputLinks(node, color) {
    node.findLinksOutOf().each(function (link) {
        link.findObject("SHAPE").stroke = color;
    });
}

// update nodes by the specific function for its type
// determine the color of links coming out of this node based on those coming in and node type

function doInput(node) {
    // the output is just the node's Shape.fill
    setOutputLinks(node, node.findObject("NODESHAPE").fill);
}

function doAnd(node) {
    const color = node.findLinksInto().all(linkIsTrue) ? green : red;
    setOutputLinks(node, color);
}

function doNand(node) {
    const color = !node.findLinksInto().all(linkIsTrue) ? green : red;
    setOutputLinks(node, color);
}

function doNot(node) {
    const color = !node.findLinksInto().all(linkIsTrue) ? green : red;
    setOutputLinks(node, color);
}

function doOr(node) {
    const color = node.findLinksInto().any(linkIsTrue) ? green : red;
    setOutputLinks(node, color);
}

function doNor(node) {
    const color = !node.findLinksInto().any(linkIsTrue) ? green : red;
    setOutputLinks(node, color);
}

function doXor(node) {
    let truecount = 0;
    node.findLinksInto().each(function (link) {
        if (linkIsTrue(link)) truecount++;
    });
    const color = truecount % 2 === 0 ? green : red;
    setOutputLinks(node, color);
}

function doXnor(node) {
    let truecount = 0;
    node.findLinksInto().each(function (link) {
        if (linkIsTrue(link)) truecount++;
    });
    const color = truecount % 2 !== 0 ? green : red;
    setOutputLinks(node, color);
}

function doOutput(node) {
    // assume there is just one input link
    // we just need to update the node's Shape.fill
    node.linksConnected.each(function (link) {
        node.findObject("NODESHAPE").fill = link.findObject("SHAPE").stroke;
    });
}

// save a model to and load a model from JSON text, displayed below the Diagram
function save() {
    document.getElementById("modelToSaveOrLoad").value = JSON.stringify(logicCircuitDiagram.model, null, 2);
    let textContent = logicCircuitDiagram.model.toJson();
    logicCircuitDiagram.isModified = false;
}
