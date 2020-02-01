package com.araguacaima.open_archi.persistence.diagrams.core.specification;

import com.araguacaima.open_archi.persistence.commons.Constants;
import com.araguacaima.open_archi.persistence.diagrams.core.Shape;
import com.araguacaima.open_archi.persistence.diagrams.core.ShapeFactory;
import com.araguacaima.specification.AbstractSpecification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FixShape extends AbstractSpecification {

    public FixShape() {
        this(false);
    }

    public FixShape(boolean evaluateAllTerms) {
        super(evaluateAllTerms);
    }

    @Override
    public boolean isSatisfiedBy(Object object, Map<Object, Object> map) {
        Class<?> clazz = object.getClass();
        if (Shape.class.isAssignableFrom(clazz)) {
            Shape entity = (Shape) object;
            Shape shape = ShapeFactory.getInstance(entity.getType());
            shape.override(entity);
            shape.resetId();
            Map<Object, Object> storedReplacementes = (Map<Object, Object>) map.get(Constants.SPECIFICATION_STORED_REPLACEMENTS);
            if (storedReplacementes == null) {
                storedReplacementes = new HashMap<>();
                map.put(Constants.SPECIFICATION_STORED_REPLACEMENTS, storedReplacementes);
            }
            storedReplacementes.put(object, shape);
            map.put(Constants.BREAK_REMAINING_SPECIFICATIONS, true);
        }
        return true;
    }

    @Override
    public Collection<Object> getTerms() {
        return new ArrayList<>();
    }

}
