package com.araguacaima.open_archi.persistence.diagrams.core.specification;

import com.araguacaima.commons.utils.ReflectionUtils;
import com.araguacaima.open_archi.persistence.diagrams.core.Item;
import com.araguacaima.specification.AbstractSpecification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class ExtractItems extends AbstractSpecification {

    private static ReflectionUtils reflectionUtils = ReflectionUtils.getInstance();

    public ExtractItems() {
        this(false);
    }

    public ExtractItems(boolean evaluateAllTerms) {
        super(evaluateAllTerms);
    }

    public boolean isSatisfiedBy(Object object, Map<Object, Object> map) {
        Set<Item> taggables = (Set<Item>) map.get("Items");
        if (taggables == null) {
            Object initiator = map.get("Initiator");
            taggables = reflectionUtils.extractByType(initiator, Item.class);
            map.put("Items", taggables);
        }
        return true;
    }

    public Collection<Object> getTerms() {
        return new ArrayList<>();
    }
}
