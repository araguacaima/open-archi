const groupTemplate = gojs(
    go.Group, "Auto",
    groupStyle(),
    {
        // highlight when dragging into the Group
        mouseDragEnter: function (e, grp, prev) {
            highlightGroup(e, grp, true);
        },
        mouseDragLeave: function (e, grp, next) {
            highlightGroup(e, grp, false);
            let selection = e.diagram.selection;
            if (selection.size === 0) return;
            let ok = true;
            selection.each(selection => {
                ok = ok && (selection.data.group === grp.key);
            });
            if (ok) {
                if (!e.diagram.lastInput.shift) {
                    e.diagram.currentTool.doCancel();
                }
            }
        },
        ungroupable: true,
        computesBoundsAfterDrag: true,
        // when the selection is dropped into a Group, add the selected Parts into that Group;
        // if it fails, cancel the tool, rolling back any changes
        mouseDrop: function (e, grp) {
            let selection = e.diagram.selection;
            let ok = true;
            selection.each(selection => {
                ok = ok && (selection.data.group === grp.key);
            });
            if (ok) {
                grp.diagram.currentTool.doCancel();
            } else {
                ok = (grp !== null
                    ? grp.addMembers(grp.diagram.selection, true)
                    : e.diagram.commandHandler.addTopLevelParts(selection, true));
                if (!ok) {
                    e.diagram.currentTool.doCancel();
                }
            }
            fixMeta();
        },
        handlesDragDropForMembers: true,  // don't need to define handlers on member Nodes and Links
        // Groups containing Groups lay out their members horizontally
        layout:
            gojs(go.GridLayout,
                {
                    wrappingWidth: Infinity, alignment: go.GridLayout.Position,
                    cellSize: new go.Size(1, 1), spacing: new go.Size(12, 12)
                })
    },
    gojs(go.Shape, "Rectangle",
        {
            name: "SHAPE",
            stretch: go.GraphObject.Horizontal,
            strokeWidth: 2
        },
        new go.Binding("fill", "", OpenArchiWrapper.toFillSecondary),
        new go.Binding("stroke", "", OpenArchiWrapper.toFillPrimary)
    ),
    gojs(go.Panel, "Vertical",  // title above Placeholder
        gojs(go.Panel, "Horizontal",  // button next to TextBlock
            {
                name: "HEADER",
                stretch: go.GraphObject.Horizontal
            },
            new go.Binding("background", "", OpenArchiWrapper.toFillPrimary),
            gojs("SubGraphExpanderButton",
                {alignment: go.Spot.Right, margin: 5}),
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
        ),  // end Horizontal Panel
        gojs(go.Placeholder,
            {
                padding: 10,
                alignment: go.Spot.TopLeft
            }
        )
    ),  // end Vertical Panel
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
