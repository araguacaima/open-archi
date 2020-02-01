let alreadyProcessedNodes;
let alreadyProcessedLinks;

function fulfill(item, isGroup, group, rank) {
    if (item.id !== undefined) {
        item.key = item.id;
    }
    if (item.key === undefined) {
        item.key = Commons.prototype.random();
    }
    item.isGroup = isGroup;
    if (group !== undefined) {
        item.group = group;
    }
    if (rank !== undefined) {
        item.rank = rank;
    }
    return item;
}

function extractLinks(model) {
    let links = [];
    if (model.relationships !== undefined && model.relationships !== null) {
        model.relationships.forEach(relationship => {
            links.push({
                to: relationship.destination.id,
                from: relationship.source.id
            });
        });
    }
    return links;
}

function removeDuplicates(arr) {
    let unique_array = [];
    for (let i = 0; i < arr.length; i++) {
        const element = arr[i];
        if (unique_array.length === 0) {
            unique_array.push(element)
        } else {
            let flag = false;
            for (let j = 0; j < unique_array.length; j++) {
                const uniqueArrayElement = unique_array[j];
                if (_.isEqual(uniqueArrayElement, element)) {
                    flag = true;
                }
            }
            if (!flag) {
                unique_array.push(element);
            }
        }
    }
    return unique_array;
}

function findParent(parentId, model) {
    if (parentId !== undefined && model !== undefined) {
        let nodes = findValues(model, "key");
        return nodes.find(node => {
            return node.key.toString() === parentId.toString();
        })
    }
    return undefined;
}

function findByField(model, field, value) {
    if (value !== undefined && model !== undefined) {
        let nodes = findValues(model, field);
        return nodes.find(node => {
            return node[field] === value;
        })
    }
    return undefined;
}

class OpenArchiWrapper {


    static toFillPrimary(data, node) {
        let colorScheme;
        if (_.isObject(data)) {
            colorScheme = OpenArchiWrapper.getPrimaryColorScheme(data);
        } else {
            colorScheme = OpenArchiWrapper.getPrimaryColorScheme(node);
        }
        return colorScheme.fillColor;
    }

    static fromFillPrimary(obj, data, model) {
        let colorScheme = OpenArchiWrapper.getPrimaryColorScheme(data);
        const fill = colorScheme.fillColor;
        model.setDataProperty(data, "fill", fill ? fill : data.fill);
    }

    static toFillSecondary(data, node) {
        let colorScheme;
        if (_.isObject(data)) {
            colorScheme = OpenArchiWrapper.getSecondaryColorScheme(data);
        } else {
            colorScheme = OpenArchiWrapper.getSecondaryColorScheme(node);
        }
        return colorScheme.fillColor;
    }

    static fromFillSecondary(obj, data, model) {
        let colorScheme = OpenArchiWrapper.getSecondaryColorScheme(data);
        const fill = colorScheme.fillColor;
        model.setDataProperty(data, "fill", fill ? fill : data.fill);
    }

    static toStrokePrimary(data, node) {
        let colorScheme = OpenArchiWrapper.getPrimaryColorScheme(data);
        const stroke = colorScheme.strokeColor;
        const brush = new go.Brush(go.Brush.Solid);
        brush.color = stroke ? stroke : data.stroke;
        return brush;
    }

    static fromStrokePrimary(obj, data, model) {
        let colorScheme = OpenArchiWrapper.getPrimaryColorScheme(data);
        const stroke = colorScheme.strokeColor;
        model.setDataProperty(data, "stroke", stroke ? stroke : data.stroke);
    }

    static toStrokeSecondary(data, node) {
        let colorScheme = OpenArchiWrapper.getSecondaryColorScheme(data);
        const stroke = colorScheme.strokeColor;
        const brush = new go.Brush(go.Brush.Solid);
        brush.color = stroke ? stroke : data.stroke;
        return brush;
    }

    static fromStrokeSecondary(obj, data, model) {
        let colorScheme = OpenArchiWrapper.getSecondaryColorScheme(data);
        const stroke = colorScheme.strokeColor;
        model.setDataProperty(data, "stroke", stroke ? stroke : data.stroke);
    }

    static toTextSecondary(data, node) {
        let colorScheme = OpenArchiWrapper.getSecondaryColorScheme(data);
        const stroke = colorScheme.textColor;
        const brush = new go.Brush(go.Brush.Solid);
        brush.color = stroke ? stroke : data.stroke;
        return brush;
    }

    static fromTextSecondary(obj, data, model) {
        let colorScheme = OpenArchiWrapper.getSecondaryColorScheme(data);
        const stroke = colorScheme.textColor;
        model.setDataProperty(data, "stroke", stroke ? stroke : data.stroke);
    }

    static toTextPrimary(data, node) {
        let colorScheme = OpenArchiWrapper.getPrimaryColorScheme(data);
        const stroke = colorScheme.textColor;
        const brush = new go.Brush(go.Brush.Solid);
        brush.color = stroke ? stroke : data.stroke;
        return brush;
    }

    static fromTextPrimary(obj, data, model) {
        let colorScheme = OpenArchiWrapper.getPrimaryColorScheme(data);
        const stroke = colorScheme.textColor;
        model.setDataProperty(data, "stroke", stroke ? stroke : data.stroke);
    }

    static toTitle(data, node) {
        return data.name;
    }

    static fromTitle(obj, data, model) {
        model.setDataProperty(data, "text", obj);
        model.setDataProperty(data, "name", obj);
    }

    static toImage(data, node) {
        return data.image ? data.image.raw : "";
    }

    static fromImage(obj, data, model) {
        model.setDataProperty(data, "source", data.image.raw);
    }

    static toName(data, node) {
        return data.name;
    }

    static fromName(obj, data, model) {
        model.setDataProperty(data, "text", data.name);
        model.setDataProperty(data, "name", data.name);
    }

    /*    static toSize(data, node) {
            let size;
            const shape = data.shape;
            const size2 = shape.size;
            if (shape && size2) {
                const width = size2.width;
                const height = size2.height;
                size = new go.Size(width === 0 ? 25 : width, height === 0 ? 15 : height);
            } else {
                size = new go.Size(25, 15);
            }
            return size;
        }

        static fromSize(size, data, model) {
            model.setDataProperty(data, "width", size.width);
            model.setDataProperty(data, "height", size.height);
        }*/

    static toFigure(data, node) {
        return data.shape.figure;
    }

    static fromFigure(figure, data, model) {
        model.setDataProperty(data, "figure", data.shape.figure);
    }

    static toRank(data, node) {
        return data.rank;
    }

    static fromRank(figure, data, model) {
        let rank = {};

        model.setDataProperty(data, "rank", rank);
    }

    static toFromLinkable(data, node) {
        const shape = data.shape;
        if (shape) {
            return shape.output;
        }
        return true;
    }

    static fromFromLinkable(loc, data, model) {
        let fromLinkable = true;
        const shape = data.shape;
        if (shape) {
            fromLinkable = shape.output;
        }
        model.setDataProperty(data, "fromLinkable", fromLinkable);
    }


    static toToLinkable(data, node) {
        const shape = data.shape;
        if (shape) {
            return shape.input;
        }
        return true;
    }

    static fromToLinkable(loc, data, model) {
        let toLinkable = true;
        const shape = data.shape;
        if (shape) {
            toLinkable = shape.input;
        }
        model.setDataProperty(data, "toLinkable", toLinkable);
    }

    static toCategory(data, node) {
        const shape = data.shape;
        if (shape) {
            return shape.type;
        }
        return "DEFAULT";
    }

    static fromCategory(loc, data, model) {
        let category = "DEFAULT";
        const shape = data.shape;
        if (shape) {
            category = shape.type;
        }
        model.setDataProperty(data, "category", category);
    }

    static toIsGroup(data, node, isGroup) {
        if (data === undefined || data === null) {
            return false;
        }
        const shape = data.shape;
        if (shape !== undefined) {
            if (isGroup !== undefined) {
                return isGroup;
            } else {
                return shape.isGroup || shape.group;
            }
        } else {
            return data.isGroup || data.group;
        }
    }

    static toFeature(data, node) {
        return data.features;
    }

    static fromFeature(feature, data, model) {
        let features = [];
        model.setDataProperty(data, "features", features);
    }

    static fixCategory(elements) {
        if (elements !== undefined) {
            elements.forEach(function (element) {
                element.category = OpenArchiWrapper.toCategory(element);
            });
            return elements.sort(function (a, b) {
                return a.rank - b.rank;
            });
        }
    }

    static getPrimaryColorScheme(data) {
        let colorScheme = {};
        if (data !== undefined && data !== null) {
            let colorSchemes = [];
            if (data.colorSchemes !== undefined) {
                colorSchemes = data.colorSchemes;
            } else {
                let shape = undefined;
                if (data.shape === undefined || data.shape.colorSchemes === undefined) {
                    let elementType;
                    if (data.shape) {
                        elementType = data.shape.type;
                    } else {
                        elementType = data.type;
                    }
                    $.ajax({
                        url: basePath + "/api/catalogs/element-types/" + elementType + "/shape",
                        type: 'GET',
                        crossDomain: true,
                        contentType: "application/json",
                        converters: {
                            "text json": function (response) {
                                return (response === "") ? null : JSON.parse(response);
                            }
                        },
                        async: false
                    }).done((shapeText, textStatus, response) => {
                            if (response.status === 200) {
                                shape = JSON.parse(shapeText)
                            }
                        }
                    );
                }
                if (shape !== undefined) {
                    colorSchemes = shape.colorSchemes;
                } else {
                    colorSchemes = data.shape.colorSchemes;
                }
            }
            if (colorSchemes !== undefined) {
                for (let i = 0; colorSchemes.length > i; i++) {
                    const colorScheme_ = colorSchemes[i];
                    if (colorScheme_.name === "PRIMARY") {
                        colorScheme = colorScheme_;
                        break;
                    }
                }
            }
        }
        return colorScheme;
    }

    static getSecondaryColorScheme(data) {
        let colorScheme = {};
        if (data !== undefined && data !== null) {
            let colorSchemes = [];
            if (data.colorSchemes !== undefined) {
                colorSchemes = data.colorSchemes;
            } else {
                let shape = undefined;
                if (data.shape === undefined || data.shape.colorSchemes === undefined) {
                    let elementType;
                    if (data.shape) {
                        elementType = data.shape.type;
                    } else {
                        elementType = data.type;
                    }
                    $.ajax({
                        url: basePath + "/api/catalogs/element-types/" + elementType + "/shape",
                        type: 'GET',
                        crossDomain: true,
                        contentType: "application/json",
                        converters: {
                            "text json": function (response) {
                                return (response === "") ? null : JSON.parse(response);
                            }
                        },
                        async: false
                    }).done((shapeText, textStatus, response) => {
                            if (response.status === 200) {
                                shape = JSON.parse(shapeText)
                            }
                        }
                    );
                }
                if (shape !== undefined) {
                    colorSchemes = shape.colorSchemes;
                } else {
                    colorSchemes = data.shape.colorSchemes;
                }
            }
            if (colorSchemes !== undefined) {
                for (let i = 0; colorSchemes.length > i; i++) {
                    const colorScheme_ = colorSchemes[i];
                    if (colorScheme_.name === "SECONDARY") {
                        colorScheme = colorScheme_;
                        break;
                    }
                }
            }
        }
        return colorScheme;
    }

    static toVisible(data, node) {
        let visible = true;
        const metaData = data.metaData;
        if (metaData !== undefined) {
            const views = metaData.views;
            if (views !== undefined) {
                for (let i = 0; i < views.length; i++) {
                    const view = views[i];
                    if (view.parentId === data.id) {
                        visible = view.visible;
                        break;
                    }
                }

            }
        }
        return visible;
    }

    static fromVisible(obj, data, model) {
        let metaData = data.metaData;
        if (metaData === undefined) {
            metaData = {};
        }
        let views = metaData.views;
        if (views === undefined) {
            metaData.views = [];
            metaData.views.push({parentId: data.id});
            views = metaData.views;
        }
        for (let i = 0; i < views.length; i++) {
            const view = views[i];
            if (view.parentId === data.id) {
                view.visible = obj;
                break;
            }
        }
        model.setDataProperty(data, "metaData", metaData);
    }
}