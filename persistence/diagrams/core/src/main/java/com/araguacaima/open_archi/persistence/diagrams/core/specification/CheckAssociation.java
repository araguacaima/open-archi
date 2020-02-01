package com.araguacaima.open_archi.persistence.diagrams.core.specification;

import com.araguacaima.open_archi.persistence.diagrams.core.ElementKind;
import com.araguacaima.specification.AbstractSpecification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CheckAssociation extends AbstractSpecification {

    public CheckAssociation() {
        this(false);
    }

    public CheckAssociation(boolean evaluateAllTerms) {
        super(evaluateAllTerms);
    }

    @Override
    public boolean isSatisfiedBy(Object object, Map<Object, Object> map) {
        ElementKind originType = (ElementKind) map.get("OriginType");
        ElementKind destinationType = (ElementKind) map.get("DestinationType");
        if (originType == null || destinationType == null) {
            return false;
        }
        switch (originType) {
            case ARCHITECTURE_MODEL:
                switch (destinationType) {
                    case LAYER:
                    case SYSTEM:
                    case CONTAINER:
                    case COMPONENT:
                        return true;
                    default:
                        return false;
                }
            default:
                return false;
        }
    }

    @Override
    public Collection<Object> getTerms() {
        return new ArrayList<>();
    }

}
