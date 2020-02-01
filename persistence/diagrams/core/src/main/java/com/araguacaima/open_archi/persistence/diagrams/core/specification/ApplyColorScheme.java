package com.araguacaima.open_archi.persistence.diagrams.core.specification;

import com.araguacaima.open_archi.persistence.diagrams.core.ElementKind;
import com.araguacaima.open_archi.persistence.diagrams.core.ElementShape;
import com.araguacaima.open_archi.persistence.diagrams.core.ElementShapeWrapper;
import com.araguacaima.open_archi.persistence.diagrams.core.Item;
import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import com.araguacaima.specification.AbstractSpecification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ApplyColorScheme extends AbstractSpecification {

    public ApplyColorScheme() {
        this(false);
    }

    public ApplyColorScheme(boolean evaluateAllTerms) {
        super(evaluateAllTerms);
    }

    public boolean isSatisfiedBy(Object object, Map<Object, Object> map) {
        if (Item.class.isAssignableFrom(object.getClass())) {
            Item item = (Item) object;
            if (item.getShape() == null) {
                ElementKind kind = item.getKind();
                Map<String, Object> map_ = new HashMap<>();
                map_.put("type", kind);
                ElementShape elementShape = OrpheusDbJPAEntityManagerUtils.findByQuery(ElementShape.class, ElementShape.GET_ELEMENT_SHAPE_BY_TYPE, map_);
                item.setShape(ElementShapeWrapper.toShape(elementShape));
            }
        }
        return true;
    }

    public Collection<Object> getTerms() {
        return new ArrayList<>();
    }
}
