function initMindMap(diagramID, nodeDataArray, linkDataArray) {

    let myDiagram = gojs(go.Diagram, "diagramDiv-" + diagramID,  // create a Diagram for the DIV HTML element
        {
            // when the user drags a node, also move/copy/delete the whole subtree starting with that node
            "commandHandler.copiesTree": true,
            "commandHandler.deletesTree": true,
            "draggingTool.dragsTree": true,
            initialContentAlignment: go.Spot.Center,  // center the whole graph
            "undoManager.isEnabled": true
        });

    // a node consists of some text with a line shape underneath
    myDiagram.nodeTemplate =
        gojs(go.Node, "Vertical",
            {selectionObjectName: "TEXT"},
            gojs(go.TextBlock,
                {
                    name: "TEXT",
                    minSize: new go.Size(30, 15),
                    editable: true
                },
                // remember not only the text string but the scale and the font in the node data
                new go.Binding("text", "text").makeTwoWay(),
                new go.Binding("scale", "scale").makeTwoWay(),
                new go.Binding("font", "font").makeTwoWay()),
            gojs(go.Shape, "LineH",
                {
                    stretch: go.GraphObject.Horizontal,
                    strokeWidth: 3, height: 3,
                    // this line shape is the port -- what links connect with
                    portId: "", fromSpot: go.Spot.LeftRightSides, toSpot: go.Spot.LeftRightSides
                },
                new go.Binding("stroke", "brush"),
                // make sure links come in from the proper direction and go out appropriately
                new go.Binding("fromSpot", "dir", function (d) {
                    return spotConverter(d, true);
                }),
                new go.Binding("toSpot", "dir", function (d) {
                    return spotConverter(d, false);
                })),
            // remember the locations of each node in the node data
            new go.Binding("location", "loc", go.Point.parse).makeTwoWay(go.Point.stringify),
            // make sure text "grows" in the desired direction
            new go.Binding("locationSpot", "dir", function (d) {
                return spotConverter(d, false);
            })
        );

    // selected nodes show a button for adding children
    myDiagram.nodeTemplate.selectionAdornmentTemplate =
        gojs(go.Adornment, "Spot",
            gojs(go.Panel, "Auto",
                // this Adornment has a rectangular blue Shape around the selected node
                gojs(go.Shape, {fill: null, stroke: "dodgerblue", strokeWidth: 3}),
                gojs(go.Placeholder, {margin: new go.Margin(4, 4, 0, 4)})
            ),
            // and this Adornment has a Button to the right of the selected node
            gojs("Button",
                {
                    alignment: go.Spot.Right,
                    alignmentFocus: go.Spot.Left,
                    click: addNodeAndLink  // define click behavior for this Button in the Adornment
                },
                gojs(go.TextBlock, "+",  // the Button content
                    {font: "bold 8pt sans-serif"})
            )
        );

    // the context menu allows users to change the font size and weight,
    // and to perform a limited tree layout starting at that node
    myDiagram.nodeTemplate.contextMenu =
        gojs(go.Adornment, "Vertical",
            gojs("ContextMenuButton",
                gojs(go.TextBlock, "Bigger"),
                {
                    click: function (e, obj) {
                        changeTextSize(obj, 1.1);
                    }
                }),
            gojs("ContextMenuButton",
                gojs(go.TextBlock, "Smaller"),
                {
                    click: function (e, obj) {
                        changeTextSize(obj, 1 / 1.1);
                    }
                }),
            gojs("ContextMenuButton",
                gojs(go.TextBlock, "Bold/Normal"),
                {
                    click: function (e, obj) {
                        toggleTextWeight(obj);
                    }
                }),
            gojs("ContextMenuButton",
                gojs(go.TextBlock, "Layout"),
                {
                    click: function (e, obj) {
                        const adorn = obj.part;
                        adorn.diagram.startTransaction("Subtree Layout");
                        layoutTree(adorn.adornedPart);
                        adorn.diagram.commitTransaction("Subtree Layout");
                    }
                }
            )
        );

    // a link is just a Bezier-curved line of the same color as the node to which it is connected
    myDiagram.linkTemplate =
        gojs(go.Link,
            {
                curve: go.Link.Bezier,
                fromShortLength: -2,
                toShortLength: -2,
                selectable: false
            },
            gojs(go.Shape,
                {strokeWidth: 3},
                new go.Binding("stroke", "toNode", function (n) {
                    if (n.data.brush) return n.data.brush;
                    return "black";
                }).ofObject())
        );

    // the Diagram's context menu just displays commands for general functionality
    myDiagram.contextMenu =
        gojs(go.Adornment, "Vertical",
            gojs("ContextMenuButton",
                gojs(go.TextBlock, "Undo"),
                {
                    click: function (e, obj) {
                        e.diagram.commandHandler.undo();
                    }
                },
                new go.Binding("visible", "", function (o) {
                    return o.diagram && o.diagram.commandHandler.canUndo();
                }).ofObject()),
            gojs("ContextMenuButton",
                gojs(go.TextBlock, "Redo"),
                {
                    click: function (e, obj) {
                        e.diagram.commandHandler.redo();
                    }
                },
                new go.Binding("visible", "", function (o) {
                    return o.diagram && o.diagram.commandHandler.canRedo();
                }).ofObject()),
            gojs("ContextMenuButton",
                gojs(go.TextBlock, "Save"),
                {
                    click: function (e, obj) {
                        save();
                    }
                }),
            gojs("ContextMenuButton",
                gojs(go.TextBlock, "Load"),
                {
                    click: function (e, obj) {
                        load();
                    }
                })
        );

    myDiagram.addDiagramListener("SelectionMoved", function (e) {
        const rootX = myDiagram.findNodeForKey(0).location.x;
        myDiagram.selection.each(function (node) {
            if (node.data.parent !== 0) return; // Only consider nodes connected to the root
            const nodeX = node.location.x;
            if (rootX < nodeX && node.data.dir !== "right") {
                updateNodeDirection(node, "right");
            } else if (rootX > nodeX && node.data.dir !== "left") {
                updateNodeDirection(node, "left");
            }
            layoutTree(node);
        });
    });

    // read in the predefined graph using the JSON format data held in the "modelToSaveOrLoad" textarea
    myDiagram.model = new go.TreeModel(nodeDataArray);
    const desktopDiagram = getDiagramById(diagramID);
    desktopDiagram.diagram = myDiagram;
}

function spotConverter(dir, from) {
    if (dir === "left") {
        return (from ? go.Spot.Left : go.Spot.Right);
    } else {
        return (from ? go.Spot.Right : go.Spot.Left);
    }
}

function changeTextSize(obj, factor) {
    const adorn = obj.part;
    adorn.diagram.startTransaction("Change Text Size");
    const node = adorn.adornedPart;
    const tb = node.findObject("TEXT");
    tb.scale *= factor;
    adorn.diagram.commitTransaction("Change Text Size");
}

function toggleTextWeight(obj) {
    const adorn = obj.part;
    adorn.diagram.startTransaction("Change Text Weight");
    const node = adorn.adornedPart;
    const tb = node.findObject("TEXT");
    // assume "bold" is at the start of the font specifier
    const idx = tb.font.indexOf("bold");
    if (idx < 0) {
        tb.font = "bold " + tb.font;
    } else {
        tb.font = tb.font.substr(idx + 5);
    }
    adorn.diagram.commitTransaction("Change Text Weight");
}

function updateNodeDirection(node, dir) {
    myDiagram.model.setDataProperty(node.data, "dir", dir);
    // recursively update the direction of the child nodes
    const chl = node.findTreeChildrenNodes(); // gives us an iterator of the child nodes related to this particular node
    while (chl.next()) {
        updateNodeDirection(chl.value, dir);
    }
}

function addNodeAndLink(e, obj) {
    const adorn = obj.part;
    const diagram = adorn.diagram;
    diagram.startTransaction("Add Node");
    const oldnode = adorn.adornedPart;
    const olddata = oldnode.data;
    // copy the brush and direction to the new node data
    const newdata = {text: "idea", brush: olddata.brush, dir: olddata.dir, parent: olddata.key};
    diagram.model.addNodeData(newdata);
    layoutTree(oldnode);
    diagram.commitTransaction("Add Node");
}

function layoutTree(node) {
    if (node.data.key === 0) {  // adding to the root?
        layoutAll();  // lay out everything
    } else {  // otherwise lay out only the subtree starting at this parent node
        const parts = node.findTreeParts();
        layoutAngle(parts, node.data.dir === "left" ? 180 : 0);
    }
}

function layoutAngle(parts, angle) {
    const layout = go.GraphObject.make(go.TreeLayout,
        {
            angle: angle,
            arrangement: go.TreeLayout.ArrangementFixedRoots,
            nodeSpacing: 5,
            layerSpacing: 20,
            setsPortSpot: false, // don't set port spots since we're managing them with our spotConverter function
            setsChildPortSpot: false
        });
    layout.doLayout(parts);
}

function layoutAll() {
    const root = myDiagram.findNodeForKey(0);
    if (root === null) return;
    myDiagram.startTransaction("Layout");
    // split the nodes and links into two collections
    const rightward = new go.Set(go.Part);
    const leftward = new go.Set(go.Part);
    root.findLinksConnected().each(function (link) {
        const child = link.toNode;
        if (child.data.dir === "left") {
            leftward.add(root);  // the root node is in both collections
            leftward.add(link);
            leftward.addAll(child.findTreeParts());
        } else {
            rightward.add(root);  // the root node is in both collections
            rightward.add(link);
            rightward.addAll(child.findTreeParts());
        }
    });
    // do one layout and then the other without moving the shared root node
    layoutAngle(rightward, 0);
    layoutAngle(leftward, 180);
    myDiagram.commitTransaction("Layout");
}

