function initCheckBoxes(diagramID) {

    let myDiagram = gojs(go.Diagram, "diagramDiv-" + diagramID,  // create a Diagram for the DIV HTML element
        {
            initialContentAlignment: go.Spot.Center,  // center the content
            "undoManager.isEnabled": true  // enable undo & redo
        });

    // this template includes a lot of CheckBoxes, each configured in different manners
    myDiagram.nodeTemplate =
        gojs(go.Node, "Auto",  // the Shape will go around the whole table
            gojs(go.Shape, {strokeWidth: 0},  // no border
                new go.Binding("fill", "color")),
            gojs(go.Panel, "Table",
                {padding: 3},
                gojs(go.TextBlock,
                    {row: 0, column: 0, columnSpan: 2},
                    {margin: 3, font: "bold 10pt sans-serif"},  // some room around the bold text
                    new go.Binding("text", "key")),
                // the first column has an assortment of CheckBoxes
                gojs(go.Panel, "Vertical",
                    {row: 1, column: 0, defaultAlignment: go.Spot.Left},
                    gojs("CheckBox", "choice1",
                        gojs(go.TextBlock, "default")
                    ),
                    gojs("CheckBox", "choice2",
                        {"ButtonIcon.stroke": "green"},
                        gojs(go.TextBlock, "green")
                    ),
                    gojs("CheckBox", "choice3",
                        {"ButtonIcon.stroke": "red", "ButtonIcon.figure": "XLine"},
                        gojs(go.TextBlock, "red X")
                    ),
                    gojs("CheckBox", "choice4",
                        {"_buttonFillOver": "pink", "_buttonStrokeOver": "red"},
                        gojs(go.TextBlock, "pink over")
                    ),
                    gojs("CheckBox", "choice5",
                        {"Button.width": 32, "Button.height": 32},
                        gojs(go.TextBlock, "BIG",
                            {font: "bold 12pt sans-serif"})
                    ),
                    gojs("CheckBox", "choice6",
                        {
                            "Button.width": 20, "Button.height": 20,
                            "ButtonBorder.figure": "Circle", "ButtonBorder.stroke": "blue",
                            "ButtonIcon.figure": "Circle", "ButtonIcon.fill": "blue",
                            "ButtonIcon.strokeWidth": 0, "ButtonIcon.desiredSize": new go.Size(10, 10)
                        },
                        gojs(go.TextBlock, "blue circle")
                    ),
                    gojs("CheckBox", "choice7", go.Panel.Vertical,
                        gojs(go.TextBlock, "vertical")
                    )
                ),
                // the second column is a list of CheckBoxes
                gojs(go.Panel, "Table",
                    {
                        row: 1, column: 1,
                        alignment: go.Spot.Top,
                        minSize: new go.Size(50, NaN),
                        itemTemplate:
                            gojs("CheckBox", "checked", go.Panel.TableRow,
                                gojs(go.TextBlock,  // align text towards the right, next to the Button
                                    {column: 0, alignment: go.Spot.Right},
                                    new go.Binding("text", "name")),
                                {"Button.column": 1}  // put Button in second column, to the right of text
                            )
                    },
                    new go.Binding("itemArray", "items")
                ),
                // now a checkbox at the bottom of the whole table
                gojs("CheckBox", "",  // not data bound
                    {row: 3, columnSpan: 2, alignment: go.Spot.Left},
                    // this checkbox is not bound to model data, but it does toggle the Part.movable
                    // property of the Node that this is in
                    gojs(go.TextBlock, "Node is not movable"),
                    { // _doClick is executed within a transaction by the CheckBoxButton click function
                        "_doClick": function (e, obj) {
                            obj.part.movable = !obj.part.movable;  // toggle the Part.movable flag
                        }
                    }
                )
            )
        );

    // but use the default Link template, by not setting Diagram.linkTemplate

    // create the model data that will be represented by Nodes and Links
    myDiagram.model =
        gojs(go.GraphLinksModel,
            {
                copiesArrays: true,
                copiesArrayObjects: true,
                nodeDataArray:
                    [
                        {
                            key: "Alpha",
                            color: "lightblue",
                            choice1: true,
                            choice2: true,
                            choice3: true,
                            choice4: true,
                            choice5: true,
                            choice6: true,
                            choice7: true,
                            items: [{name: "item 0"},
                                {name: "item 1"},
                                {name: "item 2"}]
                        },
                        {
                            key: "Beta", color: "orange",
                            items: [{name: "B1", checked: false},
                                {name: "Bee2", checked: true}]
                        },
                        {
                            key: "Gamma", color: "lightgreen",
                            items: [{name: "C-one", checked: true},
                                {name: "C-two", checked: true},
                                {name: "C-three"}]
                        },
                        {
                            key: "Delta", color: "pink", choice1: true, choice2: false,
                            items: []
                        }
                    ],
                linkDataArray:
                    [
                        {from: "Alpha", to: "Beta"},
                        {from: "Alpha", to: "Gamma"},
                        {from: "Gamma", to: "Delta"},
                        {from: "Delta", to: "Alpha"}
                    ]
            });
    myDiagram.layoutDiagram();
    const desktopDiagram = getDiagramById(diagramID);
    desktopDiagram.diagram = myDiagram;
}