package com.araguacaima.open_archi.persistence.diagrams.core.specification;

import com.araguacaima.open_archi.persistence.commons.Constants;
import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import com.araguacaima.open_archi.persistence.diagrams.core.ElementKind;
import com.araguacaima.open_archi.persistence.diagrams.core.Item;
import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import com.araguacaima.specification.AbstractSpecification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CheckEntityAlreadyExists extends AbstractSpecification {

    public CheckEntityAlreadyExists() {
        this(false);
    }

    public CheckEntityAlreadyExists(boolean evaluateAllTerms) {
        super(evaluateAllTerms);
    }

    @Override
    public boolean isSatisfiedBy(Object object, Map<Object, Object> map) {
        Class<?> clazz = object.getClass();
        Object initiator = map.get(Constants.INITIATOR);
        if (!initiator.equals(object)) {
            return false;
        }
        if (BaseEntity.class.isAssignableFrom(clazz)) {
            BaseEntity entity = (BaseEntity) object;
            Object key = entity.getId();
            Object storedItem = OrpheusDbJPAEntityManagerUtils.find(clazz, key);
            if (storedItem == null) {
                if (Item.class.isAssignableFrom(clazz)) {
                    Item item = (Item) entity;
                    Map<String, Object> params = new HashMap<>();
                    ElementKind kind = item.getKind();
                    String name = item.getName();
                    String id = item.getId();
                    params.put("kind", kind);
                    params.put("name", name);
                    params.put("id", id);
                    storedItem = OrpheusDbJPAEntityManagerUtils.findByQuery(Item.class, Item.GET_ITEMS_BY_ID_OR_NAME_AND_KIND, params);
                }
            }
            if (storedItem != null) {
                map.put(Constants.SPECIFICATION_MESSAGE, "Entity with key of '" + key + "' already exists");
                Map<Object, Object> storedReplacementes = (Map<Object, Object>) map.get(Constants.SPECIFICATION_STORED_REPLACEMENTS);
                if (storedReplacementes == null) {
                    storedReplacementes = new HashMap<>();
                    map.put(Constants.SPECIFICATION_STORED_REPLACEMENTS, storedReplacementes);
                }
                BaseEntity storedItem1 = (BaseEntity) storedItem;
                storedItem1.override(entity);
                storedItem1.resetId();
                storedReplacementes.put(object, storedItem);
                return true;
            } else {
                map.put(Constants.SPECIFICATION_MESSAGE, "Entity with key of '" + key + "' does not exists");
            }
        }
        return false;
    }

    @Override
    public Collection<Object> getTerms() {
        return new ArrayList<>();
    }

}
