function initEntityRelationship(diagramID) {

    let myDiagram =
        gojs(go.Diagram, "diagramDiv-" + diagramID,  // must name or refer to the DIV HTML element
            {
                initialContentAlignment: go.Spot.Center,
                allowDelete: false,
                allowCopy: false,
                layout: gojs(go.ForceDirectedLayout),
                "undoManager.isEnabled": true
            });

    // define several shared Brushes
    const bluegrad = gojs(go.Brush, "Linear", {
        0: "rgb(150, 150, 250)",
        0.5: "rgb(86, 86, 186)",
        1: "rgb(86, 86, 186)"
    });
    const greengrad = gojs(go.Brush, "Linear", {0: "rgb(158, 209, 159)", 1: "rgb(67, 101, 56)"});
    const redgrad = gojs(go.Brush, "Linear", {0: "rgb(206, 106, 100)", 1: "rgb(180, 56, 50)"});
    const yellowgrad = gojs(go.Brush, "Linear", {0: "rgb(254, 221, 50)", 1: "rgb(254, 182, 50)"});
    const lightgrad = gojs(go.Brush, "Linear", {1: "#E6E6FA", 0: "#FFFAF0"});

    // the template for each attribute in a node's array of item data
    const itemTempl =
        gojs(go.Panel, "Horizontal",
            gojs(go.Shape,
                {desiredSize: new go.Size(10, 10)},
                new go.Binding("figure", "figure"),
                new go.Binding("fill", "color")),
            gojs(go.TextBlock,
                {
                    stroke: "#333333",
                    font: "bold 14px sans-serif"
                },
                new go.Binding("text", "name"))
        );

    // define the Node template, representing an entity
    myDiagram.nodeTemplate =
        gojs(go.Node, "Auto",  // the whole node panel
            {
                selectionAdorned: true,
                resizable: true,
                layoutConditions: go.Part.LayoutStandard & ~go.Part.LayoutNodeSized,
                fromSpot: go.Spot.AllSides,
                toSpot: go.Spot.AllSides,
                isShadowed: true,
                shadowColor: "#C5C1AA"
            },
            new go.Binding("location", "location").makeTwoWay(),
            // whenever the PanelExpanderButton changes the visible property of the "LIST" panel,
            // clear out any desiredSize set by the ResizingTool.
            new go.Binding("desiredSize", "visible", function (v) {
                return new go.Size(NaN, NaN);
            }).ofObject("LIST"),
            // define the node's outer shape, which will surround the Table
            gojs(go.Shape, "Rectangle",
                {fill: lightgrad, stroke: "#756875", strokeWidth: 3}),
            gojs(go.Panel, "Table",
                {margin: 8, stretch: go.GraphObject.Fill},
                gojs(go.RowColumnDefinition, {row: 0, sizing: go.RowColumnDefinition.None}),
                // the table header
                gojs(go.TextBlock,
                    {
                        row: 0, alignment: go.Spot.Center,
                        margin: new go.Margin(0, 14, 0, 2),  // leave room for Button
                        font: "bold 16px sans-serif"
                    },
                    new go.Binding("text", "key")),
                // the collapse/expand button
                gojs("PanelExpanderButton", "LIST",  // the name of the element whose visibility this button toggles
                    {row: 0, alignment: go.Spot.TopRight}),
                // the list of Panels, each showing an attribute
                gojs(go.Panel, "Vertical",
                    {
                        name: "LIST",
                        row: 1,
                        padding: 3,
                        alignment: go.Spot.TopLeft,
                        defaultAlignment: go.Spot.Left,
                        stretch: go.GraphObject.Horizontal,
                        itemTemplate: itemTempl
                    },
                    new go.Binding("itemArray", "items"))
            )  // end Table Panel
        );  // end Node

    // define the Link template, representing a relationship
    myDiagram.linkTemplate =
        gojs(go.Link,  // the whole link panel
            {
                selectionAdorned: true,
                layerName: "Foreground",
                reshapable: true,
                routing: go.Link.AvoidsNodes,
                corner: 5,
                curve: go.Link.JumpOver
            },
            gojs(go.Shape,  // the link shape
                {stroke: "#303B45", strokeWidth: 2.5}),
            gojs(go.TextBlock,  // the "from" label
                {
                    textAlign: "center",
                    font: "bold 14px sans-serif",
                    stroke: "#1967B3",
                    segmentIndex: 0,
                    segmentOffset: new go.Point(NaN, NaN),
                    segmentOrientation: go.Link.OrientUpright
                },
                new go.Binding("text", "text")),
            gojs(go.TextBlock,  // the "to" label
                {
                    textAlign: "center",
                    font: "bold 14px sans-serif",
                    stroke: "#1967B3",
                    segmentIndex: -1,
                    segmentOffset: new go.Point(NaN, NaN),
                    segmentOrientation: go.Link.OrientUpright
                },
                new go.Binding("text", "toTitle"))
        );

    // create the model for the E-R diagram
    const nodeDataArray = [
        {
            key: "Products",
            items: [{name: "ProductID", iskey: true, figure: "Decision", color: yellowgrad},
                {name: "ProductName", iskey: false, figure: "Cube1", color: bluegrad},
                {name: "SupplierID", iskey: false, figure: "Decision", color: "purple"},
                {name: "CategoryID", iskey: false, figure: "Decision", color: "purple"}]
        },
        {
            key: "Suppliers",
            items: [{name: "SupplierID", iskey: true, figure: "Decision", color: yellowgrad},
                {name: "CompanyName", iskey: false, figure: "Cube1", color: bluegrad},
                {name: "ContactName", iskey: false, figure: "Cube1", color: bluegrad},
                {name: "Address", iskey: false, figure: "Cube1", color: bluegrad}]
        },
        {
            key: "Categories",
            items: [{name: "CategoryID", iskey: true, figure: "Decision", color: yellowgrad},
                {name: "CategoryName", iskey: false, figure: "Cube1", color: bluegrad},
                {name: "Description", iskey: false, figure: "Cube1", color: bluegrad},
                {name: "Picture", iskey: false, figure: "TriangleUp", color: redgrad}]
        },
        {
            key: "Order Details",
            items: [{name: "OrderID", iskey: true, figure: "Decision", color: yellowgrad},
                {name: "ProductID", iskey: true, figure: "Decision", color: yellowgrad},
                {name: "UnitPrice", iskey: false, figure: "MagneticData", color: greengrad},
                {name: "Quantity", iskey: false, figure: "MagneticData", color: greengrad},
                {name: "Discount", iskey: false, figure: "MagneticData", color: greengrad}]
        },
    ];
    const linkDataArray = [
        {from: "Products", to: "Suppliers", text: "0..N", toText: "1"},
        {from: "Products", to: "Categories", text: "0..N", toText: "1"},
        {from: "Order Details", to: "Products", text: "0..N", toText: "1"}
    ];
    myDiagram.model = new go.GraphLinksModel(nodeDataArray, linkDataArray);
    const desktopDiagram = getDiagramById(diagramID);
    desktopDiagram.diagram = myDiagram;
}