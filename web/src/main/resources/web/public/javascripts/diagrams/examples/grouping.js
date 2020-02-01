function initGrouping(diagramID) {

    let myDiagram =
        gojs(go.Diagram, "diagramDiv-" + diagramID,  // Diagram refers to its DIV HTML element by id
            {
                // start everything in the middle of the viewport
                initialContentAlignment: go.Spot.Center,
                layout: gojs(go.TreeLayout,  // the layout for the entire diagram
                    {
                        angle: 90,
                        arrangement: go.TreeLayout.ArrangementHorizontal,
                        isRealtime: false
                    })
            });

    // define the node template for non-groups
    myDiagram.nodeTemplate =
        gojs(go.Node, "Auto",
            gojs(go.Shape, "Rectangle",
                {stroke: null, strokeWidth: 0},
                new go.Binding("fill", "key")),
            gojs(go.TextBlock,
                {margin: 7, font: "Bold 14px Sans-Serif"},
                //the text, color, and key are all bound to the same property in the node data
                new go.Binding("text", "key"))
        );

    myDiagram.linkTemplate =
        gojs(go.Link,
            {routing: go.Link.Orthogonal, corner: 10},
            gojs(go.Shape, {strokeWidth: 2}),
            gojs(go.Shape, {toArrow: "OpenTriangle"})
        );

    // define the group template
    myDiagram.groupTemplate =
        gojs(go.Group, "Auto",
            { // define the group's internal layout
                layout: gojs(go.TreeLayout,
                    {angle: 90, arrangement: go.TreeLayout.ArrangementHorizontal, isRealtime: false}),
                // the group begins unexpanded;
                // upon expansion, a Diagram Listener will generate contents for the group
                isSubGraphExpanded: false,
                // when a group is expanded, if it contains no parts, generate a subGraph inside of it
                subGraphExpandedChanged: function (group) {
                    if (group.memberParts.count === 0) {
                        randomGroup(diagramID, group.data.key);
                    }
                }
            },
            gojs(go.Shape, "Rectangle",
                {fill: null, stroke: "gray", strokeWidth: 2}),
            gojs(go.Panel, "Vertical",
                {defaultAlignment: go.Spot.Left, margin: 4},
                gojs(go.Panel, "Horizontal",
                    {defaultAlignment: go.Spot.Top},
                    // the SubGraphExpanderButton is a panel that functions as a button to expand or collapse the subGraph
                    gojs("SubGraphExpanderButton"),
                    gojs(go.TextBlock,
                        {font: "Bold 18px Sans-Serif", margin: 4},
                        new go.Binding("text", "key"))
                ),
                // create a placeholder to represent the area where the contents of the group are
                gojs(go.Placeholder,
                    {padding: new go.Margin(0, 10)})
            )  // end Vertical Panel
        );  // end Group


    const desktopDiagram = getDiagramById(diagramID);
    desktopDiagram.diagram = myDiagram;
    // generate the initial model
    randomGroup(diagramID);
}

// Generate a random number of nodes, including groups.
// If a group's key is given as a parameter, put these nodes inside it
function randomGroup(diagramID, group) {
    const myDiagram = getDiagramById(diagramID).diagram;
    // all modification to the diagram is within this transaction
    myDiagram.startTransaction("addGroupContents");
    const addedKeys = [];  // this will contain the keys of all nodes created
    let groupCount = 0;  // the number of groups in the diagram, to determine the numbers in the keys of new groups
    myDiagram.nodes.each(function (node) {
        if (node instanceof go.Group) groupCount++;
    });
    // create a random number of groups
    // ensure there are at least 10 groups in the diagram
    let groups = Math.floor(Math.random() * 2);
    if (groupCount < 10) groups += 1;
    for (var i = 0; i < groups; i++) {
        const name = "group" + (i + groupCount);
        myDiagram.model.addNodeData({key: name, isGroup: true, group: group});
        addedKeys.push(name);
    }
    const nodes = Math.floor(Math.random() * 3) + 2;
    // create a random number of non-group nodes
    for (var i = 0; i < nodes; i++) {
        const color = go.Brush.randomColor();
        // make sure the color, which will be the node's key, is unique in the diagram before adding the new node
        if (myDiagram.findPartForKey(color) === null) {
            myDiagram.model.addNodeData({key: color, group: group});
            addedKeys.push(color);
        }
    }
    // add at least one link from each node to another
    // this could result in clusters of nodes unreachable from each other, but no lone nodes
    const arr = [];
    for (let x = 0; x < addedKeys.length; x++) {
        arr.push(addedKeys[x]);
    }
    arr.sort(function (x, y) {
        return Math.random(2) - 1;
    });
    for (var i = 0; i < arr.length; i++) {
        const from = Math.floor(Math.random() * (arr.length - i)) + i;
        if (from !== i) {
            myDiagram.model.addLinkData({from: arr[from], to: arr[i]});
        }
    }
    myDiagram.commitTransaction("addGroupContents");
}