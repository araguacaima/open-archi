function initIcons(diagramID) {

    // a collection of colors
    const colors = {
        blue: "#00B5CB",
        orange: "#F47321",
        green: "#C8DA2B",
        gray: "#888",
        white: "#F5F5F5"
    };

    let myDiagram = gojs(go.Diagram, "diagramDiv-" + diagramID,  // create a Diagram for the DIV HTML element
        {
            initialContentAlignment: go.Spot.Center,
            "undoManager.isEnabled": true,
            layout: gojs(go.TreeLayout)
        });

    // "icons" is defined in icons.js

    // A data binding conversion function. Given an icon name, return a Geometry.
    // This assumes that all icons want to be filled.
    // This caches the Geometry, because the Geometry may be shared by multiple Shapes.
    function geoFunc(geoname) {
        let geo = icons_[geoname];
        if (geo === undefined) geo = "heart";  // use this for an unknown icon name
        if (typeof geo === "string") {
            geo = icons_[geoname] = go.Geometry.parse(geo, true);  // fill each geometry
        }
        return geo;
    }

    // Define a simple template consisting of the icon surrounded by a filled circle
    myDiagram.nodeTemplate =
        gojs(go.Node, "Auto",
            gojs(go.Shape, "Circle",
                {fill: "lightcoral", strokeWidth: 4, stroke: colors["gray"], width: 60, height: 60},
                new go.Binding("fill", "color")),
            gojs(go.Shape,
                {margin: 3, fill: colors["white"], strokeWidth: 0},
                new go.Binding("geometry", "geo", geoFunc)),
            // Each node has a tooltip that reveals the name of its icon
            {
                toolTip:
                    gojs(go.Adornment, "Auto",
                        gojs(go.Shape, {fill: "LightYellow", stroke: colors["gray"], strokeWidth: 2}),
                        gojs(go.TextBlock, {margin: 8, stroke: colors["gray"], font: "bold 16px sans-serif"},
                            new go.Binding("text", "geo")))
            }
        );

    // Define a Link template that routes orthogonally, with no arrowhead
    myDiagram.linkTemplate =
        gojs(go.Link,
            {routing: go.Link.Orthogonal, corner: 5, toShortLength: -2, fromShortLength: -2},
            gojs(go.Shape, {strokeWidth: 5, stroke: colors["gray"]})); // the link shape

    // Create the model data that will be represented by Nodes and Links
    myDiagram.model = new go.GraphLinksModel(
        [
            {key: 1, geo: "file", color: colors["blue"]},
            {key: 2, geo: "alarm", color: colors["orange"]},
            {key: 3, geo: "lab", color: colors["blue"]},
            {key: 4, geo: "earth", color: colors["blue"]},
            {key: 5, geo: "heart", color: colors["green"]},
            {key: 6, geo: "arrow-up-right", color: colors["blue"]},
            {key: 7, geo: "html5", color: colors["orange"]},
            {key: 8, geo: "twitter", color: colors["orange"]}
        ],
        [
            {from: 1, to: 2},
            {from: 1, to: 3},
            {from: 3, to: 4},
            {from: 4, to: 5},
            {from: 4, to: 6},
            {from: 3, to: 7},
            {from: 3, to: 8}
        ]);


    // The second Diagram showcases every icon in icons.js
    myDiagram2 = gojs(go.Diagram, "diagramDiv2-" + diagramID,
        { // share node templates between both Diagrams
            nodeTemplate: myDiagram.nodeTemplate,
            // simple grid layout
            layout: gojs(go.GridLayout)
        });

    // Convert the icons collection into an Array of JavaScript objects
    const nodeArray = [];
    for (let k in icons_) {
        nodeArray.push({geo: k, color: colors["blue"]});
    }
    myDiagram2.model.nodeDataArray = nodeArray;
    const desktopDiagram = getDiagramById(diagramID);
    desktopDiagram.diagram = myDiagram;

}