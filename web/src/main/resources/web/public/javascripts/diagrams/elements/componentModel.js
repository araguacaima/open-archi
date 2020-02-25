

const componentModelTemplate = gojs(
    go.Group, "Auto",
    groupStyle(),
    { // use a simple layout that ignores links to stack the "lane" Groups on top of each other
        selectionObjectName: "SHAPE",  // selecting a lane causes the body of the lane to be highlit, not the label
        resizable: true,
        resizeObjectName: "SHAPE",  // the custom resizeAdornmentTemplate only permits two kinds of resizing
        movable: true, // allows users to re-order by dragging
        copyable: true,  // can't copy lanes or pools
        avoidable: false,  // don't impede AvoidsNodes routed Links
        minLocation: new go.Point(NaN, -Infinity),  // only allow vertical movement
        maxLocation: new go.Point(NaN, Infinity),
        layerName: "Background",  // all pools and lanes are always behind all nodes and links
        layout: gojs(TableLayout/*, {spacing: new go.Size(3, 3)}*/),  // no space between lanes
        ungroupable: true,
        computesBoundsAfterDrag: true,  // needed to prevent recomputing Group.placeholder bounds too soon
        computesBoundsIncludingLinks: false,  // to reduce occurrences of links going briefly outside the lane
        computesBoundsIncludingLocation: true,  // to support empty space at top-left corner of lane
        handlesDragDropForMembers: true,  // don't need to define handlers on member Nodes and Links
        resizeAdornmentTemplate:
            gojs(go.Adornment, "Spot",
                gojs(go.Placeholder),
                gojs(go.Shape,  // for changing the length of a lane
                    {
                        alignment: go.Spot.Right,
                        desiredSize: new go.Size(7, 50),
                        fill: "lightblue",
                        stroke: "dodgerblue",
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
                        fill: "lightblue",
                        stroke: "dodgerblue",
                        cursor: "row-resize"
                    },
                    new go.Binding("visible", "", function (ad) {
                        if (ad.adornedPart === null) return false;
                        return ad.adornedPart.isSubGraphExpanded;
                    }).ofObject())
            ),
        contextMenu: partContextMenu,
        // the Node.location is at the center of each node
        locationSpot: go.Spot.Center,
        //isShadowed: true,
        //shadowColor: "#888",
        // handle mouse enter/leave events to show/hide the ports
        mouseDrop: function (e, grp) {  // dropping a copy of some Nodes and Links onto this Group adds them to this Group
            let ok = true;
            e.targetDiagram.selection.each(selection => {
                ok = ok && (selection.data.group === grp.key);
            });
            if (ok) {
                grp.diagram.currentTool.doCancel();
            } else {
                ok = grp.addMembers(grp.diagram.selection, true);
            }
            if (ok) {
                relayoutLanes(e.diagram);
                updateCrossLaneLinks(grp);
            } else {
                grp.diagram.currentTool.doCancel();
            }
            fixMeta();
        },
        mouseDragEnter: function (e, group, prev) {
            //TODO AMM: This highlaighing doesn't works
            //highlight(group, true);
            e.handled = true;
        },
        mouseDragLeave: function (e, group, next) {
            //TODO AMM: This highlaighing doesn't works
            //highlight(group, false);
            let selection = e.diagram.selection;
            if (selection.size === 0) return;
            let ok = true;
            selection.each(selection => {
                ok = ok && (selection.data.group === group.key);
            });
            if (ok) {
                if (!e.diagram.lastInput.shift) {
                    e.diagram.currentTool.doCancel();
                }
            }
            relayoutLanes(e.diagram);
            e.handled = true;
        },
        subGraphExpandedChanged: function (grp) {
            const shp = grp.resizeObject;
            if (grp.diagram.undoManager.isUndoingRedoing) return;
            if (grp.isSubGraphExpanded) {
                shp.height = grp._savedBreadth;
            } else {
                grp._savedBreadth = shp.height;
                shp.height = NaN;
            }
            updateCrossLaneLinks(grp);
        },
        stretch: go.GraphObject.Fill
    },
    new go.Binding("clonedFrom", "clonedFrom"),
    gojs(go.Shape,
        {
            strokeWidth: 3
        },
        new go.Binding("fill", "", OpenArchiWrapper.toFillPrimary),
        new go.Binding("figure", "", OpenArchiWrapper.toFigure).makeTwoWay(OpenArchiWrapper.fromFigure),
       /* new go.Binding("minSize", "", OpenArchiWrapper.toSize).makeTwoWay(OpenArchiWrapper.fromSize),*/
        new go.Binding("stroke", "", OpenArchiWrapper.toStrokePrimary),
        new go.Binding("isSubGraphExpanded", "expanded").makeTwoWay()
    ),
    gojs(go.Panel, "Table",
        {
            defaultColumnSeparatorStroke: "black",
            padding: 10
        },
        gojs(go.Panel, "Horizontal",
            {
                column: 0,
                angle: 270
            },
            new go.Binding("visible", "isSubGraphExpanded").ofObject(),
            gojs("SubGraphExpanderButton", {margin: 5}),
            gojs(go.Panel, {
                    name: "IMAGE"
                },
                new go.Binding("isSubGraphExpanded", "expanded").makeTwoWay(),
                new go.Binding("visible", "isSubGraphExpanded").ofObject(),
                gojs(go.Picture,
                    {
                        row: 0,
                        column: 1,
                        margin: 2,
                        maxSize: new go.Size(60, 60),
                        imageStretch: go.GraphObject.Uniform,
                        alignment: go.Spot.TopRight
                    },
                    new go.Binding("source", "", OpenArchiWrapper.toImage).makeTwoWay(OpenArchiWrapper.fromImage),
                    new go.Binding("minSize", "source", function (e) {
                        return e === undefined ? new go.Size(0, 0) : new go.Size(30, 30);
                    }).ofObject())),
            gojs(go.TextBlock, "Text",
                {
                    font: "bold 17pt Helvetica, Arial, sans-serif",
                    maxSize: new go.Size(160, NaN),
                    wrap: go.TextBlock.WrapFit,
                    editable: true
                },
                new go.Binding("text", "", OpenArchiWrapper.toTitle),
                new go.Binding("stroke", "", OpenArchiWrapper.toFillSecondary),
                /*new go.Binding("minSize", "", OpenArchiWrapper.toSize).makeTwoWay(OpenArchiWrapper.fromSize)*/
            )
        ),
        gojs(go.Placeholder,
            {column: 1, padding: 10})
    ),
    gojs(go.Panel, "Auto",  // the lane consisting of a background Shape and a Placeholder representing the subgraph
        gojs(go.Shape, "Rectangle",  // this is the resized object
            {
                name: "SHAPE",
                strokeWidth: 2,
            },
            new go.Binding("fill", "", OpenArchiWrapper.toStrokePrimary),
            new go.Binding("stroke", "", OpenArchiWrapper.toFillSecondary)
        ),
        gojs(go.Panel, "Table",
            {
                margin: 2,
                maxSize: new go.Size(200, NaN),
                name: "HEADER"
            },
            new go.Binding("visible", "isSubGraphExpanded", function (e) {
                return !e;
            }).ofObject(),
            // the two TextBlocks in column 0 both stretch in width
            // but align on the left side
            gojs(go.RowColumnDefinition,
                {
                    column: 0,
                    stretch: go.GraphObject.Horizontal,
                    alignment: go.Spot.Left
                }),
            gojs("SubGraphExpanderButton", {row: 0, column: 0}),  // but this remains always visible!
            gojs(go.Picture,
                {
                    name: "IMAGE",
                    row: 0,
                    column: 1,
                    margin: 2,
                    maxSize: new go.Size(30, 30),
                    imageStretch: go.GraphObject.Uniform,
                    alignment: go.Spot.TopLeft
                },
                new go.Binding("source", "", OpenArchiWrapper.toImage).makeTwoWay(OpenArchiWrapper.fromImage),
                new go.Binding("minSize", "source", function (e) {
                    return e === undefined ? new go.Size(0, 0) : new go.Size(30, 30);
                }).ofObject()
            ),

            gojs(go.TextBlock,  // this TextBlock is only seen when the swimlane is collapsed
                {
                    name: "LABEL",
                    editable: true,
                    row: 0,
                    column: 2,
                    angle: 0,
                    margin: 2,
                    font: "bold 13pt sans-serif",
                    alignment: go.Spot.BottomRight
                },
                new go.Binding("text", "", OpenArchiWrapper.toTitle).makeTwoWay(OpenArchiWrapper.fromTitle),
                new go.Binding("stroke", "", OpenArchiWrapper.toFillSecondary)
            )
        )
    ),  // end Auto Panel
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
