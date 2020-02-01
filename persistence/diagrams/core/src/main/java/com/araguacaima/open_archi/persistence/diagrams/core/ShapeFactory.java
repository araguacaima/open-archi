package com.araguacaima.open_archi.persistence.diagrams.core;

import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;

import java.util.HashMap;
import java.util.Map;

public class ShapeFactory {
    public static Shape getInstance(ElementKind kind) {

        if (kind == null) {
            return null;
        }

        Map<String, Object> params = new HashMap<>();
        params.put("type", kind);
        ElementShape elementShape = OrpheusDbJPAEntityManagerUtils.findByQuery(ElementShape.class, ElementShape.GET_ELEMENT_SHAPE_BY_TYPE, params);

        if (elementShape == null) {
            return null;
        }
        return ElementShapeWrapper.toShape(elementShape);
    }

    public static Shape buildDefault() {
        Shape shape = new Shape();
        shape.setType(ElementKind.DEFAULT);
        shape.setColorSchemes(ColorSchemeFactory.DEFAULT);
        shape.setInput(true);
        shape.setOutput(true);
        return shape;

    }
}
