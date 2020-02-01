package com.araguacaima.open_archi.persistence.diagrams.core.specification;

import com.araguacaima.open_archi.persistence.diagrams.core.Item;
import com.araguacaima.open_archi.persistence.diagrams.core.Shape;
import com.araguacaima.open_archi.persistence.diagrams.core.ShapeFactory;
import com.araguacaima.specification.AbstractSpecification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static com.araguacaima.open_archi.persistence.diagrams.core.Item.itemComparator;

public class AppendShape extends AbstractSpecification {

    public AppendShape() {
        this(false);
    }

    public AppendShape(boolean evaluateAllTerms) {
        super(evaluateAllTerms);
    }

    public boolean isSatisfiedBy(Object object, Map<Object, Object> map) {
        if (Item.class.isAssignableFrom(object.getClass())) {
            Item item = (Item) object;
            Shape shape = item.getShape();
            Shape foundShape;
            if (shape == null) {
                foundShape = ShapeFactory.buildDefault();
                item.setShape(foundShape);
            } else {
                foundShape = ShapeFactory.getInstance(shape.getType());
                if (foundShape != null) {
                    foundShape.override(shape, true, null, null, itemComparator);
                    item.setShape(foundShape);
                }
            }
        }
        return true;
    }

    public Collection<Object> getTerms() {
        return new ArrayList<>();
    }
}
