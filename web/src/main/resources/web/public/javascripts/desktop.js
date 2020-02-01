const Desktop = {
    options: {
        windowArea: ".window-area",
        windowAreaClass: "",
        taskBar: ".task-bar > .tasks",
        taskBarClass: ""
    },

    wins: {},

    setup: function (options) {
        this.options = $.extend({}, this.options, options);
        return this;
    },

    addToTaskBar: function (wnd) {
        const icon = wnd.getIcon();
        const wID = wnd.win.attr("id");
        const type = wnd.win.attr("type");
        const item = $("<span>").addClass("task-bar-item started").html(icon);

        item.data("wID", wID);
        if (type !== undefined) {
            item.data("type", type);
        }
        item.click(function () {
            const el = $("#" + wID);
            let topZ = 0;
            $(".window.resizable").each(function () {
                const thisZ = parseInt($(this).css("z-index"), 10);
                if (thisZ > topZ) {
                    topZ = thisZ;
                }
            });
            el.css('z-index', topZ + 1);
        });
        item.appendTo($(this.options.taskBar));
    },

    removeFromTaskBar: function (wnd) {
        const wID = wnd.attr("id");
        const items = $(".task-bar-item");
        const that = this;
        $.each(items, function () {
            const item = $(this);
            if (item.data("wID") === wID) {
                delete that.wins[wID];
                item.remove();
            }
        })
    },

    createWindow: function (o) {
        let win;
        const that = this;
        const type = o.type;
        const items = $(".task-bar-item");
        let alreadyCreated = false;
        if (type !== undefined) {
            $.each(items, function () {
                const item = $(this);
                if (item.data("type") === type) {
                    item.focus();
                    alreadyCreated = true;
                }
            });
            if (alreadyCreated) {
                return;
            }
        }
        o.onDragStart = function (pos, el) {
            win = $(el);
            $(".window").css("z-index", 1);
            if (!win.hasClass("modal"))
                win.css("z-index", 3);
        };
        o.onDragStop = function (pos, el) {
            win = $(el);
            if (!win.hasClass("modal"))
                win.css("z-index", 2);
        };
        o.onWindowDestroy = function (win) {
            that.removeFromTaskBar(win);
        };
        const div = o.suffix ? $("<div>", {id: "window-" + o.suffix}) : $("<div>");
        const w = div.appendTo($(this.options.windowArea));
        const wnd = w.window(o).data("window");

        win = wnd.win;
        const shift = Metro.utils.objectLength(this.wins) * 16;

        if (wnd.options.place === "auto" && wnd.options.top === "auto" && wnd.options.left === "auto") {
            win.css({
                top: shift,
                left: shift
            });
        }
        const id = win.attr("id");
        if (type !== undefined) {
            win.attr("type", type);
        }
        this.wins[id] = wnd;
        this.addToTaskBar(wnd);
        w.remove();
        return id;
    },

    close: function (id) {
        const win = this.wins[id];
        if (win !== undefined) {
            win.close();
        }
    },

    get: function (id) {
        return this.wins[id];
    }
};

Desktop.setup();

// noinspection JSUnusedGlobalSymbols
function createApiWindow() {
    const content = "<div><div id='swagger-ui'></div>";
    const contentClass = "content-class-api";
    $("<style type='text/css'>\n ." + contentClass + " { \n" +
        "    overflow-y: scroll !important;\n" +

        "    height: 100% !important;\n" +
        "  }\n" +
        " </style>").appendTo("head");

    Desktop.createWindow({
        width: "100%",
        height: "auto",
        clsWindow: "windowClass",
        clsCaption: "windowCaptionClass",
        clsContent: contentClass,
        type: "api",
        icon: "<img src=\"" + basePath + "/images/open-archi-api-logo.png\" style=\"display: inherit;\" class=\"img-responsive\">",
        title: "OpenArchi Api",
        content: "<div class='p-2'>" + content + "</div>"
    });
    window.ui = SwaggerUIBundle({
        url: "/open-archi-apis.yaml",
        logo: "/images/open-archi.png",
        dom_id: '#swagger-ui',
        deepLinking: true,
        presets: [
            SwaggerUIBundle.presets.apis,
            SwaggerUIStandalonePreset
        ],
        plugins: [
            SwaggerUIBundle.plugins.DownloadUrl
        ],
        layout: "StandaloneLayout",
        requestInterceptor: function (req) {
            req.headers["Access-Control-Allow-Origin"] = "*";
            req.headers["Access-Control-Request-Method"] = "*";
            req.headers["Access-Control-Allow-Headers"] = "*";
            return req;
        },
        responseInterceptor: function (res) {
            return res;
        }
    });
}

// noinspection JSUnusedGlobalSymbols
function createSampleWindow(resource, title) {
    returnPageContent(resource + ".html", function (content) {
        const generatedId = Commons.prototype.random();
        content = Commons.prototype.replaceAll(content, "$$${divId}$$$", generatedId);
        let generatedId2 = undefined;
        if (content.indexOf("$$${divId2}$$$") !== -1) {
            generatedId2 = Commons.prototype.random();
            content = Commons.prototype.replaceAll(content, "$$${divId2}$$$", generatedId2);
        }
        content = $(content);
        const captionClass = "caption-class-" + generatedId;
        const fillColor = "#C33C97";
        const captionClassPrimary = fillColor;
        const captionClassText = "white";
        $("<style type='text/css'>\n  ." + captionClass + " { \n" +
            "    background-color: " + captionClassPrimary + " !important; \n" +
            "    color: " + captionClassText + " !important; \n" +
            "  }\n" +
            " </style>").appendTo("head");
        const contentClass = "content-class-" + generatedId;
        const contentClassPrimary = fillColor;
        $("<style type='text/css'>\n ." + contentClass + " { \n" +
            "    overflow: hidden !important;\n" +
            "    border-bottom: solid 4px " + contentClassPrimary + " !important; \n" +
            "    border-left: solid 4px " + contentClassPrimary + " !important; \n" +
            "    border-right: solid 4px " + contentClassPrimary + " !important; \n" +
            "    max-height: 100% !important; \n" +
            "  }\n" +
            " </style>").appendTo("head");

        Desktop.createWindow({
            id: "window-" + generatedId,
            width: "100%",
            height: "100%",
            clsWindow: "window-common",
            clsCaption: captionClass,
            clsContent: contentClass,
            icon: "<img src=\"" + basePath + "/images/open-archi-samples-logo.png\" style=\"display: inherit;\" class=\"img-responsive\">",
            title: "OpenArchi Sample - " + title,
            content: content
        });
        const desktopDiagram = new DesktopDiagram({id: generatedId, diagramType: {type: "VIEW"}, isPrototyper: false});
        diagrams[generatedId] = desktopDiagram;
        if (generatedId2 !== undefined) {
            const desktopDiagram2 = new DesktopDiagram({id: generatedId2, diagramType: {type: "VIEW"}, isPrototyper: false});
            diagrams[generatedId2] = desktopDiagram2;
        }
    });
}

function createSamplesWindow() {
    const content = $("<table class='table'></table>");
    content.append("<tbody>" +
                   "    <tr>"+
                   "        <td><button class='button' style='background-color: #4AACCC; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('checkBoxes', 'Features (checkbox)')\"> Features (checkbox)</button></td>"+
                   "        <td><button class='button' style='background-color: #4AB6D9; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('flowchart', 'Workflow')\"> Workflow</button></td>"+
                   "        <td><button class='button' style='background-color: #139DD9; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('gantt', 'Gantt Diagram')\"> Gantt Diagram</button></td>"+
                   "        <td><button class='button' style='background-color: #198BCA; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('sequenceDiagram', 'UML - Sequence')\"> UML - Sequence</button></td>"+
                   "    </tr>\n" +
                   "    <tr>"+
                   "        <td><button class='button' style='background-color: #5BCAEE; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('columnResizing', 'Adjusting sizes')\"> Adjusting sizes</button></td>"+
                   "        <td><button class='button' style='background-color: #A5E8FD; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('grouping', 'Expander')\"> Expander</button></td>"+
                   "        <td><button class='button' style='background-color: #4EB8E5; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('regrouping', 'Re-grouping')\"> Re-grouping</button></td>"+
                   "        <td><button class='button' style='background-color: #9CD6EF; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('shopFloorMonitor', 'Flow and Sequence 2')\"> Flow and Sequence 2</button></td>"+
                   "    </tr>\n" +
                   "    <tr>"+
                   "        <td><button class='button' style='background-color: #2AA9E1; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('comments', 'Comments')\"> Comments</button></td>"+
                   "        <td><button class='button' style='background-color: #198BCF; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('guidedDragging', 'Visual Guides')\"> Visual Guides</button></td>"+
                   "        <td><button class='button' style='background-color: #0D6394; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('icons', 'SVG Icons')\"> SVG Icons</button></td>"+
                   "        <td><button class='button' style='background-color: #0D4972; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('swimBands', 'Release Planning')\"> Release Planning</button></td>"+
                   "    </tr>\n" +
                   "    <tr>"+
                   "        <td><button class='button' style='background-color: #0B79B7; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('dragCreating', 'Fast origination')\"> Fast origination</button></td>"+
                   "        <td><button class='button' style='background-color: #198BCA; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('kanban', 'Kanban Board')\"> Kanban Board</button></td>"+
                   "        <td><button class='button' style='background-color: #139DD9; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('mindMap', 'Strategic Maps')\"> Strategic Maps</button></td>"+
                   "        <td><button class='button' style='background-color: #8F236C; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('swimLanes', 'Process Diagram')\"> Process Diagram</button></td>"+
                   "    </tr>\n" +
                   "    <tr>"+
                   "        <td><button class='button' style='background-color: #AD2481; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('draggableLink', 'Constraints')\"> Constraints</button></td>"+
                   "        <td><button class='button' style='background-color: #D574AF; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('logicCircuit', 'Flow and Sequence 1')\"> Flow and Sequence 1</button></td>"+
                   "        <td><button class='button' style='background-color: #4EB8E5; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('navigation', 'Following the Flow')\"> Following the Flow</button></td>"+
                   "        <td><button class='button' style='background-color: #4AACCC; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('umlClass', 'UML - Classes')\"> UML - Classes</button></td>"+
                   "    </tr>\n" +
                   "    <tr>"+
                   "        <td><button class='button' style='background-color: #198BCF; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('entityRelationship', 'Entity Relationship')\"> Entity Relationship</button></td>"+
                   "        <td><button class='button' style='background-color: #7b246e; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('orgChartStatic', 'Zooming')\"> Zooming</button></td>"+
                   "        <td><button class='button' style='background-color: #139DD9; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('records', 'Features Mapping')\"> Features Mapping</button></td>"+
                   "        <td><button class='button' style='background-color: #0D6394; color: white; width: -webkit-fill-available;' onclick=\"createSampleWindow('updateDemo', 'Realtime Update')\"> Realtime Update</button></td>"+
                   "    </tr>\n" +
                    "</tbody>");
    Metro.dialog.create({
        title: "Examples",
        content: content,
        overlayClickClose: true,
        removeOnClose: true,
        defaultAction: false,
        width: "auto",
        clsTitle: "big-text text-center"
    });
}

function associateSVGToNewWindow(generatedId) {
    const desktopDiagram = getDiagramById(generatedId);
    const diagram = desktopDiagram.diagram;
    const svg = appendSVG(diagram);
    createWindowModal(svg, "SVG Diagram", 750);
}

function associateImageToNewWindow(generatedId) {
    const desktopDiagram = getDiagramById(generatedId);
    const diagram = desktopDiagram.diagram;
    const image = appendImage(diagram);
    createWindowModal(image, "PNG Diagram", 750);
}

// noinspection JSUnusedGlobalSymbols
function createWindowModal(content, title, width) {
    let width_;
    if (width === undefined) {
        width_ = 300;
    } else {
        width_ = width;
    }
    return Desktop.createWindow({
        width: width_,
        icon: "<span class='mif-cogs'></span>",
        title: title,
        content: $("<div class='p-2'></div>").append(content),
        overlay: true,
        overlayColor: "transparent",
        modal: true,
        place: "center",
        onShow: function (win) {
            win.addClass("ani-swoopInTop");
            setTimeout(function () {
                win.removeClass("ani-swoopInTop");
            }, 1000);
        },
        onClose: function (win) {
            win.addClass("ani-swoopOutTop");
        }
    });
}

function processForDownload(blob, name, extension) {
    let name_ = name;
    if (name_ === undefined) {
        name_ = "model"
    }
    let extension_ = extension;
    if (extension_ === undefined) {
        extension_ = "dat"
    }
    const url = window.URL.createObjectURL(blob);
    const filename = name_ + "." + extension_;

    let a = document.createElement("a");
    a.style = "display: none";
    a.href = url;
    a.download = filename;

    // IE 11
    if (window.navigator.msSaveBlob !== undefined) {
        window.navigator.msSaveBlob(blob, filename);
        return;
    }

    document.body.appendChild(a);
    requestAnimationFrame(function () {
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);
    });
}

function downloadSVG(diagramID) {
    const desktopDiagram = getDiagramById(diagramID);
    const diagram = desktopDiagram.diagram;
    const svg = diagram.makeSvg({scale: 1, background: "transparent"});
    const svgstr = new XMLSerializer().serializeToString(svg);
    const extension = "svg";
    const blob = new Blob([svgstr], {type: "image/" + extension + "+xml"});
    const name = desktopDiagram.meta.name;
    processForDownload(blob, name, extension);
}

function downloadImage(diagramID) {
    const desktopDiagram = getDiagramById(diagramID);
    const diagram = desktopDiagram.diagram;
    const image = diagram.makeImage({scale: 1, background: "transparent"});
    const imagestr = new XMLSerializer().serializeToString(image);
    const extension = "png";
    const blob = new Blob([imagestr], {type: "image/" + extension});
    const name = desktopDiagram.meta.name;
    processForDownload(blob, name, extension);
}

// noinspection JSUnusedGlobalSymbols
function createDataPropertiesInspector(diagramID) {
    const desktopDiagram = getDiagramById(diagramID);
    const divid = "dataInspector_" + diagramID;
    Desktop.createWindow({
        width: 300,
        icon: "<span class='mif-cogs'></span>",
        title: "Data Inspector",
        content: "<div id='" + divid + "'></div>",
        place: "right"
    });

    var inspector = new Inspector(divid, desktopDiagram.diagram,
        {
            // allows for multiple nodes to be inspected at once
            multipleSelection: true,
            // max number of node properties will be shown when multiple selection is true
            showSize: 4,
            // when multipleSelection is true, when showAllProperties is true it takes the union of properties
            // otherwise it takes the intersection of properties
            showAllProperties: true,
            // uncomment this line to only inspect the named properties below instead of all properties on each object:
            // includesOwnProperties: false,
            properties: {
                "text": {},
                // key would be automatically added for nodes, but we want to declare it read-only also:
                "key": {readOnly: true, show: Inspector.showIfPresent},
                // color would be automatically added for nodes, but we want to declare it a color also:
                "color": {show: Inspector.showIfPresent, type: 'color'},
                // Comments and LinkComments are not in any node or link data (yet), so we add them here:
                "Comments": {show: Inspector.showIfNode},
                "LinkComments": {show: Inspector.showIfLink},
                "isGroup": {readOnly: true, show: Inspector.showIfPresent},
                "flag": {show: Inspector.showIfNode, type: 'checkbox'},
                "state": {
                    show: Inspector.showIfNode,
                    type: "select",
                    choices: function (node, propName) {
                        if (Array.isArray(node.data.choices)) return node.data.choices;
                        return ["one", "two", "three", "four", "five"];
                    }
                },
                "choices": {show: false},  // must not be shown at all
                // an example of specifying the <input> type
                "password": {show: Inspector.showIfPresent, type: 'password'}
            }
        });
    window.dataInspector = inspector;
}

// noinspection JSUnusedGlobalSymbols
function createDebugPropertiesInspector(diagramID) {
    const desktopDiagram = getDiagramById(diagramID);
    const divid = "debugInspector_" + diagramID;
    Desktop.createWindow({
        width: 300,
        icon: "<span class='mif-cogs'></span>",
        title: "Debug Inspector",
        content: "<div id='" + divid + "'></div>",
        place: "right"
    });

    var inspector = new DebugInspector(divid, desktopDiagram.diagram,
        {
            acceptButton: true,
            resetButton: true,
            /*
            // example predicate, only show data objects:
            inspectPredicate: function(value) {
              return !(value instanceof go.GraphObject)
            }
            */
        });
    window.debugInspector = inspector;
}

// noinspection JSUnusedGlobalSymbols
function createWindowYoutube() {
    Desktop.createWindow({
        width: 500,
        icon: "<span class='mif-youtube'></span>",
        title: "Youtube video",
        content: "https://youtu.be/S9MeTn1i72g",
        clsContent: "bg-dark"
    });
}

function createPrototyperWindowForModel(model) {
    return createPrototyperWindow(model.diagramType, model);
}

function createPrototyperWindow(diagramType, model) {
    const generatedId = Commons.prototype.random();
    let captionClass = "";
    let contentClass = "";
    if (diagramType !== undefined && !_.isEmpty(diagramType) && diagramType.type !== 'DEFAULT') {
        const colorScheme = OpenArchiWrapper.getPrimaryColorScheme(diagramType);
        if (colorScheme !== undefined) {
            captionClass = "caption-class-" + generatedId;
            const captionClassPrimary = colorScheme.fillColor;
            const captionClassText = colorScheme.textColor;
            $("<style type='text/css'>\n  ." + captionClass + " { \n" +
                "    background-color: " + captionClassPrimary + " !important; \n" +
                "    color: " + captionClassText + " !important; \n" +
                "  }\n" +
                " </style>").appendTo("head");
            contentClass = "content-class-" + generatedId;
            const contentClassPrimary = colorScheme.fillColor;
            $("<style type='text/css'>\n ." + contentClass + " { \n" +
                "    overflow: hidden !important;\n" +
                "    border-bottom: solid 2px " + contentClassPrimary + " !important; \n" +
                "    border-left: solid 2px " + contentClassPrimary + " !important; \n" +
                "    border-right: solid 2px " + contentClassPrimary + " !important; \n" +
                "    max-height: 100% !important; \n" +
                "  }\n" +
                " </style>").appendTo("head");
            let elementTypesOptions = "";
            const elementTypes = diagramType.elementShapes;
            if (elementTypes !== undefined && !_.isEmpty(elementTypes)) {
                elementTypes.forEach(elementType => {
                    elementTypesOptions = elementTypesOptions + "<option value='" + JSON.stringify(elementType) + "'>" + elementType.type + "</option>\n";
                });
            }
        }
        let content =
            //Menu
            "    <nav data-role='ribbonmenu' style='z-index: 4; padding-left: 4px; padding-top: 4px; padding-right: 4px;'>\n" +
            "        <ul class='tabs-holder'>\n" +
            "            <li class='active'><a href='#section_main'>Menu</a></li>\n" +
            "            <li><a href='#section_tab1'>Search</a></li>\n" +
            "            <li><a href='#section_tab2'>Info</a></li>\n" +
            "            <li><a href='#section_tab3'>Hightlighting</a></li>\n" +
            "            <li><a href='#section_tab4'>Relatives</a></li>\n" +
            "            <li><a href='#section_tab5'>Import</a></li>\n" +
            "            <li><a href='#section_tab6'>Export</a></li>\n" +
            "        </ul>\n" +
            "        <div class='content-holder' style='border-left: 1px solid #dadbdc; border-right: 1px solid #dadbdc;'>\n" +
            "            <div class='section' id='section_main'>\n" +
            "                <div class='group' style='padding: 0'>\n" +
            "                    <button class='ribbon-icon-button'>\n" +
            "                        <span class='icon'><img src='" + basePath + "/images/share.svg'></span>\n" +
            "                        <span class='caption'>Share</span>\n" +
            "                    </button>\n" +
            "                    <button class='ribbon-icon-button'>\n" +
            "                        <span class='icon'><img src='" + basePath + "/images/gmail.svg'></span>\n" +
            "                        <span class='caption'>Email</span>\n" +
            "                    </button>\n" +
            "                    <div>\n" +
            "                        <button class='ribbon-icon-button'  onclick='save(getDiagramById(\"" + generatedId + "\"))'>\n" +
            "                            <span class='icon'><span class='mif-cloud-upload'></span></span>\n" +
            "                            <span class='caption'>Save</span>\n" +
            "                        </button>\n" +
            "                    </div>\n" +
            "                    <div>\n" +
            "                        <button class='ribbon-icon-button'  onclick='validateModel(getDiagramById(\"" + generatedId + "\"))'>\n" +
            "                            <span class='icon'><span class='mif-checkmark'></span></span>\n" +
            "                            <span class='caption'>Validate</span>\n" +
            "                        </button>\n" +
            "                    </div>\n" +
            "                </div>\n" +
            "            </div>\n" +
            "            <div class='section' id='section_tab2'>\n" +
            "                <div class='group' style='padding: 0'>\n" +
            "                    <button class='ribbon-icon-button' onclick='createDataPropertiesInspector(\"" + generatedId + "\")'>\n" +
            "                        <span class='icon'><img src='" + basePath + "/images/properties.svg'></span>\n" +
            "                        <span class='caption'>Properties</span>\n" +
            "                    </button>\n" +
            "                </div>\n" +
            "                <div class='group' style='padding: 0'>\n" +
            "                    <button class='ribbon-icon-button' onclick='createDebugPropertiesInspector(\"" + generatedId + "\")'>\n" +
            "                        <span class='icon'><img src='" + basePath + "/images/debug.svg'></span>\n" +
            "                        <span class='caption'>Debug</span>\n" +
            "                    </button>\n" +
            "                </div>\n" +
            "            </div>\n" +
            "            <div class='section' id='section_tab4'>\n" +
            "                <div class='group' style='padding: 0'>\n" +
            "                    <button class='ribbon-icon-button' onclick='loadChildren(\"" + generatedId + "\")'>\n" +
            "                        <span class='icon'><img src='" + basePath + "/images/children-nodes.svg'></span>\n" +
            "                        <span class='caption'>Children</span>\n" +
            "                    </button>\n" +
            "                    <button class='ribbon-icon-button' onclick='loadParents(\"" + generatedId + "\")'>\n" +
            "                        <span class='icon'><img src='" + basePath + "/images/parent-node.svg'></span>\n" +
            "                        <span class='caption'>Parents</span>\n" +
            "                    </button>\n" +
            "                </div>\n" +
            "            </div>\n" +
            "            <div class='section' id='section_tab5'>\n" +
            "                <div class='group' style='padding: 0'>\n" +
            "                    <button class='ribbon-icon-button' onclick='showJsonDialogForImport(\"" + generatedId + "\")'>\n" +
            "                        <span class='icon'><img src='" + basePath + "/images/json.svg'></span>\n" +
            "                        <span class='caption'>From JSON</span>\n" +
            "                    </button>\n" +
            "                    <input type='file' id='upload-file-" + generatedId + "' accept='application/json' style='display: none;' />\n" +
            "                    <button id='choose-upload-button-" + generatedId + "' class='ribbon-icon-button'>\n" +
            "                        <span class='mif-upload'></span>\n" +
            "                        <span class='caption'>From File</span>\n" +
            "                    </button>\n" +
            "                </div>\n" +
            "            </div>\n" +
            "            <div class='section' id='section_tab6'>\n" +
            "                <div class='group' style='padding: 0'>\n" +
            "                    <button class='ribbon-icon-button' onclick='showJsonDialogForExport(\"" + generatedId + "\")'>\n" +
            "                        <span class='icon'><img src='" + basePath + "/images/json.svg'></span>\n" +
            "                        <span class='caption'>To JSON</span>\n" +
            "                    </button>\n" +
            "                </div>\n" +
            "                <div class='group' style='padding: 0'>\n" +
            "                    <button class='ribbon-icon-button' onclick='downloadJson(\"" + generatedId + "\")'>\n" +
            "                        <span class='mif-download'></span>\n" +
            "                        <span class='caption'>Download JSON</span>\n" +
            "                    </button>\n" +
            "                </div>\n" +
            "                <div class='group' style='padding: 0'>\n" +
            "                    <button class='ribbon-icon-button' onclick='associateSVGToNewWindow(\"" + generatedId + "\")'>\n" +
            "                        <span class='icon'><img src='" + basePath + "/images/svg-logo.jpg'></span>\n" +
            "                        <span class='caption'>Get SVG</span>\n" +
            "                    </button>\n" +
            "                </div>\n" +
            "                <div class='group' style='padding: 0'>\n" +
            "                    <button class='ribbon-icon-button' onclick='downloadSVG(\"" + generatedId + "\")'>\n" +
            "                        <span class='mif-file-download'></span>\n" +
            "                        <span class='caption'>Export SVG</span>\n" +
            "                    </button>\n" +
            "                </div>\n" +
            "                <div class='group' style='padding: 0'>\n" +
            "                    <button class='ribbon-icon-button' onclick='associateImageToNewWindow(\"" + generatedId + "\")'>\n" +
            "                        <span class='icon'><img src='" + basePath + "/images/png-logo.png'></span>\n" +
            "                        <span class='caption'>Get PNG</span>\n" +
            "                    </button>\n" +
            "                </div>\n" +
            "                <div class='group' style='padding: 0'>\n" +
            "                    <button class='ribbon-icon-button' onclick='downloadImage(\"" + generatedId + "\")'>\n" +
            "                        <span class='mif-file-download'></span>\n" +
            "                        <span class='caption'>Export PNG</span>\n" +
            "                    </button>\n" +
            "                </div>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "    </nav>\n"
            +
            //Content
            "    <div class='grid' style='height: 100%; padding: 0'>\n" +
            "        <div class='row' style='max-height: calc(100% - 65px); height: 100%;'>\n" +
            "            <div class='stub' style='width: 128px; height: 100%; padding: 0'>\n" +
            "                <div class='active' id='sidebar" + "-" + generatedId + "' style='position: initial;'>\n" +
            "                  <div style='margin-left: 0; height: 100%'>\n" +
            "                      <div>\n" +
            "                        <div id='paletteDivBasic" + "-" + generatedId + "'></div>\n" +
            "                      </div>\n" +
            "                      <div>\n" +
            "                        <div id='paletteDivGeneral" + "-" + generatedId + "' style='height: 300px'></div>\n" +
            "                      </div>\n" +
            "                  </div>\n" +
            "                </div>\n" +
            "            </div>\n" +
            "            <div class='cell' style='height: 100%;'>\n" +
            "                <div id='content" + "-" + generatedId + "' style='padding: 0'>\n" +
            "                    <div id='diagramDiv-" + generatedId + "'></div>\n" +
            "                </div>" +
            "            </div>\n" +
            "        </div>\n" +
            "    </div>\n";
        let text = "";
        if (diagramType !== undefined && !_.isEmpty(diagramType) && diagramType.type !== 'DEFAULT') {
            text = " | [" + diagramType.type + "]";
        }
        Desktop.createWindow({
            id: "window-" + generatedId,
            width: "100%",
            height: "100%",
            clsWindow: "window-common",
            clsCaption: captionClass,
            clsContent: contentClass,
            icon: "<img src=\"" + basePath + "/images/open-archi-prototyper-logo.png\" style=\"display: inherit;\" class=\"img-responsive\">",
            title: "OpenArchi Prototyper" + text,
            content: content
        });
        if (model !== undefined) {
            const model_ = OpenArchiToDiagram.process(model);
            new DesktopDiagram({
                id: generatedId,
                diagramType: diagramType,
                isPrototyper: true,
                nodeDataArray: model_.nodeDataArray,
                linkDataArray: model_.linkDataArray
            });
        } else {
            new DesktopDiagram({id: generatedId, diagramType: diagramType, isPrototyper: true});
        }
        $(function () {
            const uploadFile = $("#upload-file-" + generatedId);
            const chooseUploadButton = $("#choose-upload-button-" + generatedId);
            chooseUploadButton.click(function () {
                uploadFile.click();
            });
            uploadFile.change(function () {
                const file = this.files[0];
                const reader = new FileReader();
                reader.onload = (function () {
                    return function (e) {
                        const diagram = getDiagramById(generatedId);
                        const json = e.target.result;
                        expand(json, diagram.diagram);
                    };
                })(file);
                reader.readAsText(file);
            });
        });
        return generatedId;
    } else {
        return;
    }
}

function chooseModelType() {
    $.ajax({
        url: basePath + "/api/catalogs/diagram-types",
        type: 'GET',
        crossDomain: true,
        contentType: "application/json",
        converters: {
            "text json": function (response) {
                return (response === "") ? null : JSON.parse(response);
            }
        }
    }).done((diagramTypes, textStatus, response) => {
        if (response.status === 200) {
            let diagramTypesOptions = "";
            if (diagramTypes !== undefined && !_.isEmpty(diagramTypes)) {
                diagramTypes.forEach(diagramType => {
                    diagramTypesOptions = diagramTypesOptions + "<option value='" + JSON.stringify(diagramType) + "'>" + diagramType.type + "</option>\n";
                });
            }
            Metro.dialog.create({
                title: "Model Type",
                modal: true,
                show: true,
                content:
                    "<div>" +
                    "  <select id='diagram-types'\n" +
                    "          data-role='select' " +
                    "          class='mt-2'>\n" +
                    diagramTypesOptions +
                    "  </select>\n" +
                    "</div>",
                actions: [
                    {
                        caption: "Continue",
                        cls: "primary js-dialog-close",
                        onclick: function (el) {
                            const diagramType = $("#diagram-types");
                            const diagramType2 = diagramType.val();
                            const diagramTypeObj = JSON.parse(diagramType2);
                            createPrototyperWindow(diagramTypeObj);
                            Metro.dialog.remove(el);
                        }
                    },
                    {
                        caption: "Dismiss",
                        cls: "js-dialog-close",
                        onclick: function (el) {
                            Metro.dialog.remove(el);
                        }
                    }
                ]
            });
        }
    });
}

function showJsonDialogForImport(diagramID) {
    const diagram = getDiagramById(diagramID);
    let editor;
    let winId_ = Desktop.createWindow({
        width: 300,
        icon: "<img src=\"" + basePath + "/images/json.svg\">",
        title: "Json importer",
        content: "<div class='p-2' id='jsoneditor-" + diagramID + "'></div><button id='jsoneditor-button-" + diagramID + "' class='button'>Load</button>",
        overlay: true,
        overlayColor: "#000000",
        clsWindow: "window-json",
        clsCaption: "window-caption-common",
        clsContent: "window-json-content",
        modal: true,
        place: "center",
        onShow: function (win) {
            /*            win.addClass("ani-swoopInTop");
                        setTimeout(function () {
                            win.removeClass("ani-swoopInTop");
                        }, 1000);*/
            // set json
            $.ajax({
                url: basePath + "/api/catalogs/json-schema?modelType=" + diagram.diagramType.type + "_Create",
                type: 'GET',
                crossDomain: true,
                contentType: "application/json",
                converters: {
                    "text json": function (response) {
                        return (response === "") ? null : JSON.parse(response);
                    }
                }
            }).done((jsonSchema, textStatus, response) => {
                if (response.status === 200) {
                    if (jsonSchema !== undefined && !_.isEmpty(jsonSchema)) {
                        let cleanedJsonSchema = JSON.stringify(jsonSchema);
                        cleanedJsonSchema = cleanedJsonSchema.replace(/"#\/definitions\/(\w*)"/gim, function (match, p1) {
                            return "\"" + p1.toLowerCase() + "\"";
                        });
                        cleanedJsonSchema = JSON.parse(cleanedJsonSchema);
                        const json = {};
                        const schemaRefs = cleanedJsonSchema.definitions;
                        delete cleanedJsonSchema.definitions;
                        editor.setSchema(cleanedJsonSchema, schemaRefs);
                        editor.set(json);
                        document.getElementById('jsoneditor-button-' + diagramID).onclick = function (el) {
                            try {
                                expand(editor.get(), diagram.diagram);
                                Desktop.close(winId_);
                                /*win_.next(".overlay").remove();
                                win_.remove();*/
                            } catch (err) {
                                Metro.dialog.create({
                                    title: "Error",
                                    modal: true,
                                    show: true,
                                    content:
                                        "<div>Invalid json object</div>",
                                    actions: [
                                        {
                                            caption: "Dismiss",
                                            cls: "js-dialog-close",
                                            onclick: function (el) {
                                                Metro.dialog.remove(el);
                                            }
                                        }
                                    ]
                                });
                            }
                        };

                    }
                } else {
                    Metro.dialog.create({
                        title: "Error",
                        modal: true,
                        show: true,
                        content:
                            "<div>" + jsonSchema + "</div>",
                        actions: [
                            {
                                caption: "Dismiss",
                                cls: "js-dialog-close",
                                onclick: function (el) {
                                    Desktop.close(winId_);
                                    /*win_.next(".overlay").remove();
                                    win_.remove();*/
                                }
                            }
                        ]
                    });
                }
            });
        },
        /*        onClose: function (win) {
                    win.addClass("ani-swoopOutTop");
                },*/
        onWindowCreate: function (win) {
            // create the editor
            const container = document.getElementById('jsoneditor-' + diagramID);
            let options = {
                mode: 'code',
                modes: ['code', 'form', 'text', 'tree', 'view'] // allowed modes
            };
            editor = new JSONEditor(container, options);
        }
    });
}

function showJsonDialogForExport(diagramID) {
    const diagram = getDiagramById(diagramID);
    Desktop.createWindow({
        width: 300,
        icon: "<img src=\"" + basePath + "/images/json.svg\">",
        title: "Json exporter",
        content: "<div class='p-2' id='jsoneditor-" + diagramID + "'></div>",
        overlay: true,
        overlayColor: "#000000",
        clsWindow: "window-json",
        clsCaption: "window-caption-common",
        clsContent: "window-json-content",
        modal: true,
        place: "center",
        /*        onShow: function (win) {
                    win.addClass("ani-swoopInTop");
                    setTimeout(function () {
                        win.removeClass("ani-swoopInTop");
                    }, 1000);
                },
                onClose: function (win) {
                    win.addClass("ani-swoopOutTop");
                },*/
        onWindowCreate: function (win) {
            // create the editor
            const container = document.getElementById('jsoneditor-' + diagramID);
            let options = {
                mode: 'tree',
                modes: ['code', 'form', 'text', 'tree', 'view'] // allowed modes
            };
            let editor = new JSONEditor(container, options);
            const json = OpenArchiFromDiagram.process(diagram);
            // set json
            editor.set(json);
        }
    });
}

function showValidationReport(messages) {

    const content = $("<div></div>");
    if (messages !== undefined && _.isArray(messages) && messages.length > 0) {

        const table = $("" +
            "<table class='table'>" +
            "  <thead>\n" +
            "    <tr>\n" +
            "       <th>Rule Name</th>\n" +
            "       <th>Comment</th>\n" +
            "       <th>Expected Value</th>\n" +
            "       <th>Value</th>\n" +
            "    </tr>\n" +
            "  </thead>" +
            "</table>");
        const tbody = $("<tbody></tbody>");

        messages.forEach(message => {

            let clazz;
            switch (message.type) {
                case "SUCCESS":
                    clazz = "success";
                    break;
                case "WARNING":
                    clazz = "alert";
                    break;
                case "DEBUG":
                    clazz = "";
                    break;
                case "INFO":
                    clazz = "info";
                    break;
                case "ERROR":
                    clazz = "alert";
                    break;
                default:
                    clazz = "";
                    break;
            }
            tbody.append("<tr>" +
                "<td class='" + clazz + "'" + ">" + message.ruleName + "</td>" +
                "<td class='" + clazz + "'" + ">" + message.comment + "</td>" +
                "<td class='" + clazz + "'" + ">" + message.expectedValue + "</td>" +
                "<td class='" + clazz + "'" + ">" + message.object + "</td>" +
                "</tr>");
        });
        table.append(tbody);
        content.append(table);
        Desktop.createWindow({
            width: 300,
            icon: "<span class='icon'><span class='mif-checkmark'></span></span>",
            title: "Validation Report",
            content: content,
            overlay: true,
            overlayColor: "#000000",
            clsWindow: "window-json",
            clsCaption: "window-caption-common",
            clsContent: "window-json-content",
            modal: true,
            place: "center"
        });
        createCharm({
            id: "charm-validation-success",
            type: "ERROR",
            title: "Model validated with errors!"
        });
    } else {
        createCharm({
            id: "charm-validation-success",
            type: "SUCCESS",
            title: "Model validated with no issues!"
        });
    }
}

function createCharm(options) {

    if (options === undefined) {
        return;
    }

    if (options.id === undefined || options.type === undefined || options.title === undefined ) {
        return;
    }

    if (options.type.match(/^((?!(SUCCESS|INFO|WARNING|ERROR)).)*$/g)) {
        throw new Error({name: "TypeError", message: "Invalid charm type!. Valid ones are: SUCCESS, WARNING, INFO, ERROR"});
    }

    if (options.opacity === undefined) {
        options.opacity = 0.98;
    }

    if (options.fadeIn === undefined) {
        options.fadeIn = 1000;
    }

    if (options.delay === undefined) {
        options.delay = 1800;
    }

    if (options.fadeOut === undefined) {
        options.fadeOut = 3000;
    }

    if (options.position === undefined) {
        options.position = "top"
    }

    let charm = $("<div></div>");
    charm.attr("id", options.id);
    charm.data("role", "charms");
    charm.data("role-charms", "true");
    charm.data("opacity", options.opacity);
    charm.data("position", options.position);
    charm.css("min-height", "75px");
    charm.css("border-radius", "10px");
    charm.css("border-width", "7px");
    charm.css("border-style", "solid");
    charm.addClass("charms");
    charm.addClass("open");
    charm.addClass("d-flex");
    charm.addClass("flex-justify-center");
    charm.addClass("p-2");

    if (options.type.toUpperCase() === "SUCCESS") {
        charm.css("background-color","#DFF2BF");
        charm.css("border-color", "green");
        let spanCheckmark = $("<span></span>");
        spanCheckmark.addClass("mif-checkmark");
        spanCheckmark.addClass("mif-2x");
        spanCheckmark.css("color", "green");
        spanCheckmark.addClass("flex-self-center");
        spanCheckmark.addClass("flex-self-stretch");
        charm.append(spanCheckmark);
        let spanTitle = $("<span></span>");
        spanTitle.css("font-size", "x-large");
        spanTitle.css("color", "green");
        spanTitle.html("&nbsp;" + options.title);
        charm.append(spanTitle);

        if (options.message) {
            let spanMessage = $("<span></span>");
            spanMessage.addClass("p-4");
            spanMessage.css("color", "green");
            spanMessage.html("&nbsp;" + options.message);
            spanTitle.append(spanMessage);
            options.delay =+ 5000;
            options.fadeOut =+ 1000;
        }
    } else if (options.type.toUpperCase() === "INFO") {
        charm.css("background-color","#BBEEFF");
        charm.css("border-color", "blue");
        let spanBlocked = $("<span></span>");
        spanBlocked.addClass("mif-info");
        spanBlocked.addClass("mif-2x");
        spanBlocked.css("color", "blue");
        spanBlocked.addClass("flex-self-center");
        spanBlocked.addClass("flex-self-stretch");
        charm.append(spanBlocked);
        let spanTitle = $("<span></span>");
        spanTitle.css("font-size", "x-large");
        spanTitle.css("color", "blue");
        spanTitle.html("&nbsp;" + options.title);
        charm.append(spanTitle);

        if (options.message) {
            let spanMessage = $("<span></span>");
            spanMessage.addClass("p-4");
            spanMessage.css("color", "blue");
            spanMessage.html("&nbsp;" + options.message);
            spanTitle.append(spanMessage);
            options.delay =+ 5000;
            options.fadeOut =+ 1000;
        }
    } else if (options.type.toUpperCase() === "WARNING") {
        charm.css("background-color","#FEEFB3");
        charm.css("border-color", "orange");
        let spanBlocked = $("<span></span>");
        spanBlocked.addClass("mif-warning");
        spanBlocked.addClass("mif-2x");
        spanBlocked.css("color", "orange");
        spanBlocked.addClass("flex-self-center");
        spanBlocked.addClass("flex-self-stretch");
        charm.append(spanBlocked);
        let spanTitle = $("<span></span>");
        spanTitle.css("font-size", "x-large");
        spanTitle.css("color", "orange");
        spanTitle.html("&nbsp;" + options.title);
        charm.append(spanTitle);

        if (options.message) {
            let spanMessage = $("<span></span>");
            spanMessage.addClass("p-4");
            spanMessage.css("color", "orange");
            spanMessage.html("&nbsp;" + options.message);
            spanTitle.append(spanMessage);
            options.delay =+ 5000;
            options.fadeOut =+ 1000;
        }
    } else if (options.type.toUpperCase() === "ERROR") {
        charm.css("background-color","#FFBABA");
        charm.css("border-color", "red");
        let spanBlocked = $("<span></span>");
        spanBlocked.addClass("mif-blocked");
        spanBlocked.addClass("mif-2x");
        spanBlocked.css("color", "red");
        spanBlocked.addClass("flex-self-center");
        spanBlocked.addClass("flex-self-stretch");
        charm.append(spanBlocked);
        let spanTitle = $("<span></span>");
        spanTitle.css("font-size", "x-large");
        spanTitle.css("color", "red");
        spanTitle.html("&nbsp;" + options.title);
        charm.append(spanTitle);

        if (options.message) {
            let spanMessage = $("<span></span>");
            spanMessage.addClass("p-4");
            spanMessage.css("color", "red");
            spanMessage.html("&nbsp;" + options.message);
            spanTitle.append(spanMessage);
            options.delay =+ 5000;
            options.fadeOut =+ 1000;
        }
    }
    charm.prependTo("body")
        .fadeIn(options.fadeIn)
        .delay(options.delay)
        .fadeOut(options.fadeOut, function(){
                charm.remove();
            }
        );
    return charm;
}

$(function () {
    let dataArray;
    $("#search-button").autocomplete({
        minLength: 2,
        source: function (request, response) {
            $.get(basePath + "/api/models", {
                query: "name=='*" + request.term + "*'",
                fieldsToInclude: "id,name,kind"
            }).done(function (data) {
                const models = [];
                dataArray = data;
                if (Array.isArray(data)) {
                    if (data.length === 0) {
                        createCharm({
                            id: "charm-finding-success",
                            type: "INFO",
                            title: "Diagram found and loaded successfuly"
                        });
                    }
                    data.forEach(function (model) {
                        models.push({id: model.id, value: model.name});
                    });
                    response(models);
                } else {
                    response({});
                    createCharm({
                        id: "charm-finding-warning",
                        type: "WARNING",
                        title: "Diagram not found!"
                    });
                }
            }).fail((jqXHR, textStatus, errorThrown) => {
                createCharm({
                    id: "charm-finding-warning",
                    type: "WARNING",
                    title: "Diagram not found!"
                });
            });
        },
        select: function (event, element) {
            const id = element.item.id;
            const model = dataArray.find(function (model) {
                return model.id === id;
            });
            $.get(basePath + "/api/models/" + model.id)
                .done((data, textStatus, response) => {
                        if (response.status === 200) {
                            const windowId = createPrototyperWindowForModel(data);
                            $("#search-button").val('');
                            if (windowId !== undefined) {
                                createCharm({
                                    id: "charm-finding-success",
                                    type: "INFO",
                                    title: "Diagram found and loaded successfuly"
                                });
                            } else {
                                createCharm({
                                    id: "charm-finding-warning",
                                    type: "WARNING",
                                    title: "Diagram found but with inconsistencies!.",
                                    message: "Diagram with name '" + data.name + "' and id '" + data.id + "' cannot be shown"
                                });
                            }
                        } else {
                            createCharm({
                                id: "charm-finding-warning",
                                type: "WARNING",
                                title: "Diagram not found!"
                            });
                        }
                    }
                ).fail((jqXHR, textStatus, errorThrown) => {
                    createCharm({
                        id: "charm-finding-warning",
                        type: "WARNING",
                        title: "Diagram not found!"
                    });
                });
        },
        // optional (if other layers overlap autocomplete list)
        open: function (event, ui) {
            $(".ui-autocomplete").css("z-index", 1000);
        }
    });
});