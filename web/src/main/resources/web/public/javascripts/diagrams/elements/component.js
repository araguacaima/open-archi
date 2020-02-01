const componentTemplate = gojs(
    go.Node, "Spot",
    nodeStyle(),
    {
        selectionAdorned: false
    },
    gojs(go.Panel, "Auto",
        gojs(go.Shape,
            {
                name: "SHAPE"
            },
            new go.Binding("figure", "", OpenArchiWrapper.toFigure).makeTwoWay(OpenArchiWrapper.fromFigure),
            new go.Binding("fill", "", OpenArchiWrapper.toFillPrimary),
            new go.Binding("stroke", "", OpenArchiWrapper.toFillPrimary)
        ),
        {
            mouseDrop: function (e, grp) {
                fixMeta();
            }
        },
        gojs(go.Panel, "Table",
            {
                margin: 6,
                maxSize: new go.Size(200, NaN),
                name: "IMAGE"
            },
            // the two TextBlocks in column 0 both stretch in width
            // but align on the left side
            gojs(go.RowColumnDefinition,
                {
                    column: 0,
                    stretch: go.GraphObject.Horizontal,
                    alignment: go.Spot.Left
                }),
            gojs(go.Picture,
                {
                    name: "IMAGE",
                    row: 0,
                    column: 0,
                    margin: 2,
                    maxSize: new go.Size(30, 30),
                    imageStretch: go.GraphObject.Uniform,
                    alignment: go.Spot.TopLeft
                },
                new go.Binding("source", "", OpenArchiWrapper.toImage).makeTwoWay(OpenArchiWrapper.fromImage)),
            gojs(go.TextBlock,  // this TextBlock is only seen when the swimlane is collapsed
                {
                    name: "LABEL",
                    editable: true,
                    row: 0,
                    column: 1,
                    angle: 0,
                    margin: 2,
                    font: "bold 13pt sans-serif",
                    alignment: go.Spot.BottomRight,
                    stroke: "white"
                },
                new go.Binding("stroke", "", OpenArchiWrapper.toFillSecondary),
                new go.Binding("text", "", OpenArchiWrapper.toTitle).makeTwoWay(OpenArchiWrapper.fromTitle))
        )
    ),
    // decorations...
/*    gojs(go.Panel, "Auto",
        {
            margin: 6,
            maxSize: new go.Size(200, NaN),
            name: "IMAGE",
            background: "#EF9B0F",
            alignment: new go.Spot(1, 0, -2, 2),
        },
        gojs(go.Shape, "RoundedRectangle",
            {
                desiredSize: new go.Size(20, 20),
                strokeWidth: 1,
                opacity: 0.0
            }),
        gojs(go.TextBlock,  // this TextBlock is only seen when the swimlane is collapsed
            {
                name: "LABEL",
                editable: true,
                row: 0,
                column: 1,
                angle: 0,
                margin: 2,
                font: "bold 13pt sans-serif",
                alignment: go.Spot.MiddleLeft,
                stroke: "white"
            },
            new go.Binding("stroke", "", OpenArchiWrapper.toFillSecondary),
            new go.Binding("text", "", OpenArchiWrapper.toTitle).makeTwoWay(OpenArchiWrapper.fromTitle)),
        new go.Binding("opacity", "prototype", function (v) {
            return v ? 1.0 : 0.0;
        })
    ),*/
    // three named ports, one on each side except the top, all output only:
    // four named ports, one on each side:
    makePort("T", go.Spot.Top),
    makePort("L", go.Spot.Left),
    makePort("R", go.Spot.Right),
    makePort("B", go.Spot.Bottom),
    { // this tooltip Adornment is shared by all nodes
        toolTip:
            gojs(go.Adornment, "Auto",
                gojs(go.Shape, {fill: "#FFFFCC"}),
                gojs(go.TextBlock, {margin: 4},  // the tooltip shows the result of calling nodeInfo(data)
                    new go.Binding("text", "", nodeInfo))
            ),
        // this context menu Adornment is shared by all nodes
        contextMenu: partContextMenu
    }
);
