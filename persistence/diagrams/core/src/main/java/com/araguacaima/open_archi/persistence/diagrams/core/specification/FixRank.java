package com.araguacaima.open_archi.persistence.diagrams.core.specification;

import com.araguacaima.open_archi.persistence.commons.Constants;
import com.araguacaima.open_archi.persistence.diagrams.core.Item;
import com.araguacaima.open_archi.persistence.diagrams.core.Rank;
import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import com.araguacaima.specification.AbstractSpecification;

import java.util.*;

public class FixRank extends AbstractSpecification {

    public FixRank() {
        this(false);
    }

    public FixRank(boolean evaluateAllTerms) {
        super(evaluateAllTerms);
    }

    @Override
    public boolean isSatisfiedBy(Object object, Map<Object, Object> map) {
        Class<?> clazz = object.getClass();
        if (Item.class.isAssignableFrom(clazz)) {
            Item entity = (Item) object;
            Object initiator = map.get(Constants.INITIATOR);
            if (initiator == null) {
                return true;
            } else {
                if (!initiator.equals(object)) {
                    Item item = null;
                    if (Item.class.isAssignableFrom(initiator.getClass())) {
                        item = (Item) initiator;
                    }
                    Integer position;
                    Set<Rank> ranks;
                    Item storedEntity = OrpheusDbJPAEntityManagerUtils.find(entity);
                    if (storedEntity != null) {
                        ranks = storedEntity.getRanks();
                        entity.setRanks(ranks);
                    } else {
                        ranks = entity.getRanks();
                    }
                    if (ranks == null) {
                        ranks = new LinkedHashSet<>();
                    }
                    if (item != null) {
                        if (ranks.isEmpty()) {
                            position = Rank.getNextRank((Set<Rank>) map.get(Constants.STORED_RANKS));
                        } else {
                            position = Rank.getMaxRank(ranks);
                        }
                        Rank rank = new Rank(item.getId(), position);
                        ranks.add(rank);
                    }
                    map.put(Constants.STORED_RANKS, ranks);
                }
            }
        }
        return true;
    }

    @Override
    public Collection<Object> getTerms() {
        return new ArrayList<>();
    }

}
