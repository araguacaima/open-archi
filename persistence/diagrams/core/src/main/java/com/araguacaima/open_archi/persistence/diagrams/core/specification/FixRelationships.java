package com.araguacaima.open_archi.persistence.diagrams.core.specification;

import com.araguacaima.open_archi.persistence.diagrams.core.CompositeElement;
import com.araguacaima.open_archi.persistence.diagrams.core.Item;
import com.araguacaima.specification.AbstractSpecification;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class FixRelationships extends AbstractSpecification {

    public FixRelationships() {
        this(false);
    }

    public FixRelationships(boolean evaluateAllTerms) {
        super(evaluateAllTerms);
    }

    public boolean isSatisfiedBy(Object object, Map<Object, Object> map) {
        if (Item.class.isAssignableFrom(object.getClass())) {
            Item item = (Item) object;
            if (item.getRelationships() != null && !item.getRelationships().isEmpty()) {
                Set<Item> items = (Set<Item>) map.get("Items");
                item.getRelationships().forEach(relationship -> {
                    if (relationship.getSource() != null) {
                        String sourceId = relationship.getSource().getId();
                        if (StringUtils.isNotBlank(sourceId)) {
                            if (items != null) {
                                items.forEach(item_ -> {
                                    if (sourceId.equals(item_.getId()) || sourceId.equals(item_.getKey())) {
                                        relationship.setSource(CompositeElement.fromItem(item_));
                                    }
                                });
                            }
                        }
                    }
                    if (relationship.getDestination() != null) {
                        String destinationId = relationship.getDestination().getId();
                        if (StringUtils.isNotBlank(destinationId)) {
                            if (items != null) {
                                items.forEach(item_ -> {
                                    if (destinationId.equals(item_.getId()) || destinationId.equals(item_.getKey())) {
                                        relationship.setDestination(CompositeElement.fromItem(item_));
                                    }
                                });
                            }
                        }
                    }
                });
            }
        }
        return true;
    }

    public Collection<Object> getTerms() {
        return new ArrayList<>();
    }
}
