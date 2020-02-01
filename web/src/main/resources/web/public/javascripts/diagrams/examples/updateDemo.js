function initUpdateDemo(diagramID, diagramID2) {

    blueDiagram =
        gojs(go.Diagram, "blueDiagram-" + diagramID,
            {
                // start everything in the middle of the viewport
                initialContentAlignment: go.Spot.Center,
                // double-click in background creates a new node there
                "clickCreatingTool.archetypeNodeData": {text: "node"}
            });

    blueDiagram.nodeTemplate =
        gojs(go.Node, "Auto",
            new go.Binding("location", "loc").makeTwoWay(),
            gojs(go.Shape, "RoundedRectangle",
                {
                    fill: gojs(go.Brush, "Linear", {0: "#00ACED", 0.5: "#00ACED", 1: "#0079A6"}),
                    stroke: "#0079A6",
                    portId: "", cursor: "pointer",  // the node's only port is the Shape
                    fromLinkable: true, fromLinkableDuplicates: true, fromLinkableSelfNode: true,
                    toLinkable: true, toLinkableDuplicates: true, toLinkableSelfNode: true
                }),
            gojs(go.TextBlock,
                {margin: 3, font: "bold 10pt Arial, sans-serif", stroke: "whitesmoke", editable: true},
                new go.Binding("text").makeTwoWay())
        );

    blueDiagram.linkTemplate =
        gojs(go.Link,
            {
                curve: go.Link.Bezier, adjusting: go.Link.Stretch,
                relinkableFrom: true, relinkableTo: true, reshapable: true
            },
            gojs(go.Shape,  // the link shape
                {strokeWidth: 2, stroke: "blue"}),
            gojs(go.Shape,  // the arrowhead
                {
                    toArrow: "standard",
                    fill: "blue", stroke: null
                })
        );
    const desktopDiagram = getDiagramById(diagramID);
    desktopDiagram.diagram = blueDiagram;


    const greenDiagram =
        gojs(go.Diagram, "greenDiagram" + "-" + diagramID2,
            {
                // start everything in the middle of the viewport
                initialContentAlignment: go.Spot.Center,
                // double-click in background creates a new node there
                "clickCreatingTool.archetypeNodeData": {key: "node"}
            });

    greenDiagram.nodeTemplate =
        gojs(go.Node, "Vertical",
            new go.Binding("location", "loc").makeTwoWay(),
            gojs(go.Shape, "Ellipse",
                {fill: "lightgreen", width: 20, height: 20, portId: ""}),
            gojs(go.TextBlock,
                {margin: 3, font: "bold 12px Georgia, sans-serif"},
                new go.Binding("text"))
        );

    greenDiagram.linkTemplate =
        gojs(go.Link,
            gojs(go.Shape,  // the link shape
                {strokeWidth: 2, stroke: "#76C176"}),
            gojs(go.Shape,  // the arrowhead
                {
                    toArrow: "standard",
                    fill: "#76C176", stroke: null
                })
        );
    const desktopDiagram2 = getDiagramById(diagramID2);
    desktopDiagram2.diagram = greenDiagram;

    // create the model data that will be represented in both diagrams simultaneously
    const model = new go.GraphLinksModel(
        [
            {key: 1, text: "Alpha", loc: new go.Point(20, 20)},
            {key: 2, text: "Beta", loc: new go.Point(160, 120)}
        ],
        [
            {from: 1, to: 2}
        ]);

    // the two Diagrams share the same model
    blueDiagram.model = model;
    greenDiagram.model = model;

    // now turn on undo/redo
    model.undoManager.isEnabled = true;


    // **********************************************************
    // A third diagram is on this page to display the undo state.
    // It functions as a tree view, showing the Transactions
    // in the UndoManager history.
    // **********************************************************

    const undoDisplay =
        gojs(go.Diagram, "undoDisplay-"+diagramID,
            {
                allowMove: false,
                maxSelectionCount: 1,
                layout:
                    gojs(go.TreeLayout,
                        {
                            alignment: go.TreeLayout.AlignmentStart,
                            angle: 0,
                            compaction: go.TreeLayout.CompactionNone,
                            layerSpacing: 16,
                            layerSpacingParentOverlap: 1,
                            nodeIndent: 2,
                            nodeIndentPastParent: 0.88,
                            nodeSpacing: 0,
                            setsPortSpot: false,
                            setsChildPortSpot: false,
                            arrangementSpacing: new go.Size(2, 2)
                        }),
                "animationManager.isEnabled": false
            });

    undoDisplay.nodeTemplate =
        gojs(go.Node,
            gojs("TreeExpanderButton",
                {width: 14, "ButtonBorder.fill": "whitesmoke"}),
            gojs(go.Panel, "Horizontal",
                {position: new go.Point(16, 0)},
                new go.Binding("background", "color"),
                gojs(go.TextBlock, {margin: 2},
                    new go.Binding("text", "text"))
            )
        );

    undoDisplay.linkTemplate = gojs(go.Link);  // not really used

    const undoModel =
        gojs(go.TreeModel,  // initially empty
            {isReadOnly: true});
    undoDisplay.model = undoModel;

    // ******************************************************
    // Add an undo listener to the main model
    // ******************************************************

    const changedLog = document.getElementById("modelChangedLog-" + diagramID);
    let editToRedo = null; // a node in the undoDisplay
    let editList = [];

    model.addChangedListener(function (e) {
        // do not display some uninteresting kinds of transaction notifications
        if (e.change === go.ChangedEvent.Transaction) {
            if (e.propertyName === "CommittingTransaction" || e.modelChange === "SourceChanged") return;
            // do not display any layout transactions
            if (e.oldValue === "Layout") return;
        }  // You will probably want to use e.isTransactionFinished instead

        // Add entries into the log
        let changes = e.toString();
        if (changes[0] !== "*") changes = "&nbsp;&nbsp;" + changes;
        changedLog.innerHTML += changes + "<br/>";
        changedLog.scrollTop = changedLog.scrollHeight;

        // Modify the undoDisplay Diagram, the tree view
        if (e.propertyName === "CommittedTransaction") {
            if (editToRedo != null) {
                // remove from the undo display diagram all nodes after editToRedo
                for (let i = editToRedo.data.index + 1; i < editList.length; i++) {
                    undoDisplay.remove(editList[i]);
                }
                editList = editList.slice(0, editToRedo.data.index);
                editToRedo = null;
            }

            const tx = e.object;
            const txname = (tx !== null ? e.object.name : "");
            const parentData = {text: txname, tag: e.object, index: editList.length - 1};
            undoModel.addNodeData(parentData);
            const parentKey = undoModel.getKeyForNodeData(parentData);
            const parentNode = undoDisplay.findNodeForKey(parentKey);
            editList.push(parentNode);
            if (tx !== null) {
                const allChanges = tx.changes;
                let odd = true;
                allChanges.each(function (change) {
                    const childData = {
                        color: (odd ? "white" : "#E0FFED"),
                        text: change.toString(),
                        parent: parentKey
                    };
                    undoModel.addNodeData(childData);
                    odd = !odd;
                });
                undoDisplay.commandHandler.collapseTree(parentNode);
            }
        } else if (e.propertyName === "FinishedUndo" || e.propertyName === "FinishedRedo") {
            const undoManager = model.undoManager;
            if (editToRedo !== null) {
                editToRedo.isSelected = false;
                editToRedo = null;
            }
            // Find the node that represents the undo or redo state and select it
            const nextEdit = undoManager.transactionToRedo;
            if (nextEdit !== null) {
                const itr = undoDisplay.nodes;
                while (itr.next()) {
                    const node = itr.value;
                    if (node.data.tag === nextEdit) {
                        node.isSelected = true;
                        editToRedo = node;
                        break;
                    }
                }
            }
        }
    }); // end model changed listener

    model.addChangedListener(function (e) {
        if (e.isTransactionFinished) {
            const tx = e.object;
            if (tx instanceof go.Transaction && window.console) {
                window.console.log(tx.toString());
                tx.changes.each(function (c) {
                    if (c.model) window.console.log("  " + c.toString());
                });
            }
        }
    });
} // end init

function clearLog(diagramID) {
    const div = document.getElementById("modelChangedLog-"+diagramID).innerHTML = "";
}

function undoDiagram(diagramID) {
    let myDiagram = getDiagramById(diagramID).diagram;
    myDiagram.commandHandler.undo();
}

function redoDiagram(diagramID) {
    let myDiagram = getDiagramById(diagramID).diagram;
    myDiagram.commandHandler.redo();
}