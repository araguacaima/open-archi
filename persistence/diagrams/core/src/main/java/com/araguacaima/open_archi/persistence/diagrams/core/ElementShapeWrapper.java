package com.araguacaima.open_archi.persistence.diagrams.core;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Set;

public class
ElementShapeWrapper {
    public static Shape toShape(ElementShape elementShape) {
        Shape shape = new Shape();
        shape.setKey(elementShape.getKey());
        shape.setId(elementShape.getId());
        shape.setMeta(elementShape.getMeta());
        shape.setType(elementShape.getType());
        Set<ColorScheme> colorSchemes = elementShape.getColorSchemes();
        if (CollectionUtils.isNotEmpty(colorSchemes)) {
            colorSchemes.forEach(colorScheme -> shape.addColorScheme(ColorSchemeWrapper.clone(colorScheme)));
        }
        shape.setInput(elementShape.isInput());
        shape.setOutput(elementShape.isOutput());
        shape.setFigure(elementShape.getFigure());
        shape.setGroup(elementShape.isGroup());
        return shape;
    }

    public static ElementShape toElementShape(Shape shape) {
        ElementShape elementShape = new ElementShape();
        elementShape.setKey(shape.getKey());
        elementShape.setId(shape.getId());
        elementShape.setMeta(shape.getMeta());
        elementShape.setType(shape.getType());
        elementShape.setColorSchemes(shape.getColorSchemes());
        Set<ColorScheme> colorSchemes = shape.getColorSchemes();
        if (CollectionUtils.isNotEmpty(colorSchemes)) {
            colorSchemes.forEach(colorScheme -> elementShape.addColorScheme(ColorSchemeWrapper.clone(colorScheme)));
        }
        elementShape.setInput(shape.isInput());
        elementShape.setOutput(shape.isOutput());
        elementShape.setFigure(shape.getFigure());
        elementShape.setGroup(shape.isGroup());
        return elementShape;
    }
}
