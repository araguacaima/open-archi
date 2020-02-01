function initContextMenu(diagramID) {

    let myDiagram =
        gojs(go.Diagram, "diagramDiv-" + diagramID,  // create a Diagram for the DIV HTML element
            {initialContentAlignment: go.Spot.Center, "undoManager.isEnabled": true});

    // This is the actual HTML context menu:
    const cxElement = document.getElementById("contextMenu");

    // Since we have only one main element, we don't have to declare a hide method,
    // we can set mainElement and GoJS will hide it automatically
    const myContextMenu = gojs(go.HTMLInfo, {
        show: showContextMenu,
        mainElement: cxElement
    });

    // define a simple Node template (but use the default Link template)
    myDiagram.nodeTemplate =
        gojs(go.Node, "Auto",
            {contextMenu: myContextMenu},
            gojs(go.Shape, "RoundedRectangle",
                // Shape.fill is bound to Node.data.color
                new go.Binding("fill", "color")),
            gojs(go.TextBlock,
                {margin: 3},  // some room around the text
                // TextBlock.text is bound to Node.data.key
                new go.Binding("text", "key"))
        );

    // create the model data that will be represented by Nodes and Links
    myDiagram.model = new go.GraphLinksModel(
        [
            {key: "Alpha", color: "crimson"},
            {key: "Beta", color: "chartreuse"},
            {key: "Gamma", color: "aquamarine"},
            {key: "Delta", color: "gold"}
        ],
        [
            {from: "Alpha", to: "Beta"},
            {from: "Alpha", to: "Gamma"},
            {from: "Beta", to: "Beta"},
            {from: "Gamma", to: "Delta"},
            {from: "Delta", to: "Alpha"}
        ]);

    myDiagram.contextMenu = myContextMenu;

    // We don't want the div acting as a context menu to have a (browser) context menu!
    cxElement.addEventListener("contextmenu", function (e) {
        e.preventDefault();
        return false;
    }, false);

    function showContextMenu(obj, diagram, tool) {
        // Show only the relevant buttons given the current state.
        const cmd = diagram.commandHandler;
        document.getElementById("cut").style.display = cmd.canCutSelection() ? "block" : "none";
        document.getElementById("copy").style.display = cmd.canCopySelection() ? "block" : "none";
        document.getElementById("paste").style.display = cmd.canPasteSelection() ? "block" : "none";
        document.getElementById("delete").style.display = cmd.canDeleteSelection() ? "block" : "none";
        document.getElementById("color").style.display = (obj !== null ? "block" : "none");

        // Now show the whole context menu element
        cxElement.style.display = "block";
        // we don't bother overriding positionContextMenu, we just do it here:
        const mousePt = diagram.lastInput.viewPoint;
        cxElement.style.left = mousePt.x + "px";
        cxElement.style.top = mousePt.y + "px";
    }
    const desktopDiagram = getDiagramById(diagramID);
    desktopDiagram.diagram = myDiagram;
}

// This is the general menu command handler, parameterized by the name of the command.
function cxcommand(event, val) {
    if (val === undefined) val = event.currentTarget.id;
    const diagram = myDiagram;
    switch (val) {
        case "cut":
            diagram.commandHandler.cutSelection();
            break;
        case "copy":
            diagram.commandHandler.copySelection();
            break;
        case "paste":
            diagram.commandHandler.pasteSelection(diagram.lastInput.documentPoint);
            break;
        case "delete":
            diagram.commandHandler.deleteSelection();
            break;
        case "color": {
            const color = window.getComputedStyle(document.elementFromPoint(event.clientX, event.clientY).parentElement)['background-color'];
            changeColor(diagram, color);
            break;
        }
    }
    diagram.currentTool.stopTool();
}

// A custom command, for changing the color of the selected node(s).
function changeColor(diagram, color) {
    // Always make changes in a transaction, except when initializing the diagram.
    diagram.startTransaction("change color");
    diagram.selection.each(function (node) {
        if (node instanceof go.Node) {  // ignore any selected Links and simple Parts
            // Examine and modify the data, not the Node directly.
            const data = node.data;
            // Call setDataProperty to support undo/redo as well as
            // automatically evaluating any relevant bindings.
            diagram.model.setDataProperty(data, "color", color);
        }
    });
    diagram.commitTransaction("change color");
}