function initGantt(diagramID) {

    let myDiagram =
        gojs(go.Diagram, "diagramDiv-" + diagramID,  // Diagram refers to its DIV HTML element by id
            {
                _widthFactor: 1,        // a scale for the nodes' positions and widths
                isReadOnly: true,       // deny the user permission to alter the diagram or zoom in or out
                allowZoom: false,
                "grid.visible": true,  // display a grid in the background of the diagram
                "grid.gridCellSize": new go.Size(30, 150)
            });

    // create the template for the standard nodes
    myDiagram.nodeTemplateMap.add("",
        gojs(go.Node, "Auto",
            // links come from the right and go to the left side of the top of the node
            {fromSpot: go.Spot.Right, toSpot: new go.Spot(0.001, 0, 11, 0)},
            gojs(go.Shape, "Rectangle",
                {height: 15},
                new go.Binding("fill", "color"),
                new go.Binding("width", "width", function (w) {
                    return scaleWidth(myDiagram, w);
                })),
            gojs(go.TextBlock,
                {margin: 2, alignment: go.Spot.Left},
                new go.Binding("text", "key")),
            // using a function in the Binding allows the value to
            // change when Diagram.updateAllTargetBindings is called
            new go.Binding("location", "loc",
                function (l) {
                    return new go.Point(scaleWidth(myDiagram, l.x), l.y);
                })
        ));

    // create the template for the start node
    myDiagram.nodeTemplateMap.add("start",
        gojs(go.Node,
            {fromSpot: go.Spot.Right, toSpot: go.Spot.Top, selectable: false},
            gojs(go.Shape, "Diamond",
                {height: 15, width: 15}),
            // make the location of the start node is not scalable
            new go.Binding("location", "loc")
        ));

    // create the template for the end node
    myDiagram.nodeTemplateMap.add("end",
        gojs(go.Node,
            {fromSpot: go.Spot.Right, toSpot: go.Spot.Top, selectable: false},
            gojs(go.Shape, "Diamond",
                {height: 15, width: 15}),
            // make the location of the end node (with location.x < 0) scalable
            new go.Binding("location", "loc",
                function (l) {
                    if (l.x >= 0) return new go.Point(scaleWidth(myDiagram, l.x), l.y);
                    else return l;
                })
        ));

    // create the link template
    myDiagram.linkTemplate =
        gojs(go.Link,
            {
                routing: go.Link.Orthogonal,
                corner: 3, toShortLength: 2,
                selectable: false
            },
            gojs(go.Shape,
                {strokeWidth: 2}),
            gojs(go.Shape,
                {toArrow: "OpenTriangle"})
        );

    // add the nodes and links to the model
    myDiagram.model = new go.GraphLinksModel(
        [ // node data
            {key: "a", color: "coral", width: 120, loc: new go.Point(scaleWidth(myDiagram, 0), 40)},
            {key: "b", color: "turquoise", width: 160, loc: new go.Point(scaleWidth(myDiagram, 0), 60)},
            {key: "c", color: "coral", width: 150, loc: new go.Point(scaleWidth(myDiagram, 120), 80)},
            {key: "d", color: "turquoise", width: 190, loc: new go.Point(scaleWidth(myDiagram, 120), 100)},
            {key: "e", color: "coral", width: 150, loc: new go.Point(scaleWidth(myDiagram, 270), 120)},
            {key: "f", color: "turquoise", width: 130, loc: new go.Point(scaleWidth(myDiagram, 310), 140)},
            {key: "g", color: "coral", width: 155, loc: new go.Point(scaleWidth(myDiagram, 420), 160)},
            {key: "begin", category: "start", loc: new go.Point(-15, 20)},
            {key: "end", category: "end", loc: new go.Point(scaleWidth(myDiagram, 575), 180)}
        ],
        [ // link data
            {from: "begin", to: "a"},
            {from: "begin", to: "b"},
            {from: "a", to: "c"},
            {from: "a", to: "d"},
            {from: "b", to: "e"},
            {from: "c", to: "e"},
            {from: "d", to: "f"},
            {from: "e", to: "g"},
            {from: "f", to: "end"},
            {from: "g", to: "end"}
        ]);

    // add a Graduated panel to show the dates, globally scoped
    dateScale =
        gojs(go.Part, "Graduated",
            {
                graduatedTickUnit: 1, graduatedMin: 0, graduatedMax: 3,
                pickable: false, location: new go.Point(0, 0)
            },
            gojs(go.Shape,
                {name: "line", strokeWidth: 0, geometryString: "M0 0 H" + scaleWidth(myDiagram, 450)}
            ),
            gojs(go.TextBlock,
                {
                    name: "labels",
                    font: "10pt sans-serif",
                    alignmentFocus: new go.Spot(0, 0, -3, -3),
                    graduatedFunction: function (v) {
                        const d = new Date(2017, 6, 23);
                        d.setDate(d.getDate() + v * 7);
                        // format date output to string
                        const options = {month: "short", day: "2-digit"};
                        return d.toLocaleDateString("en-US", options);
                    }
                }
            )
        );
    myDiagram.add(dateScale);
    const desktopDiagram = getDiagramById(diagramID);
    desktopDiagram.diagram = myDiagram;
}

// scale the number according to the current widthFactor
function scaleWidth(myDiagram, num) {
    return num * myDiagram._widthFactor;
}

// change the grid's cell size and the widthFactor,
// then update Bindings to scale the widths and positions of nodes,
// as well as the width of the date scale
function rescale(diagramID) {
    const val = parseFloat(document.getElementById("widthSlider-"+diagramID).value);
    const myDiagram = getDiagramById(diagramID).diagram;
    myDiagram.startTransaction("rescale");
    myDiagram.grid.gridCellSize = new go.Size(val, 150);
    myDiagram._widthFactor = val / 30;
    myDiagram.updateAllTargetBindings();
    // update width of date scale and maybe change interval of labels if too small
    const width = scaleWidth(myDiagram, 450);
    dateScale.findObject("line").geometryString = "M0 0 H" + width;
    if (width >= 140) dateScale.findObject("labels").interval = 1;
    if (width < 140) dateScale.findObject("labels").interval = 2;
    if (width < 70) dateScale.findObject("labels").interval = 4;
    myDiagram.commitTransaction("rescale");
}