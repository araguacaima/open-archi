// These parameters need to be set before defining the templates.
MINLENGTH = 200;  // this controls the minimum length of any swimlane
MINBREADTH = 20;  // this controls the minimum breadth of any non-collapsed swimlane
var swimLaneDiagram = {};
// some shared functions

// this may be called to force the lanes to be laid out again
function relayoutLanes() {
    swimLaneDiagram.nodes.each(function (lane) {
        if (!(lane instanceof go.Group)) return;
        if (lane.category === "Pool") return;
        lane.layout.isValidLayout = false;  // force it to be invalid
    });
    swimLaneDiagram.layoutDiagram();
}

// this is called after nodes have been moved or lanes resized, to layout all of the Pool Groups again
function relayoutDiagram() {
    swimLaneDiagram.layout.invalidateLayout();
    swimLaneDiagram.findTopLevelGroups().each(function (g) {
        if (g.category === "Pool") g.layout.invalidateLayout();
    });
    swimLaneDiagram.layoutDiagram();
}

// compute the minimum size of a Pool Group needed to hold all of the Lane Groups
function computeMinPoolSize(pool) {
    // assert(pool instanceof go.Group && pool.category === "Pool");
    let len = MINLENGTH;
    pool.memberParts.each(function (lane) {
        // pools ought to only contain lanes, not plain Nodes
        if (!(lane instanceof go.Group)) return;
        const holder = lane.placeholder;
        if (holder !== null) {
            const sz = holder.actualBounds;
            len = Math.max(len, sz.width);
        }
    });
    return new go.Size(len, NaN);
}

// compute the minimum size for a particular Lane Group
function computeLaneSize(lane) {
    // assert(lane instanceof go.Group && lane.category !== "Pool");
    const sz = computeMinLaneSize(lane);
    if (lane.isSubGraphExpanded) {
        const holder = lane.placeholder;
        if (holder !== null) {
            const hsz = holder.actualBounds;
            sz.height = Math.max(sz.height, hsz.height);
        }
    }
    // minimum breadth needs to be big enough to hold the header
    const hdr = lane.findObject("HEADER");
    if (hdr !== null) sz.height = Math.max(sz.height, hdr.actualBounds.height);
    return sz;
}

// determine the minimum size of a Lane Group, even if collapsed
function computeMinLaneSize(lane) {
    if (!lane.isSubGraphExpanded) return new go.Size(MINLENGTH, 1);
    return new go.Size(MINLENGTH, MINBREADTH);
}


// define a custom ResizingTool to limit how far one can shrink a lane Group
function LaneResizingTool() {
    go.ResizingTool.call(this);
}

go.Diagram.inherit(LaneResizingTool, go.ResizingTool);

LaneResizingTool.prototype.isLengthening = function () {
    return (this.handle.alignment === go.Spot.Right);
};

/** @override */
LaneResizingTool.prototype.computeMinSize = function () {
    const lane = this.adornedObject.part;
    // assert(lane instanceof go.Group && lane.category !== "Pool");
    const msz = computeMinLaneSize(lane);  // get the absolute minimum size
    if (this.isLengthening()) {  // compute the minimum length of all lanes
        var sz = computeMinPoolSize(lane.containingGroup);
        msz.width = Math.max(msz.width, sz.width);
    } else {  // find the minimum size of this single lane
        var sz = computeLaneSize(lane);
        msz.width = Math.max(msz.width, sz.width);
        msz.height = Math.max(msz.height, sz.height);
    }
    return msz;
};

/** @override */
LaneResizingTool.prototype.resize = function (newr) {
    const lane = this.adornedObject.part;
    if (this.isLengthening()) {  // changing the length of all of the lanes
        lane.containingGroup.memberParts.each(function (lane) {
            if (!(lane instanceof go.Group)) return;
            const shape = lane.resizeObject;
            if (shape !== null) {  // set its desiredSize length, but leave each breadth alone
                shape.width = newr.width;
            }
        });
    } else {  // changing the breadth of a single lane
        go.ResizingTool.prototype.resize.call(this, newr);
    }
    relayoutDiagram();  // now that the lane has changed size, layout the pool again
};
// end LaneResizingTool class


// define a custom grid layout that makes sure the length of each lane is the same
// and that each lane is broad enough to hold its subgraph
function PoolLayout() {
    go.GridLayout.call(this);
    this.cellSize = new go.Size(1, 1);
    this.wrappingColumn = 1;
    this.wrappingWidth = Infinity;
    this.isRealtime = false;  // don't continuously layout while dragging
    this.alignment = go.GridLayout.Position;
    // This sorts based on the location of each Group.
    // This is useful when Groups can be moved up and down in order to change their order.
    this.comparer = function (a, b) {
        const ay = a.location.y;
        const by = b.location.y;
        if (isNaN(ay) || isNaN(by)) return 0;
        if (ay < by) return -1;
        if (ay > by) return 1;
        return 0;
    };
}

go.Diagram.inherit(PoolLayout, go.GridLayout);

/** @override */
PoolLayout.prototype.doLayout = function (coll) {
    const diagram = this.diagram;
    if (diagram === null) return;
    diagram.startTransaction("PoolLayout");
    const pool = this.group;
    if (pool !== null && pool.category === "Pool") {
        // make sure all of the Group Shapes are big enough
        const minsize = computeMinPoolSize(pool);
        pool.memberParts.each(function (lane) {
            if (!(lane instanceof go.Group)) return;
            if (lane.category !== "Pool") {
                const shape = lane.resizeObject;
                if (shape !== null) {  // change the desiredSize to be big enough in both directions
                    const sz = computeLaneSize(lane);
                    shape.width = (isNaN(shape.width) ? minsize.width : Math.max(shape.width, minsize.width));
                    shape.height = (!isNaN(shape.height)) ? Math.max(shape.height, sz.height) : sz.height;
                    const cell = lane.resizeCellSize;
                    if (!isNaN(shape.width) && !isNaN(cell.width) && cell.width > 0) shape.width = Math.ceil(shape.width / cell.width) * cell.width;
                    if (!isNaN(shape.height) && !isNaN(cell.height) && cell.height > 0) shape.height = Math.ceil(shape.height / cell.height) * cell.height;
                }
            }
        });
    }
    // now do all of the usual stuff, according to whatever properties have been set on this GridLayout
    go.GridLayout.prototype.doLayout.call(this, coll);
    diagram.commitTransaction("PoolLayout");
};

// end PoolLayout class


function initSwimLanes(diagramID) {

    swimLaneDiagram = gojs(go.Diagram, "diagramDiv-" + diagramID,  // create a Diagram for the DIV HTML element
        {
            // start everything in the middle of the viewport
            initialContentAlignment: go.Spot.Center,
            // use a custom ResizingTool (along with a custom ResizeAdornment on each Group)
            resizingTool: new LaneResizingTool(),
            // use a simple layout that ignores links to stack the top-level Pool Groups next to each other
            layout: gojs(PoolLayout),
            // don't allow dropping onto the diagram's background unless they are all Groups (lanes or pools)
            mouseDragOver: function (e) {
                if (!e.diagram.selection.all(function (n) {
                        return n instanceof go.Group;
                    })) {
                    e.diagram.currentCursor = 'not-allowed';
                }
            },
            mouseDrop: function (e) {
                if (!e.diagram.selection.all(function (n) {
                        return n instanceof go.Group;
                    })) {
                    e.diagram.currentTool.doCancel();
                }
            },
            // a clipboard copied node is pasted into the original node's group (i.e. lane).
            "commandHandler.copiesGroupKey": true,
            // automatically re-layout the swim lanes after dragging the selection
            "SelectionMoved": relayoutDiagram,  // this DiagramEvent listener is
            "SelectionCopied": relayoutDiagram, // defined above
            "animationManager.isEnabled": false,
            // enable undo & redo
            "undoManager.isEnabled": true,
            allowDrop: true
        });

    // this is a Part.dragComputation function for limiting where a Node may be dragged
    function stayInGroup(part, pt, gridpt) {
        // don't constrain top-level nodes
        const grp = part.containingGroup;
        if (grp === null) return pt;
        // try to stay within the background Shape of the Group
        const back = grp.resizeObject;
        if (back === null) return pt;
        // allow dragging a Node out of a Group if the Shift key is down
        if (part.diagram.lastInput.shift) return pt;
        const p1 = back.getDocumentPoint(go.Spot.TopLeft);
        const p2 = back.getDocumentPoint(go.Spot.BottomRight);
        const b = part.actualBounds;
        const loc = part.location;
        // find the padding inside the group's placeholder that is around the member parts
        const m = grp.placeholder.padding;
        // now limit the location appropriately
        const x = Math.max(p1.x + m.left, Math.min(pt.x, p2.x - m.right - b.width - 1)) + (loc.x - b.x);
        const y = Math.max(p1.y + m.top, Math.min(pt.y, p2.y - m.bottom - b.height - 1)) + (loc.y - b.y);
        return new go.Point(x, y);
    }

    swimLaneDiagram.nodeTemplate =
        gojs(go.Node, "Auto",
            new go.Binding("location", "loc", go.Point.parse).makeTwoWay(go.Point.stringify),
            gojs(go.Shape, "Rectangle",
                {fill: "white", portId: "", cursor: "pointer", fromLinkable: true, toLinkable: true}),
            gojs(go.TextBlock, {margin: 5},
                new go.Binding("text", "key")),
            {dragComputation: stayInGroup} // limit dragging of Nodes to stay within the containing Group, defined above
        );

    function groupStyle() {  // common settings for both Lane and Pool Groups
        return [
            {
                layerName: "Background",  // all pools and lanes are always behind all nodes and links
                background: "transparent",  // can grab anywhere in bounds
                movable: true, // allows users to re-order by dragging
                copyable: false,  // can't copy lanes or pools
                avoidable: false,  // don't impede AvoidsNodes routed Links
                minLocation: new go.Point(NaN, -Infinity),  // only allow vertical movement
                maxLocation: new go.Point(NaN, Infinity)
            },
            new go.Binding("location", "loc", go.Point.parse).makeTwoWay(go.Point.stringify)
        ];
    }

    // hide links between lanes when either lane is collapsed
    function updateCrossLaneLinks(group) {
        group.findExternalLinksConnected().each(function (l) {
            l.visible = (l.fromNode.isVisible() && l.toNode.isVisible());
        });
    }

    // each Group is a "swimlane" with a header on the left and a resizable lane on the right
    swimLaneDiagram.groupTemplate =
        gojs(go.Group, "Horizontal", groupStyle(),
            {
                selectionObjectName: "SHAPE",  // selecting a lane causes the body of the lane to be highlit, not the label
                resizable: true, resizeObjectName: "SHAPE",  // the custom resizeAdornmentTemplate only permits two kinds of resizing
                layout: gojs(go.LayeredDigraphLayout,  // automatically lay out the lane's subgraph
                    {
                        isInitial: false,  // don't even do initial layout
                        isOngoing: false,  // don't invalidate layout when nodes or links are added or removed
                        direction: 0,
                        columnSpacing: 10,
                        layeringOption: go.LayeredDigraphLayout.LayerLongestPathSource
                    }),
                computesBoundsAfterDrag: true,  // needed to prevent recomputing Group.placeholder bounds too soon
                computesBoundsIncludingLinks: false,  // to reduce occurrences of links going briefly outside the lane
                computesBoundsIncludingLocation: true,  // to support empty space at top-left corner of lane
                handlesDragDropForMembers: true,  // don't need to define handlers on member Nodes and Links
                mouseDrop: function (e, grp) {  // dropping a copy of some Nodes and Links onto this Group adds them to this Group
                    if (!e.shift) return;  // cannot change groups with an unmodified drag-and-drop
                    // don't allow drag-and-dropping a mix of regular Nodes and Groups
                    if (!e.diagram.selection.any(function (n) {
                            return n instanceof go.Group;
                        })) {
                        const ok = grp.addMembers(grp.diagram.selection, true);
                        if (ok) {
                            updateCrossLaneLinks(grp);
                        } else {
                            grp.diagram.currentTool.doCancel();
                        }
                    } else {
                        e.diagram.currentTool.doCancel();
                    }
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
                }
            },
            new go.Binding("isSubGraphExpanded", "expanded").makeTwoWay(),
            // the lane header consisting of a Shape and a TextBlock
            gojs(go.Panel, "Horizontal",
                {
                    name: "HEADER",
                    angle: 270,  // maybe rotate the header to read sideways going up
                    alignment: go.Spot.Center
                },
                gojs(go.Panel, "Horizontal",  // this is hidden when the swimlane is collapsed
                    new go.Binding("visible", "isSubGraphExpanded").ofObject(),
                    gojs(go.Shape, "Diamond",
                        {width: 8, height: 8, fill: "white"},
                        new go.Binding("fill", "color")),
                    gojs(go.TextBlock,  // the lane label
                        {font: "bold 13pt sans-serif", editable: true, margin: new go.Margin(2, 0, 0, 0)},
                        new go.Binding("text", "text").makeTwoWay())
                ),
                gojs("SubGraphExpanderButton", {margin: 5})  // but this remains always visible!
            ),  // end Horizontal Panel
            gojs(go.Panel, "Auto",  // the lane consisting of a background Shape and a Placeholder representing the subgraph
                gojs(go.Shape, "Rectangle",  // this is the resized object
                    {name: "SHAPE", fill: "white"},
                    new go.Binding("fill", "color"),
                    new go.Binding("desiredSize", "size", go.Size.parse).makeTwoWay(go.Size.stringify)),
                gojs(go.Placeholder,
                    {padding: 12, alignment: go.Spot.TopLeft}),
                gojs(go.TextBlock,  // this TextBlock is only seen when the swimlane is collapsed
                    {
                        name: "LABEL",
                        font: "bold 13pt sans-serif", editable: true,
                        angle: 0, alignment: go.Spot.TopLeft, margin: new go.Margin(2, 0, 0, 4)
                    },
                    new go.Binding("visible", "isSubGraphExpanded", function (e) {
                        return !e;
                    }).ofObject(),
                    new go.Binding("text", "text").makeTwoWay())
            )  // end Auto Panel
        );  // end Group

    // define a custom resize adornment that has two resize handles if the group is expanded
    swimLaneDiagram.groupTemplate.resizeAdornmentTemplate =
        gojs(go.Adornment, "Spot",
            gojs(go.Placeholder),
            gojs(go.Shape,  // for changing the length of a lane
                {
                    alignment: go.Spot.Right,
                    desiredSize: new go.Size(7, 50),
                    fill: "lightblue", stroke: "dodgerblue",
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
                    fill: "lightblue", stroke: "dodgerblue",
                    cursor: "row-resize"
                },
                new go.Binding("visible", "", function (ad) {
                    if (ad.adornedPart === null) return false;
                    return ad.adornedPart.isSubGraphExpanded;
                }).ofObject())
        );

    swimLaneDiagram.groupTemplateMap.add("Pool",
        gojs(go.Group, "Auto", groupStyle(),
            { // use a simple layout that ignores links to stack the "lane" Groups on top of each other
                layout: gojs(PoolLayout, {spacing: new go.Size(0, 0)})  // no space between lanes
            },
            gojs(go.Shape,
                {fill: "white"},
                new go.Binding("fill", "color")),
            gojs(go.Panel, "Table",
                {defaultColumnSeparatorStroke: "black"},
                gojs(go.Panel, "Horizontal",
                    {column: 0, angle: 270},
                    gojs(go.TextBlock,
                        {font: "bold 16pt sans-serif", editable: true, margin: new go.Margin(2, 0, 0, 0)},
                        new go.Binding("text").makeTwoWay())
                ),
                gojs(go.Placeholder,
                    {column: 1})
            )
        ));

    swimLaneDiagram.linkTemplate =
        gojs(go.Link,
            {routing: go.Link.AvoidsNodes, corner: 5},
            {relinkableFrom: true, relinkableTo: true},
            gojs(go.Shape),
            gojs(go.Shape, {toArrow: "Standard"})
        );

    // define some sample graphs in some of the lanes
    swimLaneDiagram.model = new go.GraphLinksModel(
        [ // node data
            {key: "Pool1", text: "Pool", isGroup: true, category: "Pool"},
            {key: "Pool2", text: "Pool2", isGroup: true, category: "Pool"},
            {key: "Lane1", text: "Lane1", isGroup: true, group: "Pool1", color: "lightblue"},
            {key: "Lane2", text: "Lane2", isGroup: true, group: "Pool1", color: "lightgreen"},
            {key: "Lane3", text: "Lane3", isGroup: true, group: "Pool1", color: "lightyellow"},
            {key: "Lane4", text: "Lane4", isGroup: true, group: "Pool1", color: "orange"},
            {key: "oneA", group: "Lane1"},
            {key: "oneB", group: "Lane1"},
            {key: "oneC", group: "Lane1"},
            {key: "oneD", group: "Lane1"},
            {key: "twoA", group: "Lane2"},
            {key: "twoB", group: "Lane2"},
            {key: "twoC", group: "Lane2"},
            {key: "twoD", group: "Lane2"},
            {key: "twoE", group: "Lane2"},
            {key: "twoF", group: "Lane2"},
            {key: "twoG", group: "Lane2"},
            {key: "fourA", group: "Lane4"},
            {key: "fourB", group: "Lane4"},
            {key: "fourC", group: "Lane4"},
            {key: "fourD", group: "Lane4"},
            {key: "Lane5", text: "Lane5", isGroup: true, group: "Pool2", color: "lightyellow"},
            {key: "Lane6", text: "Lane6", isGroup: true, group: "Pool2", color: "lightgreen"},
            {key: "fiveA", group: "Lane5"},
            {key: "sixA", group: "Lane6"}
        ],
        [ // link data
            {from: "oneA", to: "oneB"},
            {from: "oneA", to: "oneC"},
            {from: "oneB", to: "oneD"},
            {from: "oneC", to: "oneD"},
            {from: "twoA", to: "twoB"},
            {from: "twoA", to: "twoC"},
            {from: "twoA", to: "twoF"},
            {from: "twoB", to: "twoD"},
            {from: "twoC", to: "twoD"},
            {from: "twoD", to: "twoG"},
            {from: "twoE", to: "twoG"},
            {from: "twoF", to: "twoG"},
            {from: "fourA", to: "fourB"},
            {from: "fourB", to: "fourC"},
            {from: "fourC", to: "fourD"}
        ]);
    const desktopDiagram = getDiagramById(diagramID);
    desktopDiagram.diagram = swimLaneDiagram;
    // force all lanes' layouts to be performed
    relayoutLanes();
}  // end init

// Show the diagram's model in JSON format
function save() {
    document.getElementById("modelToSaveOrLoad").value = JSON.stringify(swimLaneDiagram.model, null, 2);
    let textContent = swimLaneDiagram.model.toJson();
    swimLaneDiagram.isModified = false;
}

function load() {
    swimLaneDiagram.model = go.Model.fromJson(document.getElementById("modelToSaveOrLoad").value);
    swimLaneDiagram.delayInitialization(relayoutDiagram);
}