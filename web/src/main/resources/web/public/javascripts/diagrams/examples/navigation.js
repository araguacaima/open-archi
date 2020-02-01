var navigationDiagram = {};

function initNavigation(diagramID) {

    navigationDiagram =
        gojs(go.Diagram, "diagramDiv-" + diagramID,  // Diagram refers to its DIV HTML element by id
            {
                initialContentAlignment: go.Spot.Center,
                maxSelectionCount: 1 // no more than 1 element can be selected at a time
            });

    // define the node template
    navigationDiagram.nodeTemplate =
        gojs(go.Node, "Auto",
            new go.Binding("location", "loc"),
            {
                locationSpot: go.Spot.Center,
                toEndSegmentLength: 30, fromEndSegmentLength: 30
            },
            gojs(go.Shape, "Rectangle",
                {
                    name: "OBJSHAPE",
                    fill: "white",
                    desiredSize: new go.Size(30, 30)
                }),
            gojs(go.TextBlock,
                {margin: 4},
                new go.Binding("text", "key")),
            {
                toolTip:  //  define a tooltip for each node that displays its information
                    gojs(go.Adornment, "Auto",
                        gojs(go.Shape, {fill: "#EFEFCC"}),
                        gojs(go.TextBlock, {margin: 4},
                            new go.Binding("text", "", getInfo))
                    )
            }
        );

    // define the link template
    navigationDiagram.linkTemplate =
        gojs(go.Link,
            {
                selectionAdornmentTemplate:
                    gojs(go.Adornment,
                        gojs(go.Shape,
                            {isPanelMain: true, stroke: "dodgerblue", strokeWidth: 3}),
                        gojs(go.Shape,
                            {toArrow: "Standard", fill: "dodgerblue", stroke: null, scale: 1})
                    ),
                routing: go.Link.Normal,
                curve: go.Link.Bezier,
                toShortLength: 2
            },
            gojs(go.Shape,  //  the link shape
                {name: "OBJSHAPE"}),
            gojs(go.Shape,  //  the arrowhead
                {name: "ARWSHAPE", toArrow: "Standard"}),
            {
                toolTip:  //  define a tooltip for each link that displays its information
                    gojs(go.Adornment, "Auto",
                        gojs(go.Shape, {fill: "#EFEFCC"}),
                        gojs(go.TextBlock, {margin: 4},
                            new go.Binding("text", "", getInfo))
                    )
            }
        );

    // define the group template
    navigationDiagram.groupTemplate =
        gojs(go.Group, "Spot",
            {
                selectionAdornmentTemplate: // adornment when a group is selected
                    gojs(go.Adornment, "Auto",
                        gojs(go.Shape, "Rectangle",
                            {fill: null, stroke: "dodgerblue", strokeWidth: 3}),
                        gojs(go.Placeholder)
                    ),
                toSpot: go.Spot.AllSides, // links coming into groups at any side
                toEndSegmentLength: 30, fromEndSegmentLength: 30
            },
            gojs(go.Panel, "Auto",
                gojs(go.Shape, "Rectangle",
                    {
                        name: "OBJSHAPE",
                        parameter1: 14,
                        fill: "rgba(255,0,0,0.10)"
                    },
                    new go.Binding("desiredSize", "ds")),
                gojs(go.Placeholder,
                    {padding: 16})
            ),
            gojs(go.TextBlock,
                {
                    name: "GROUPTEXT",
                    alignment: go.Spot.TopLeft,
                    alignmentFocus: new go.Spot(0, 0, 4, 4),
                    font: "Bold 10pt Sans-Serif"
                },
                new go.Binding("text", "key")),
            {
                toolTip:  //  define a tooltip for each group that displays its information
                    gojs(go.Adornment, "Auto",
                        gojs(go.Shape, {fill: "#EFEFCC"}),
                        gojs(go.TextBlock, {margin: 4},
                            new go.Binding("text", "", getInfo))
                    )
            }
        );

    // add nodes, including groups, and links to the model
    navigationDiagram.model = new go.GraphLinksModel(
        [ // node data
            {key: "A", loc: new go.Point(320, 100)},
            {key: "B", loc: new go.Point(420, 200)},
            {key: "C", group: "Psi", loc: new go.Point(250, 225)},
            {key: "D", group: "Omega", loc: new go.Point(270, 325)},
            {key: "E", group: "Phi", loc: new go.Point(120, 225)},
            {key: "F", group: "Omega", loc: new go.Point(200, 350)},
            {key: "G", loc: new go.Point(180, 450)},
            {key: "Chi", isGroup: true},
            {key: "Psi", isGroup: true, group: "Chi"},
            {key: "Phi", isGroup: true, group: "Psi"},
            {key: "Omega", isGroup: true, group: "Psi"}
        ],
        [  // link data
            {from: "A", to: "B"},
            {from: "A", to: "C"},
            {from: "A", to: "C"},
            {from: "B", to: "B"},
            {from: "B", to: "C"},
            {from: "B", to: "Omega"},
            {from: "C", to: "A"},
            {from: "C", to: "Psi"},
            {from: "C", to: "D"},
            {from: "D", to: "F"},
            {from: "E", to: "F"},
            {from: "F", to: "G"}
        ]);

    // whenever selection changes, run updateHighlights
    navigationDiagram.addDiagramListener("ChangedSelection",
        function () {
            updateHighlights(getRadioButton());
        });

    navigationDiagram.select(navigationDiagram.findNodeForKey('C'));
    const desktopDiagram = getDiagramById(diagramID);
    desktopDiagram.diagram = navigationDiagram;
}

// This highlights all graph objects that should be highlighted
// whenever a radio button is checked or selection changes.
// Parameter e is the checked radio button.
function updateHighlights(e) {
    // Set highlight to 0 for everything before updating
    navigationDiagram.nodes.each(function (node) {
        node.highlight = 0;
    });
    navigationDiagram.links.each(function (link) {
        link.highlight = 0;
    });

    // Get the selected GraphObject and run the appropriate method
    const sel = navigationDiagram.selection.first();
    if (sel !== null) {
        switch (e.id) {
            case "linksIn":
                linksTo(sel, 1);
                break;
            case "linksOut":
                linksFrom(sel, 1);
                break;
            case "linksAll":
                linksAll(sel, 1);
                break;
            case "nodesIn":
                nodesTo(sel, 1);
                break;
            case "nodesOut":
                nodesFrom(sel, 1);
                break;
            case "nodesConnect":
                nodesConnect(sel, 1);
                break;
            case "nodesReach":
                nodesReach(sel, 1);
                break;
            case "group":
                containing(sel, 1);
                break;
            case "groupsAll":
                containingAll(sel, 1);
                break;
            case "nodesMember":
                childNodes(sel, 1);
                break;
            case "nodesMembersAll":
                allMemberNodes(sel, 1);
                break;
            case "linksMember":
                childLinks(sel, 1);
                break;
            case "linksMembersAll":
                allMemberLinks(sel, 1);
                break;
        }
    }

    // Give everything the appropriate highlighting ( color and width of stroke )
    // nodes, including groups
    navigationDiagram.nodes.each(function (node) {
        const shp = node.findObject("OBJSHAPE");
        const grp = node.findObject("GROUPTEXT");
        const hl = node.highlight;
        highlight(shp, grp, hl);
    });
    // links
    navigationDiagram.links.each(function (link) {
        const hl = link.highlight;
        const shp = link.findObject("OBJSHAPE");
        const arw = link.findObject("ARWSHAPE");
        highlight(shp, arw, hl);
    });
}

// Functions for highlighting, called by updateHighlights.
// x in each case is the selected object or the object being treated as such.
// Some have return values for use by each other or for tooltips.

// if the link connects to this node, highlight it
function linksTo(x, i) {
    if (x instanceof go.Node) {
        x.findLinksInto().each(function (link) {
            link.highlight = i;
        });
    }
}

// if the link comes from this node, highlight it
function linksFrom(x, i) {
    if (x instanceof go.Node) {
        x.findLinksOutOf().each(function (link) {
            link.highlight = i;
        });
    }
}

// highlight all links connected to this node
function linksAll(x, i) {
    if (x instanceof go.Node) {
        x.linksConnected.each(function (link) {
            link.highlight = i;
        });
    }
}

// If selected object is a link, highlight its fromNode.
// Otherwise highlight the fromNode of each link coming into the selected node.
// Return a List of the keys of the nodes.
function nodesTo(x, i) {
    const nodesToList = new go.List("string");
    if (x instanceof go.Link) {
        x.fromNode.highlight = i;
        nodesToList.add(x.data.from);
    } else {
        x.findNodesInto().each(function (node) {
            node.highlight = i;
            nodesToList.add(node.data.key);
        });
    }
    return nodesToList;
}

// same as nodesTo, but 'from' instead of 'to'
function nodesFrom(x, i) {
    const nodesFromList = new go.List("string");
    if (x instanceof go.Link) {
        x.toNode.highlight = i;
        nodesFromList.add(x.data.to);
    } else {
        x.findNodesOutOf().each(function (node) {
            node.highlight = i;
            nodesFromList.add(node.data.key);
        });
    }
    return nodesFromList;
}

// If x is a link, highlight its toNode, or if it is a node, the node(s) it links to,
// and then call nodesReach on the highlighted node(s), with the next color.
// Do not highlight any node that has already been highlit with a color
// indicating a closer relationship to the original node.
function nodesReach(x, i) {
    if (x instanceof go.Link) {
        x.toNode.highlight = i;
        nodesReach(x.toNode, i + 1);
    } else {
        x.findNodesOutOf().each(function (node) {
            if (node.highlight === 0 || node.highlight > i) {
                node.highlight = i;
                nodesReach(node, i + 1);
            }
        });
    }
}

// highlight all nodes linked to this one
function nodesConnect(x, i) {
    if (x instanceof go.Link) {
        x.toNode.highlight = i;
        x.fromNode.highlight = i;
    } else {
        x.findNodesConnected().each(function (node) {
            node.highlight = i;
        });
    }
}

// highlights the group containing this object, specific method for links
// returns the containing group of x
function containing(x, i) {
    const container = x.containingGroup;
    if (container !== null) container.highlight = i;
    return container;
}

// container is the group that contains this node and
// will be the parameter x for the next call of this function.
// Calling containing(x,i) highlights each group the appropriate color
function containingAll(x, i) {
    containing(x, i);
    const container = x.containingGroup;
    if (container !== null) containingAll(container, i + 1);
}

// if the Node"s containingGroup is x, highlight it
function childNodes(x, i) {
    const childLst = new go.List("string");
    if (x instanceof go.Group) {
        navigationDiagram.nodes.each(function (node) {
            if (node.containingGroup === x) {
                node.highlight = i;
                childLst.add(node.data.key);
            }
        });
    }
    return childLst;
}

// same as childNodes, then run allMemberNodes for each child Group with the next color
function allMemberNodes(x, i) {
    if (x instanceof go.Group) {
        navigationDiagram.nodes.each(function (node) {
            if (node.containingGroup === x) {
                node.highlight = i;
                allMemberNodes(node, i + 1);
            }
        });
    }
}

// if the link"s containing Group is x, highlight it
function childLinks(x, i) {
    const childLst = new go.List(go.Link);
    navigationDiagram.links.each(function (link) {
        if (link.containingGroup === x) {
            link.highlight = i;
            childLst.add(link);
        }
    });
    return childLst;
}

// same as childLinks, then run allMemberLinks for each child Group with the next color
function allMemberLinks(x, i) {
    childLinks(x, i);
    navigationDiagram.nodes.each(function (node) {
        if (node instanceof go.Group && node.containingGroup === x) {
            allMemberLinks(node, i + 1);
        }
    });
}

// perform the actual highlighting
function highlight(shp, obj2, hl) {
    let color;
    let width = 3;
    if (hl === 0) {
        color = "black";
        width = 1;
    }
    else if (hl === 1) {
        color = "blue";
    }
    else if (hl === 2) {
        color = "green";
    }
    else if (hl === 3) {
        color = "orange";
    }
    else if (hl === 4) {
        color = "red";
    }
    else {
        color = "purple";
    }
    shp.stroke = color;
    shp.strokeWidth = width;
    if (obj2 !== null) {
        obj2.stroke = color;
        obj2.fill = color;
    }
}

// return the selected radio button in "highlight"
function getRadioButton() {
    const radio = document.getElementsByName("highlight");
    for (let i = 0; i < radio.length; i++)
        if (radio[i].checked) return radio[i];
}

// returns the text for a tooltip, param obj is the text itself
function getInfo(model, obj) {
    const x = obj.panel.adornedPart; // the object that the mouse is over
    let text = ""; // what will be displayed
    if (x instanceof go.Node) {
        if (x instanceof go.Group) text += "Group: "; else text += "Node: ";
        text += x.data.key;
        const toLst = nodesTo(x, 0); // display names of nodes going into this node
        if (toLst.count > 0) {
            toLst.sort(function (a, b) {
                return a < b ? -1 : 1
            });
            text += "\nNodes into: ";
            toLst.each(function (key) {
                if (key !== text.substring(text.length - 3, text.length - 2)) {
                    text += key + ", ";
                }
            });
            text = text.substring(0, text.length - 2);
        }
        const frLst = nodesFrom(x, 0); // display names of nodes coming out of this node
        if (frLst.count > 0) {
            frLst.sort(function (a, b) {
                return a < b ? -1 : 1
            });
            text += "\nNodes out of: ";
            frLst.each(function (key) {
                if (key !== text.substring(text.length - 3, text.length - 2)) {
                    text += key + ", ";
                }
            });
            text = text.substring(0, text.length - 2);
        }
        const grpC = containing(x, 0); // if the node is in a group, display its name
        if (grpC !== null) text += "\nContaining SubGraph: " + grpC.data.key;
        if (x instanceof go.Group) {
            // if it"s a group, also display nodes and links contained in it
            text += "\nMember nodes: ";
            const children = childNodes(x, 0);
            children.sort(function (a, b) {
                return a < b ? -1 : 1
            });
            children.each(function (key) {
                if (key !== text.substring(text.length - 3, text.length - 2)) {
                    text += key + ", ";
                }
            });
            text = text.substring(0, text.length - 2);

            const linkChildren = childLinks(x, 0);
            if (linkChildren.count > 0) {
                text += "\nMember links: ";
                const linkStrings = new go.List("string");
                linkChildren.each(function (link) {
                    linkStrings.add(link.data.from + " --> " + link.data.to);
                });
                linkStrings.sort(function (a, b) {
                    return a < b ? -1 : 1
                });
                linkStrings.each(function (str) {
                    text += str + ", ";
                });
                text = text.substring(0, text.length - 2);
            }
        }
    } else if (x instanceof go.Link) {
        // if it"s a link, display its to and from nodes
        text += "Link: " + x.data.from + " --> " + x.data.to +
            "\nNode To: " + x.data.to + "\nNode From: " + x.data.from;
        const grp = containing(x, 0); // and containing group, if it has one
        if (grp !== null) text += "\nContaining SubGraph: " + grp.data.key;
    }
    return text;
}