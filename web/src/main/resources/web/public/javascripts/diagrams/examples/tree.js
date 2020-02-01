let gojs;
let leafTreeGraph;
let myDiagram;
let leafTreeDiagram;

function initTreeDiagram(diagramID) {
    let myDiagram = gojs(go.Diagram, "diagramDiv-" + diagramID,
        {
            layout: gojs(go.TreeLayout, {nodeSpacing: 4}),
            allowMove: false
        });

    myDiagram.addDiagramListener("ChangedSelection",
        function (e) {
            const position = e.diagram.firstInput.documentPoint;
            position.y = position.y - 50;
            myDiagram.position = position;
        });

    myDiagram.toolManager.hoverDelay = 75;

    const colorsBar = gojs(go.Panel, "Table",

        gojs(go.Shape, "RoundedRectangle", {
                fill: "#F39C11",
                stroke: null,
                row: 0,
                column: 7,
                height: 5,
                width: 20,
                toolTip: gojs(go.Adornment, "Auto",
                    gojs(go.Shape, "RoundedRectangle", {fill: "#FFF600", stroke: null}),
                    gojs(go.TextBlock,
                        {margin: 5, font: "bold 15px Helvetica, bold Arial, sans-serif"},
                        new go.Binding("text", "usedBy", function (s) {
                            let i;
                            for (i = 0; i < s.length; i++) {
                                const origin = s[i].countryName;
                                if (origin === "CORPORATE") {
                                    return origin;
                                }
                            }
                        })))
            },
            new go.Binding("visible", "usedBy", function (s) {
                let i;
                for (i = 0; i < s.length; i++) {
                    const origin = s[i].countryName;
                    if (origin === "CORPORATE") {
                        return true;
                    }
                }
                return false;
            })),

        gojs(go.Shape, "RoundedRectangle", {
                fill: "#4C1130",
                stroke: null,
                row: 0,
                column: 0,
                height: 5,
                width: 20,
                toolTip: gojs(go.Adornment, "Auto",
                    gojs(go.Shape, "RoundedRectangle", {fill: "#FFF600", stroke: null}),
                    gojs(go.TextBlock,
                        {margin: 5, font: "bold 15px Helvetica, bold Arial, sans-serif"},
                        new go.Binding("text", "usedBy", function (s) {
                            let i;
                            for (i = 0; i < s.length; i++) {
                                const origin = s[i].countryName;
                                if (origin === "ES") {
                                    return origin;
                                }
                            }
                        })))
            },
            new go.Binding("visible", "usedBy", function (s) {
                let i;
                for (i = 0; i < s.length; i++) {
                    const origin = s[i].countryName;
                    if (origin === "ES") {
                        return true;
                    }
                }
                return false;
            })),

        gojs(go.Shape, "RoundedRectangle", {
                fill: "#0000FF",
                stroke: null,
                row: 0,
                column: 1,
                height: 5,
                width: 20,
                toolTip: gojs(go.Adornment, "Auto",
                    gojs(go.Shape, "RoundedRectangle", {fill: "#FFF600", stroke: null}),
                    gojs(go.TextBlock,
                        {margin: 5, font: "bold 15px Helvetica, bold Arial, sans-serif"},
                        new go.Binding("text", "usedBy", function (s) {
                            let i;
                            for (i = 0; i < s.length; i++) {
                                const origin = s[i].countryName;
                                if (origin === "MX") {
                                    return origin;
                                }
                            }
                        })))
            },
            new go.Binding("visible", "usedBy", function (s) {
                let i;
                for (i = 0; i < s.length; i++) {
                    const origin = s[i].countryName;
                    if (origin === "MX") {
                        return true;
                    }
                }
                return false;
            })),

        gojs(go.Shape, "RoundedRectangle", {
                fill: "#FF3300",
                stroke: null,
                row: 0,
                column: 2,
                height: 5,
                width: 20,
                toolTip: gojs(go.Adornment, "Auto",
                    gojs(go.Shape, "RoundedRectangle", {fill: "#FFF600", stroke: null}),
                    gojs(go.TextBlock,
                        {margin: 5, font: "bold 15px Helvetica, bold Arial, sans-serif"},
                        new go.Binding("text", "usedBy", function (s) {
                            let i;
                            for (i = 0; i < s.length; i++) {
                                const origin = s[i].countryName;
                                if (origin === "CO") {
                                    return origin;
                                }
                            }
                        })))
            },
            new go.Binding("visible", "usedBy", function (s) {
                let i;
                for (i = 0; i < s.length; i++) {
                    const origin = s[i].countryName;
                    if (origin === "CO") {
                        return true;
                    }
                }
                return false;
            })),

        gojs(go.Shape, "RoundedRectangle", {
                fill: "#800080",
                stroke: null,
                row: 0,
                column: 3,
                height: 5,
                width: 20,
                toolTip: gojs(go.Adornment, "Auto",
                    gojs(go.Shape, "RoundedRectangle", {fill: "#FFF600", stroke: null}),
                    gojs(go.TextBlock,
                        {margin: 5, font: "bold 15px Helvetica, bold Arial, sans-serif"},
                        new go.Binding("text", "usedBy", function (s) {
                            let i;
                            for (i = 0; i < s.length; i++) {
                                const origin = s[i].countryName;
                                if (origin === "CL") {
                                    return origin;
                                }
                            }
                        })))
            },
            new go.Binding("visible", "usedBy", function (s) {
                let i;
                for (i = 0; i < s.length; i++) {
                    const origin = s[i].countryName;
                    if (origin === "CL") {
                        return true;
                    }
                }
                return false;
            })),

        gojs(go.Shape, "RoundedRectangle", {
                fill: "#7F6000",
                stroke: null,
                row: 0,
                column: 4,
                height: 5,
                width: 20,
                toolTip: gojs(go.Adornment, "Auto",
                    gojs(go.Shape, "RoundedRectangle", {fill: "#FFF600", stroke: null}),
                    gojs(go.TextBlock,
                        {margin: 5, font: "bold 15px Helvetica, bold Arial, sans-serif"},
                        new go.Binding("text", "usedBy", function (s) {
                            let i;
                            for (i = 0; i < s.length; i++) {
                                const origin = s[i].countryName;
                                if (origin === "PE") {
                                    return origin;
                                }
                            }
                        })))
            },
            new go.Binding("visible", "usedBy", function (s) {
                let i;
                for (i = 0; i < s.length; i++) {
                    const origin = s[i].countryName;
                    if (origin === "PE") {
                        return true;
                    }
                }
                return false;
            })),

        gojs(go.Shape, "RoundedRectangle", {
                fill: "#009900",
                stroke: null,
                row: 0,
                column: 5,
                height: 5,
                width: 20,
                toolTip: gojs(go.Adornment, "Auto",
                    gojs(go.Shape, "RoundedRectangle", {fill: "#FFF600", stroke: null}),
                    gojs(go.TextBlock,
                        {margin: 5, font: "bold 15px Helvetica, bold Arial, sans-serif"},
                        new go.Binding("text", "usedBy", function (s) {
                            let i;
                            for (i = 0; i < s.length; i++) {
                                const origin = s[i].countryName;
                                if (origin === "AR") {
                                    return origin;
                                }
                            }
                        })))
            },
            new go.Binding("visible", "usedBy", function (s) {
                let i;
                for (i = 0; i < s.length; i++) {
                    const origin = s[i].countryName;
                    if (origin === "AR") {
                        return true;
                    }
                }
                return false;
            })),

        gojs(go.Shape, "RoundedRectangle", {
                fill: "#FFC000",
                stroke: null,
                row: 0,
                column: 6,
                height: 5,
                width: 20,
                toolTip: gojs(go.Adornment, "Auto",
                    gojs(go.Shape, "RoundedRectangle", {fill: "#FFF600", stroke: null}),
                    gojs(go.TextBlock,
                        {margin: 5, font: "bold 15px Helvetica, bold Arial, sans-serif"},
                        new go.Binding("text", "usedBy", function (s) {
                            let i;
                            for (i = 0; i < s.length; i++) {
                                const origin = s[i].countryName;
                                if (origin === "VE") {
                                    return origin;
                                }
                            }
                        })))
            },
            new go.Binding("visible", "usedBy", function (s) {
                let i;
                for (i = 0; i < s.length; i++) {
                    const origin = s[i].countryName;
                    if (origin === "VE") {
                        return true;
                    }
                }
                return false;
            })),

        gojs(go.Shape, "RoundedRectangle", {
                fill: "#666633",
                stroke: null,
                row: 0,
                column: 7,
                height: 5,
                width: 20,
                toolTip: gojs(go.Adornment, "Auto",
                    gojs(go.Shape, "RoundedRectangle", {fill: "#FFF600", stroke: null}),
                    gojs(go.TextBlock,
                        {margin: 5, font: "bold 15px Helvetica, bold Arial, sans-serif"},
                        new go.Binding("text", "usedBy", function (s) {
                            let i;
                            for (i = 0; i < s.length; i++) {
                                const origin = s[i].countryName;
                                if (origin === "USA") {
                                    return origin;
                                }
                            }
                        })))
            },
            new go.Binding("visible", "usedBy", function (s) {
                let i;
                for (i = 0; i < s.length; i++) {
                    const origin = s[i].countryName;
                    if (origin === "USA") {
                        return true;
                    }
                }
                return false;
            }))
    );

    myDiagram.nodeTemplate =
        gojs(go.Node, "Auto",
            {
                selectionChanged: onSelectionChanged,
                click: myDiagramNodeClick
            },

            gojs(go.Panel, "Table",
                {
                    margin: 2,
                    defaultAlignment: go.Spot.Left
                },
                gojs(go.RowColumnDefinition, {
                    row: 0,
                    column: 0,
                    height: 5,
                    separatorPadding: 3

                }),
                gojs(go.RowColumnDefinition, {
                    row: 1,
                    column: 0,
                    sizing: go.RowColumnDefinition.None
                }),
                gojs(go.Panel, "Auto",
                    {row: 0, column: 0, alignment: go.Spot.Left},
                    colorsBar
                ),

                gojs(go.Panel, "Auto",
                    {row: 1, column: 0},
                    gojs(go.Shape, "RoundedRectangle", {
                        name: "node",
                        stroke: null
                    }, new go.Binding("fill", "rgb")),
                    gojs(go.TextBlock,
                        {
                            font: "bold 15px Helvetica, bold Arial, sans-serif",
                            stroke: "white", margin: 3
                        },
                        new go.Binding("text", "name"))
                )
            ),
            {
                selectionAdornmentTemplate: gojs(go.Adornment, "Auto",
                    gojs(go.Shape, "RoundedRectangle",
                        {fill: null, stroke: "#053908", strokeWidth: 5}),
                    gojs(go.Placeholder)
                )
            }
        );

    myDiagram.linkTemplate =
        gojs(go.Link, {
                selectable: false
            },
            gojs(go.Shape,
                {strokeWidth: 2, stroke: "#2C2C29"}
            ));

    myDiagram.model = new go.TreeModel(dataArray);
    const desktopDiagram = getDiagramById(diagramID);
    desktopDiagram.diagram = myDiagram;

}
