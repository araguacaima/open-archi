package com.araguacaima.open_archi.persistence.diagrams.core.specification;

import com.araguacaima.open_archi.persistence.diagrams.core.Item;
import com.araguacaima.specification.AbstractSpecification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class PropagatePrototype extends AbstractSpecification {

    public PropagatePrototype() {
        this(false);
    }

    public PropagatePrototype(boolean evaluateAllTerms) {
        super(evaluateAllTerms);
    }

    public boolean isSatisfiedBy(Object object, Map<Object, Object> map) {
        if (Item.class.isAssignableFrom(object.getClass())) {
            Item initiator = (Item) map.get("Parent");
            Item item = (Item) object;
            boolean prototype;
            if (initiator != null) {
                prototype = initiator.isPrototype();
            } else {
                initiator = (Item) map.get("Initiator");
                prototype = item.isPrototype();
                if (initiator == null) {
                    Boolean incomingPrototype = (Boolean) map.get("IsPrototype");
                    if (incomingPrototype != null) {
                        initiator = (Item) map.get("Parent");
                        if (initiator != null) {
                            prototype = initiator.isPrototype();
                        }
                    }
                } else {
                    prototype = initiator.isPrototype();
                }
            }
            item.setPrototype(prototype);
        }
        return true;
    }

    public Collection<Object> getTerms() {
        return new ArrayList<>();
    }
}
