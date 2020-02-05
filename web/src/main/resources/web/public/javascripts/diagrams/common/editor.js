MINLENGTH = 200;  // this controls the minimum length of any swimlane
MINBREADTH = 20;  // this controls the minimum breadth of any non-collapsed swimlane

function capitalize(text) {
    return text.substr(0, 1).toUpperCase() + text.substr(2, text.length).toLowerCase();
}

// Show the diagram's model in JSON format that the user may edit
function save(desktopDiagram) {

    let diagram = desktopDiagram.diagram;
    let value_ = OpenArchiFromDiagram.process(desktopDiagram);
    if (_.isEmpty(value_)) {
        return;
    }
    if ( desktopDiagram.diagramType) {
        if ( desktopDiagram.diagramType.id) {
            value_.diagramType = {"id": desktopDiagram.diagramType.id};
        } else {
            value_.diagramType = desktopDiagram.diagramType;
        }
    }
    let value = JSON.stringify(value_);
    diagram.isModified = false;
    diagram.model.modelData.position = go.Point.stringify(diagram.position);
    const endpoint = OpenArchiFromDiagram.toEndpoint(value_);
    $.ajax({
        url: basePath + endpoint,
        data: value,
        type: 'POST',
        crossDomain: true,
        contentType: "application/json",
        converters: {
            "text json": function (response) {
                return (response === "") ? null : JSON.parse(response);
            }
        }
    }).done((data, textStatus, response) => {
            if (response.status === 201) {
                createCharm({
                    id: "charm-saving-success",
                    type: "SUCCESS",
                    title: "Diagram created or modifed successfully!"
                });

                retrievePalette(desktopDiagram, fixPalette);

                diagram.startTransaction("Deleting new element");
                const node = diagram.findNodeForKey(value_.key);
                if (node !== null) {
                    diagram.remove(node);
                }
                diagram.model.removeNodeData(value_);
                diagram.requestUpdate();
                diagram.commitTransaction("Deleting new element");

                process(data, desktopDiagram.diagram, data.kind);

            } else {
                const id = value_.id;
                delete value_.id;
                $.ajax({
                    url: basePath + endpoint + "/" + id,
                    data: JSON.stringify(value_),
                    type: 'PUT',
                    crossDomain: true,
                    contentType: "application/json",
                    converters: {
                        "text json": function (response) {
                            return (response === "") ? null : JSON.parse(response);
                        }
                    }
                }).done((data, textStatus, response) => {
                        if (response.status === 200) {
                            createCharm({
                                id: "charm-saving-success",
                                type: "SUCCESS",
                                title: "Diagram created or modifed successfully!"
                            });
                            retrievePalette(desktopDiagram, fixPalette);
                        } else {
                            if (response.status === 201) {
                                createCharm({
                                    id: "charm-saving-success",
                                    type: "SUCCESS",
                                    title: "Diagram created or modifed successfully!"
                                });
                                retrievePalette(desktopDiagram, fixPalette);
                            } else {
                                createCharm({
                                    id: "charm-saving-error",
                                    type: "ERROR",
                                    title: "Diagram not created nor modified!"
                                });
                            }
                        }
                    }
                ).fail((jqXHR, textStatus, errorThrown) => alert(errorThrown))
            }
        }
    ).fail((response, textStatus, errorThrown) => {
        createCharm({
            id: "charm-saving-error",
            type: "ERROR",
            title: "Diagram not created nor modified!"
        });
        if (response.status === 409) {
            let id = value_.id;
            if (id === undefined) {
                $.ajax({
                    url: basePath + endpoint + "?name=" + value_.name + "&kind=" + value_.kind,
                    type: 'GET',
                    async: false,
                    crossDomain: true,
                    contentType: "application/json",
                    converters: {
                        "text json": function (response) {
                            return (response === "") ? null : JSON.parse(response);
                        }
                    }
                }).done((data, textStatus, response) => {
                    if (response.status === 200) {
                        if (_.isArray(data) && !_.isEmpty(data)) {
                            id = data[0].id;
                            update(id, endpoint, value_, desktopDiagram);
                        } else {
                            alert("Invalid operation");
                        }
                    }
                });
            } else {
                delete value_.id;
                update(id, endpoint, value_, desktopDiagram);
            }
        } else {
            alert(errorThrown);
        }
    });
}

function update(id, endpoint, value_, desktopDiagram) {

    $.ajax({
        url: basePath + endpoint + "/" + id,
        data: JSON.stringify(value_),
        type: 'PUT',
        crossDomain: true,
        contentType: "application/json",
        converters: {
            "text json": function (response) {
                return (response === "") ? null : JSON.parse(response);
            }
        }
    }).done((data, textStatus, response) => {
            if (response.status === 200) {
                retrievePalette(desktopDiagram, fixPalette);
            } else {
                if (response.status === 201) {
                    retrievePalette(desktopDiagram, fixPalette);
                } else {
                    alert("Not created!");
                }
            }
        }
    ).fail((response, textStatus, errorThrown) => alert(errorThrown))
}

function load(model, diagram) {
    let modelToSaveOrLoad = $("#modelToSaveOrLoad");
    let jsonString = modelToSaveOrLoad.children()[0].innerText;
    if (model === undefined) {
        expand(jsonString, diagram);
    } else {
        expand(model, diagram);
    }
}

function addImageToNode(elementType, data, desktopDiagram) {
    const diagram = desktopDiagram.diagram;
    diagram.startTransaction("Deleting new element");
    diagram.model.setDataProperty(data, "image", desktopDiagram.meta.image);
    diagram.requestUpdate();
    diagram.commitTransaction("Deleting new element");
}

function placeNewNode(elementType, data, name, desktopDiagram, deleteData) {
    if (_.isObject(elementType)) {
        placeNode(elementType, data, name, desktopDiagram, deleteData);
    } else {
        $.ajax({
            url: basePath + "/api/catalogs/element-types/" + elementType + "/shape",
            type: 'GET',
            crossDomain: true,
            contentType: "application/json",
            converters: {
                "text json": function (response) {
                    return (response === "") ? null : JSON.parse(response);
                }
            }
        }).done((shapeText, textStatus, response) => {
                if (response.status === 200) {
                    let shape = JSON.parse(shapeText);
                    placeNode(shape, data, name, desktopDiagram, deleteData);
                }
            }
        ).fail((jqXHR, textStatus, errorThrown) => alert(errorThrown));
    }
}

function placeNode(elementType, data, name, desktopDiagram, deleteData) {
    const diagram = desktopDiagram.diagram;
    diagram.startTransaction("Deleting new element");
    if (deleteData !== undefined) {
        const node = diagram.findNodeForKey(deleteData.key);
        if (node !== null) {
            diagram.remove(node);
        }
        diagram.model.removeNodeData(deleteData);
        diagram.requestUpdate();
        diagram.commitTransaction("Deleting new element");
        diagram.startTransaction("Adding new element");
        data.category = elementType.type;
        diagram.model.addNodeData(data);
    } else {
        diagram.model.removeNodeData(data);
        diagram.requestUpdate();
        diagram.commitTransaction("Deleting new element");
        diagram.startTransaction("Adding new element");
        delete data["text"];
        delete data["__gohashid"];
        data.kind = elementType.type;
        data.name = name;
        data.image = desktopDiagram.meta.image;
        data.shape = elementType;
        const colorScheme = OpenArchiWrapper.getPrimaryColorScheme(elementType);
        data.fill = colorScheme.fillColor;
        data.category = elementType.type;
        data.isGroup = OpenArchiWrapper.toIsGroup(elementType, null, elementType.group);
        diagram.model.addNodeData(data);
    }
    diagram.requestUpdate();
    diagram.commitTransaction("Adding new element");
    delete desktopDiagram.meta.image;
    relayoutLanes(diagram);
    fixMeta(desktopDiagram)
}

function checkAndSave(suffix, desktopDiagram) {
    const diagram = desktopDiagram.diagram;
    let basicElementData = $('#dialog' + suffix);
    if (basicElementData === undefined) {
        basicElementData = $('#basic-element-data' + suffix);
    }
    const key = desktopDiagram.key;
    let data = diagram.model.findNodeDataForKey(key);
    let elementType;
    if (Metro.dialog.isDialog(basicElementData)) {
        const elementTypeSelected = $("#element-types" + suffix);
        const val = elementTypeSelected.val();
        elementType = JSON.parse(val);
        Metro.dialog.remove(basicElementData);
    } else {
        elementType = desktopDiagram.diagramType;
    }
    if (data !== null) {
        const name = $("#element-name" + suffix).val();
        $.ajax({
            url: basePath + "/api/models?name=" + name + "&kind=" + elementType.type,
            type: 'GET',
            crossDomain: true,
            contentType: "application/json",
            converters: {
                "text json": function (response) {
                    return (response === "") ? null : JSON.parse(response);
                }
            }
        }).done((model, textStatus, response) => {
                if (response.status === 200 && !_.isEmpty(model)) {
                    placeNewNode(elementType, model.shift(), name, desktopDiagram, data);
                } else {
                    placeNewNode(elementType.type, data, name, desktopDiagram);
                }
            }
        ).fail((jqXHR, textStatus, errorThrown) => alert(errorThrown));
    }
}

function persists(diagram, nodeDataArray, linkDataArray) {
    diagram.startTransaction("Expand element");
    diagram.model.addNodeDataCollection(nodeDataArray);
    if (linkDataArray !== undefined && linkDataArray !== null) {
        diagram.model.addLinkDataCollection(linkDataArray);
    }
    const pos = diagram.model.modelData.position;
    if (pos) {
        diagram.initialPosition = go.Point.parse(pos);
    }
    diagram.requestUpdate();
    diagram.commitTransaction("Expand element");
    //Scale according to expended element size
    diagram.startTransaction("Scaling diagram");
    const documentBounds = diagram.documentBounds;
    const viewportBounds = diagram.viewportBounds;
    let scale = 1;
    let scaleWidth = 1;
    let scaleHeight = 1;
    const paletteWidthSize = 124;
    const menuHeightSize = 67;
    const padding = 30;
    const width = documentBounds.width;
    const height = documentBounds.height;
    const width2 = viewportBounds.width - paletteWidthSize - padding;
    const height2 = viewportBounds.height - menuHeightSize - padding;
    if (width > width2) {
        scaleWidth = width2 / width;
    }
    if (height > height2) {
        scaleHeight = height2 / height;
    }

    if (scaleWidth < 1 || scaleHeight < 1) {
        if (scaleWidth < scaleHeight) {
            scale = scaleWidth;
        } else {
            if (scaleWidth > scaleHeight) {
                scale = scaleHeight;
            }
        }
    }

    if (scale < 1) {
        diagram.scale = scale;
        diagram.requestUpdate();
    }
    diagram.commitTransaction("Scaling diagram");
    relayoutLanes(diagram);
}

function closeDialog(id) {
    const element = $("#" + id);
    Metro.dialog.close(element);
}

function expandTree(node) {
    return node.childNodes.length < 3;
}

function process(model, diagram, diagramType) {
    const newDiagram = OpenArchiToDiagram.process(model);
    const nodeDataArray = newDiagram.nodeDataArray;
    const linkDataArray = newDiagram.linkDataArray;
    if (nodeDataArray !== undefined && nodeDataArray !== null) {
        let diff = [];
        diagram.model.nodeDataArray.forEach(node => {
            const found = nodeDataArray.filter(node_ => {
                return node.name === node_.name && node.kind === node_.kind && node.id !== node_.id;
            });
            if (found !== undefined && !_.isEmpty(found)) {
                found.forEach(found_ =>
                    diff.push({"already-existent-element": found_, "new-element": node}));
            }
        });
        if (!_.isEmpty(diff)) {
            let resolveConflicts;
            const generatedId = Commons.prototype.random();
            const id = "dialog-" + generatedId;
            const content = $("<div>", {
                "data-role": "wizard",
                "data-start": 1,
                "data-button-mode": "button",
                "data-button-outline": false,
                "data-cls-wizard": "custom-wizard",
                "data-icon-help": "<span>Close</span>"
            });
            const jsonObjects = [];
            diff.forEach((diff_, i) => {
                const alreadyExistentElement = Commons.prototype.clean(diff_["already-existent-element"]);
                const newElement = Commons.prototype.clean(diff_["new-element"]);

                const $div = $("<div>", {
                    class: "page-content"
                }).append($("<h6>")
                    .append("Difference #" + (i + 1)))
                    .append($("<div>").append(
                        $("<div class=\"grid\">\n" +
                            "    <div class=\"row\">\n" +
                            "        <div class=\"cell cellWidth\">" +
                            "            <input id=\"existent-checkbox-" + i + "\"" +
                            "                   type=\"checkbox\" \n" +
                            "                   data-role=\"checkbox\"\n" +
                            "                   data-style=\"2\"\n" +
                            "                   data-cls-caption=\"fg-cyan text-bold\"\n" +
                            "                   data-cls-check=\"bd-cyan\">" +
                            "        </div>\n" +
                            "        <div class=\"cell cellWidth\">" +
                            "            <input id=\"new-checkbox-" + i + "\"" +
                            "                   type=\"checkbox\" \n" +
                            "                   data-role=\"checkbox\"\n" +
                            "                   data-style=\"2\"\n" +
                            "                   data-cls-caption=\"fg-cyan text-bold\"\n" +
                            "                   data-cls-check=\"bd-cyan\">" +
                            "        </div>\n" +
                            "    </div>\n" +
                            "    <div class=\"row p2\">\n" +
                            "        <div class=\"cell cellWidth\"><h7>Existent</h7></div>\n" +
                            "        <div class=\"cell cellWidth\"><h7>New</h7></div>\n" +
                            "    </div>\n" +
                            "    <div class=\"row\" id=\"difference-diagram-" + i + "\"></div>\n" +
                            "    <div class=\"row\" id=\"details-" + i + "\"></div>\n" +
                            "</div>")
                    ));

                const id2 = "already-existent-element-wrapper-" + i;
                $("<div>", {id: id2, class: "cell cellWidth"}).appendTo("body");
                const id3 = "new-element-wrapper-" + i;
                $("<div>", {id: id3, class: "cell cellWidth"}).appendTo("body");

                const alreadyExistentElementWrapper = document.getElementById(id2);
                const newElementWrapper = document.getElementById(id3);

                const alreadyExistentElementTree = jsonTree.create(alreadyExistentElement, alreadyExistentElementWrapper);
                const newElementTree = jsonTree.create(newElement, newElementWrapper);

                $("<section>").append($div).appendTo(content);

                jsonObjects.push({
                    id: i,
                    existentTree: alreadyExistentElementTree,
                    newTree: newElementTree
                });
            });
            resolveConflicts = Metro.dialog.create({
                title: "Resolve conflicts",
                overlay: true,
                defaultAction: false,
                show: true,
                removeOnClose: true,
                content: content,
                clsDialog: "custom-dialog",
                onClose: function (el) {
                    persists(diagram, nodeDataArray, linkDataArray);
                }
            });
            jsonObjects.forEach(jsonObject => {
                const id = jsonObject.id;

                const details = $("#" + "details-" + id);
                const existentJsonWrapper = $("<div>", {class: "cell cellWidth cellScroll"}).get()[0];
                jsonObject.existentTree.appendTo(existentJsonWrapper);
                details.append(existentJsonWrapper);
                const newJsonWrapper = $("<div>", {class: "cell cellWidth cellScroll"}).get()[0];
                jsonObject.newTree.appendTo(newJsonWrapper);
                details.append(newJsonWrapper);

                const diagrams = $("#" + "difference-diagram-" + id);
                const id2 = "existent-diagram-wrapper-" + id;
                const existentDiagramWrapper = $("<div>", {id: id2, class: "cell cellWidth cellScroll"});
                const existentDiagram = new DesktopDiagram({id: id2, diagramType: diagramType, isPrototyper: true});
                diagrams.append(existentDiagramWrapper);
                const id3 = "new-diagram-wrapper-" + id;
                const newDiagramWrapper = $("<div>", {id: id3, class: "cell cellWidth cellScroll"});
                const newDiagram = new DesktopDiagram({id: id3, diagramType: diagramType, isPrototyper: true});
                diagrams.append(newDiagramWrapper);
            });
            resolveConflicts.attr("id", id);
            content.attr("data-on-help-click", "closeDialog('" + id + "');");
            content.attr("data-on-finish-click", "closeDialog('" + id + "');");
        } else {
            persists(diagram, nodeDataArray, linkDataArray);
        }
    }
}

function expand(data, diagram) {
    let model;
    if (!Commons.prototype.isObject(data)) {
        model = JSON.parse(data);
    } else {
        model = data;
    }
    if (model.shape === undefined || model.shape.colorSchemes === undefined) {
        $.ajax({
            url: basePath + "/api/catalogs/element-types/" + model.kind + "/shape",
            type: 'GET',
            crossDomain: true,
            contentType: "application/json",
            converters: {
                "text json": function (response) {
                    return (response === "") ? null : JSON.parse(response);
                }
            }
        }).done((shapeText, textStatus, response) => {
                if (response.status === 200) {
                    model.shape = JSON.parse(shapeText);
                    model.shape.id = Commons.prototype.random();
                    model.shape.colorSchemes.forEach(function (colorScheme) {
                        colorScheme.id = Commons.prototype.random();
                    });
                    process(model, diagram, data.kind);
                }
            }
        ).fail((jqXHR, textStatus, errorThrown) => alert(errorThrown));
    } else {
        process(model, diagram, data.kind);
    }
}

function fixMeta(desktopDiagram) {
    if (desktopDiagram === undefined) {
        return;
    }
    const diagram = desktopDiagram.diagram;
    if (diagram !== undefined) {
        const rootNodes = diagram.findTreeRoots();
        if (rootNodes !== undefined) {
            let count = 0;
            let rootNodeData = {};
            rootNodes.each(function (node) {
                count++;
                rootNodeData = node;
            });
            if (count === 1) {
                const data = rootNodeData.data;
                if (data !== undefined && (desktopDiagram.meta.name === "New Element" || desktopDiagram.meta.name === undefined)) {
                    desktopDiagram.meta.name = data.name;
                    //$("#diagram-name").val(data.name);
                }
                desktopDiagram.meta.kind = data.kind;
                desktopDiagram.meta.id = data.id;
            } else {
                desktopDiagram.meta.id = undefined;
                desktopDiagram.meta.name = undefined;
                //$("#diagram-name").val("");
            }
        }
    }
}

function expandGroups(g, i, level) {
    if (!(g instanceof go.Group)) return;
    g.isSubGraphExpanded = i < level;
    g.memberParts.each(function (m) {
        expandGroups(m, i + 1, level);
    })
}

function openExample(url, targetDiv) {
    $("#dataModelDraggable").show();
    getPageContent(url, targetDiv);
}

function openContent(url, targetDiv) {
    getPageContent(url, targetDiv);
}

function getPageContent(url, targetDiv) {
    $.ajax({
        url: url,
        crossDomain: true,
        beforeSend: function (xhr) {
            xhr.overrideMimeType("text/html; charset=utf-8");
        }
    }).done(function (data) {
        let target;
        if (targetDiv === undefined) {
            target = $("#myInfo");
        } else {
            target = $("#" + targetDiv);
        }
        target.html(data);
        target.show();
    }).fail(function (data) {
        alert(data);
    });
}


function returnPageContent(url, callback) {
    $.ajax({
        url: basePath + "/samples/" + url,
        crossDomain: true,
        beforeSend: function (xhr) {
            xhr.overrideMimeType("text/html; charset=utf-8");
        }
    }).done(function (data) {
        callback(data);
    }).fail(function (data) {
        alert(data);
    });
}

function getJsonContent(url, callback) {
    $.ajax({
        url: url,
        beforeSend: function (xhr) {
            xhr.overrideMimeType("application/json; charset=utf-8");
        }
    }).done(function (data) {
        let jsonData = JSON.stringify(data);
        let modelToSaveOrLoad = $("#modelToSaveOrLoad");
        modelToSaveOrLoad.empty();
        modelToSaveOrLoad.jsonView(jsonData);
        if (callback !== undefined && (typeof callback === "function")) {
            callback(data)
        }
    });
}

function appendSVG(diagram) {
    return diagram.makeSvg({
        scale: 1.2,
        background: "transparent"
    });
}

function appendImage(diagram) {
    return diagram.makeImage({
        scale: 1.2,
        background: "transparent"
    });
}

function addNodeToTemplateByType(data, type, diagram) {
    const templateNode = getNodeByType(data);
    let isGroup = templateNode.isGroup !== undefined ? templateNode.isGroup : data.isGroup;
    let type_ = type;
    if (type_ === undefined) {
        type_ = data.category !== undefined ? data.category : data.kind
    }
    if (isGroup) {
        diagram.groupTemplateMap.add(type_, templateNode);
    } else {
        diagram.nodeTemplateMap.add(type_, templateNode);
    }
}

function validateModel(desktopDiagram) {
    const diagram = desktopDiagram.diagram;
    const endpoint = "/api/models/validate";
    const value = OpenArchiFromDiagram.process(desktopDiagram);
    $.ajax({
        url: basePath + endpoint,
        data: JSON.stringify(value),
        type: 'POST',
        crossDomain: true,
        contentType: "application/json",
        converters: {
            "text json": function (response) {
                return (response === "") ? null : JSON.parse(response);
            }
        }
    }).done((data, textStatus, response) => {
            if (response.status === 202) {
                createCharm({
                    id: "charm-saving-success",
                    type: "SUCCESS",
                    title: "Diagram created or modifed successfully!"
                });
                if (data) {
                    showValidationReport(data);
                }
            } else {
                createCharm({
                    id: "charm-saving-error",
                    type: "ERROR",
                    title: "Diagram not created nor modified!"
                });
            }
        }
    ).fail((response, textStatus, errorThrown) => {
        createCharm({
            id: "charm-saving-error",
            type: "ERROR",
            title: "Diagram not created nor modified!"
        });
    });
}

function confirmAndSave(diagram) {
    const model = diagram.model;
    if (model !== undefined) {
        const nodeDataArray = model.nodeDataArray;
        if (nodeDataArray !== undefined) {
            if (nodeDataArray.length > 1) {
                //$('#diagram-info').modal('show');
            } else {
                save(undefined, diagram);
            }
        }
    }
}

function getElementType() {
    let elementTypesDropdown = $("#elementTypesDropdown");
    const elementType = elementTypesDropdown.closest('.dropdown').find('.dropdown-toggle');
    if (elementType) {
        let type = elementType.html();
        if (type) {
            let elementType_ = {};
            elementType_.type = type.split(" ")[0].trim();
            let isGroup = false;
            let selectedLi = elementTypesDropdown.children("li:contains('" + elementType_.type + "')");
            elementType_.group = selectedLi.attr("data-isgroup");
            return elementType_;
        }
    }
    return null;
}


function handleNewImage(evt, desktopDiagram, node) {
    const diagram = desktopDiagram.diagram;
    const key = desktopDiagram.key;
    let data = diagram.model.findNodeDataForKey(key);
    const divId = diagram.div.id;
    const splittedIds = divId.split("-");
    let suffix = splittedIds.length > 1 ? "-" + splittedIds[1] : divId;
    const elementType = node.shape;
    const content = "<div>\n" +
        "            <form data-role='validator' action='javascript:' novalidate='novalidate' data-role-validator='true' name='basic-element-form'>\n" +
        "                <input id='image" + suffix + "' class=mt-2 type='file' data-role='file' data-prepend='Image' data-button-title='<span class=mif-folder></span>' accept='image/svg+xml'/>\n" +
        "            </form>\n" +
        "         </div>";
    const dialog = Metro.dialog.create({
        title: "Add Image",
        modal: true,
        show: true,
        content: content,
        actions: [
            {
                caption: "Continue",
                cls: "primary js-dialog-close",
                onclick: function (e) {
                    addImageToNode(elementType.type, data, desktopDiagram);
                }
            },
            {
                caption: "Dismiss",
                cls: "js-dialog-close",
                onclick: function (e) {
                    diagram.model.removeNodeData(data);
                }
            }
        ]
    });
    $("#image" + suffix).on("change", function (e) {
        handleImageSelect(e, desktopDiagram);
    });
}

function handleImageSelect(evt, desktopDiagram) {
    let files = evt.target.files;
    let i = 0, file;
    const diagram = desktopDiagram.diagram;
    for (; file = files[i]; i++) {
        const type = file.type;
        // Only process SVG image files.
        if (type !== 'image/svg+xml') {
            continue;
        }

        const reader = new FileReader();

        // Closure to capture the file information.
        reader.onload = (function (file) {
            return function (e) {
                let rawImage_ = e.target.result;
                let rawImage = rawImage_.replace(/^data:image\/svg\+xml;base64,/, "");
                rawImage = window.atob(rawImage);

                let $element = $('#element-image-data');
                const elementKey = $element.attr("key");

                if (elementKey !== undefined) {
                    diagram.model.nodeDataArray.forEach(node => {
                        if (node.key.toString() === elementKey) {
                            diagram.model.setDataProperty(node, "image", {raw: rawImage_, type: type});
                        }
                    });
                    diagram.requestUpdate();

                } else {
                    desktopDiagram.meta.image = {raw: rawImage_, type: type};
                }
            };
        })(file);
        // Read in the image file as a data URL.
        reader.readAsDataURL(file);
    }
}

function findValues(obj, key) {
    return findValuesHelper(obj, key, []);
}

function findValuesHelper(obj, key, list) {
    let i;
    if (!obj) return list;
    if (obj instanceof Array) {
        for (i in obj) {
            list = list.concat(findValuesHelper(obj[i], key, []));
        }
        return list;
    }
    if (obj[key]) list.push(obj);

    if ((typeof obj === "object") && (obj !== null)) {
        let children = Object.keys(obj);
        if (children.length > 0) {
            for (i = 0; i < children.length; i++) {
                list = list.concat(findValuesHelper(obj[children[i]], key, []));
            }
        }
    }
    return list;
}

function copyTextToClipboard(text) {
    const textArea = document.createElement("textArea");

    // Place in top-left corner of screen regardless of scroll position.
    textArea.style.position = 'fixed';
    textArea.style.top = 0;
    textArea.style.left = 0;

    // Ensure it has a small width and height. Setting to 1px / 1em
    // doesn't work as this gives a negative w/h on some browsers.
    textArea.style.width = '2em';
    textArea.style.height = '2em';

    // We don't need padding, reducing the size if it does flash render.
    textArea.style.padding = 0;

    // Clean up any borders.
    textArea.style.border = 'none';
    textArea.style.outline = 'none';
    textArea.style.boxShadow = 'none';

    // Avoid flash of white box if rendered for any reason.
    textArea.style.background = 'transparent';


    textArea.value = text;

    document.body.appendChild(textArea);
    textArea.focus();
    textArea.select();

    try {
        const successful = document.execCommand('copy');
        if (successful) {
            const msg = successful ? 'successful' : 'unsuccessful';
            console.log('Copying text command was ' + msg);
            alert('"' + msg + '" was copied to clipboard')
        }
    } catch (err) {
        console.log('Oops, unable to copy');
    }
    document.body.removeChild(textArea);
}

function downloadJson(diagramID) {
    const diagram = getDiagramById(diagramID);
    const json = OpenArchiFromDiagram.process(diagram);
    const type = "application/json";
    const file = new Blob([JSON.stringify(json, null, '\t')], {type: type});
    let filename = diagram.meta.name || "model";
    filename = filename + ".json";
    if (window.navigator.msSaveOrOpenBlob) // IE10+
        window.navigator.msSaveOrOpenBlob(file, filename);
    else { // Others
        const a = document.createElement("a"),
            url = URL.createObjectURL(file);
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        setTimeout(function () {
            document.body.removeChild(a);
            window.URL.revokeObjectURL(url);
        }, 0);
    }
}

function timeoutDemo(){
    Metro.dialog.create({
        title: "Timeout demo",
        content: "<div>This dialog will be closed after 3 sec</div>",
        autoHide: 3000
    });
}

function customsDemo(){
    Metro.dialog.create({
        title: "Customs demo",
        content: "<div>This dialog with customs classes</div>",
        clsDialog: "bg-dark",
        clsTitle: "fg-yellow",
        clsContent: "fg-white",
        clsDefaultAction: "alert"
    });
}

function animateDemo(){
    Metro.dialog.create({
        title: "Animation demo",
        content: "<div>This dialog animated with onShow, onHide events</div>",
        onShow: function(el){
            el.addClass("ani-swoopInTop");
            setTimeout(function(){
                el.removeClass("ani-swoopInTop");
            }, 500);
        },
        onHide: function(el){
            el.addClass("ani-swoopOutTop");
            setTimeout(function(){
                el.removeClass("ani-swoopOutTop");
            }, 500);
        }
    });
}

function actionsDemo(){
    Metro.dialog.create({
        title: "Dialog actions",
        content: "<div>This dialog with custom actions</div>",
        actions: [
            {
                caption: "Yes, i'am",
                cls: "js-dialog-close alert",
                onclick: function(){
                    alert("You choose YES");
                }
            },
            {
                caption: "No, thanks",
                cls: "js-dialog-close",
                onclick: function(){
                    alert("You choose NO");
                }
            }
        ]
    });
}