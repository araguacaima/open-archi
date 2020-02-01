package com.araguacaima.open_archi.persistence.diagrams.core.specification;

import com.araguacaima.open_archi.persistence.commons.Constants;
import com.araguacaima.open_archi.persistence.diagrams.core.DiagramType;
import com.araguacaima.open_archi.persistence.diagrams.core.Item;
import com.araguacaima.open_archi.persistence.diagrams.core.Rank;
import com.araguacaima.specification.AbstractSpecification;

import java.util.*;

public class FixDiagramType extends AbstractSpecification {

    public FixDiagramType() {
        this(false);
    }

    public FixDiagramType(boolean evaluateAllTerms) {
        super(evaluateAllTerms);
    }

    @Override
    public boolean isSatisfiedBy(Object object, Map<Object, Object> map) {
        Class<?> clazz = object.getClass();
        if (Item.class.isAssignableFrom(clazz)) {
            Item entity = (Item) object;

            DiagramType diagramType = entity.getDiagramType();

            if (diagramType == null) {
                Item initiator = (Item) map.get(Constants.INITIATOR);
                diagramType = initiator.getDiagramType();
            }

            DiagramType diagramTypeStored = (DiagramType) map.get(Constants.INITIATOR_DIAGRAM_TYPE);
            if (diagramTypeStored == null) {
                map.put(Constants.INITIATOR_DIAGRAM_TYPE, diagramType);
                diagramTypeStored = diagramType;
            }
            entity.setDiagramType(diagramTypeStored);
        }
        return true;
    }

    @Override
    public Collection<Object> getTerms() {
        return new ArrayList<>();
    }

}
