const architectureModelTemplate = gojs(
    go.Group, "Horizontal",
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
        layout: gojs(PoolLayout, {spacing: new go.Size(8, 8)}),  // no space between lanes
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
    new go.Binding("background", "", OpenArchiWrapper.toFillPrimary),
    new go.Binding("isSubGraphExpanded", "expanded").makeTwoWay(),
    // the lane header consisting of a Shape and a TextBlock
    gojs(go.Panel, "Horizontal",
        {
            name: "HEADER",
            angle: 270,  // maybe rotate the header to read sideways going up
            alignment: go.Spot.Center,
        },
        new go.Binding("background", "", OpenArchiWrapper.toFillPrimary),
        gojs(go.Panel, "Horizontal",  // this is hidden when the swimlane is collapsed
            new go.Binding("visible", "isSubGraphExpanded").ofObject(),
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
            gojs(go.TextBlock,  // the lane label
                {
                    font: "bold 13pt sans-serif",
                    editable: true,
                    margin: new go.Margin(2, 0, 0, 0),
                    alignment: go.Spot.BottomCenter,
                    name: "LABEL"
                },
                new go.Binding("stroke", "", OpenArchiWrapper.toTextPrimary),
                new go.Binding("text", "", OpenArchiWrapper.toTitle).makeTwoWay(OpenArchiWrapper.fromTitle))
        ),
        gojs("SubGraphExpanderButton", {margin: 5})  // but this remains always visible!
    ),  // end Horizontal Panel
    gojs(go.Panel, "Auto",  // the lane consisting of a background Shape and a Placeholder representing the subgraph
        gojs(go.Shape, "Rectangle",  // this is the resized object
            {
                name: "SHAPE",
                strokeWidth: 2,
            },
            new go.Binding("stroke", "", OpenArchiWrapper.toFillPrimary),
            new go.Binding("fill", "", OpenArchiWrapper.toFillSecondary)
        ),
        gojs(go.Placeholder,
            {
                padding: 10,
                alignment: go.Spot.TopLeft,

            }),
        gojs(go.Panel, "Table",
            {
                margin: 2,
                maxSize: new go.Size(200, NaN),
                name: "HEADER"
            },
            new go.Binding("background", "", OpenArchiWrapper.toFillPrimary).makeTwoWay(OpenArchiWrapper.toFillPrimary),
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
                    column: 1,
                    angle: 0,
                    margin: 2,
                    font: "bold 13pt sans-serif",
                    alignment: go.Spot.BottomRight
                },
                new go.Binding("stroke", "", OpenArchiWrapper.toTextPrimary),
                new go.Binding("text", "", OpenArchiWrapper.toTitle).makeTwoWay(OpenArchiWrapper.fromTitle))
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
