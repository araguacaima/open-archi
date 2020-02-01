function initShopFloorMonitor(diagramID, nodeDataArray, linkDataArray) {

    let myDiagram =
        gojs(go.Diagram, "diagramDiv-" + diagramID,
            {
                "animationManager.isEnabled": false,
                initialContentAlignment: go.Spot.Center
            });

    // conversion functions for Bindings in the Node template:

    function nodeTypeImage(type) {
        switch (type) {
            case "S2":
                return "/images/diagrams/voice atm switch.jpg";
            case "S3":
                return "/images/diagrams/server switch.jpg";
            case "P1":
                return "/images/diagrams/general processor.jpg";
            case "P2":
                return "/images/diagrams/storage array.jpg";
            case "M4":
                return "/images/diagrams/iptv broadcast server.jpg";
            case "M5":
                return "/images/diagrams/content engine.jpg";
            case "I1":
                return "/images/diagrams/pc.jpg";
            default:
                return "/images/diagrams/pc.jpg";
        }
        if (type.charAt(0) === "S") return;
        if (type.charAt(0) === "P") return "images/general processor.jpg";
        if (type.charAt(0) === "M")
            return "images/pc.jpg";
    }

    function nodeProblemConverter(msg) {
        if (msg) return "red";
        return null;
    }

    function nodeOperationConverter(s) {
        if (s >= 2) return "TriangleDown";
        if (s >= 1) return "Rectangle";
        return "Circle";
    }

    function nodeStatusConverter(s) {
        if (s >= 2) return "red";
        if (s >= 1) return "yellow";
        return "green";
    }

    myDiagram.nodeTemplate =
        gojs(go.Node, "Vertical",
            {locationObjectName: "ICON"},
            new go.Binding("location", "loc", go.Point.parse).makeTwoWay(go.Point.stringify),
            gojs(go.Panel, "Spot",
                gojs(go.Panel, "Auto",
                    {name: "ICON"},
                    gojs(go.Shape,
                        {fill: null, stroke: null},
                        new go.Binding("background", "problem", nodeProblemConverter)),
                    gojs(go.Picture,
                        {margin: 5},
                        new go.Binding("source", "type", nodeTypeImage))
                ),  // end Auto Panel
                gojs(go.Shape, "Circle",
                    {
                        alignment: go.Spot.TopLeft, alignmentFocus: go.Spot.TopLeft,
                        width: 12, height: 12, fill: "orange"
                    },
                    new go.Binding("figure", "operation", nodeOperationConverter)),
                gojs(go.Shape, "Triangle",
                    {
                        alignment: go.Spot.TopRight, alignmentFocus: go.Spot.TopRight,
                        width: 12, height: 12, fill: "blue"
                    },
                    new go.Binding("fill", "status", nodeStatusConverter))
            ),  // end Spot Panel
            gojs(go.TextBlock,
                new go.Binding("text"))
        );  // end Node


    // conversion function for Bindings in the Link template:

    function linkProblemConverter(msg) {
        if (msg) return "red";
        return "gray";
    }

    myDiagram.linkTemplate =
        gojs(go.Link, go.Link.AvoidsNodes,
            {corner: 3},
            gojs(go.Shape,
                {strokeWidth: 2, stroke: "gray"},
                new go.Binding("stroke", "problem", linkProblemConverter))
        );

    myDiagram.model = new go.GraphLinksModel(nodeDataArray, linkDataArray);


    // simulate some real-time problem monitoring, once every two seconds:
    function randomProblems() {
        const model = myDiagram.model;
        // update all nodes
        let arr = model.nodeDataArray;
        for (var i = 0; i < arr.length; i++) {
            data = arr[i];
            data.problem = (Math.random() < 0.8) ? "" : "Power loss due to ...";
            data.status = Math.random() * 3;
            data.operation = Math.random() * 3;
            model.updateTargetBindings(data);
        }
        // and update all links
        arr = model.linkDataArray;
        for (i = 0; i < arr.length; i++) {
            data = arr[i];
            data.problem = (Math.random() < 0.7) ? "" : "No Power";
            model.updateTargetBindings(data);
        }
    }

    function loop() {
        setTimeout(function () {
            randomProblems();
            loop();
        }, 2000);
    }
    const desktopDiagram = getDiagramById(diagramID);
    desktopDiagram.diagram = myDiagram;

    loop();  // start the simulation
}

