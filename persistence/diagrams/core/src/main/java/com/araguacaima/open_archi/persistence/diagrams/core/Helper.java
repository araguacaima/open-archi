package com.araguacaima.open_archi.persistence.diagrams.core;

import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;

import java.util.*;

import static com.araguacaima.open_archi.persistence.diagrams.core.Item.itemComparator;

public class Helper {


    @SuppressWarnings("unchecked")
    public static <T extends BaseEntity> void fixCollection(
            Set<T> source,
            Set<T> target,
            boolean keepMeta,
            String suffix,
            CompositeElement clonedFrom,
            Comparator comparator) {

        Collection<T> remaining = CollectionUtils.disjunction(target, source);
        Collection<T> toRemove = new ArrayList<>();
        for (T item : target) {
            T found = IterableUtils.find(remaining, item_ -> comparator.compare(item_, item) == 0);
            if (found != null) {
                toRemove.add(found);
            }
        }
        target.removeAll(toRemove);
        for (T item : source) {
            try {
                T newItem = (T) item.getClass().newInstance();
                newItem.override(item, keepMeta, suffix, clonedFrom, itemComparator);
                T found = IterableUtils.find(target, item_ -> comparator.compare(item_, newItem) == 0);
                if (found != null) {
                    target.add(found);
                } else {
                    target.add(item);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseEntity> void fixCollection(
            Set<T> source,
            Set<T> target,
            boolean keepMeta,
            Comparator comparator) {

        if (CollectionUtils.isNotEmpty(source)) {
            Collection<T> remaining = CollectionUtils.disjunction(target, source);
            Collection<T> toRemove = new ArrayList<>();
            for (T item : target) {
                T found = IterableUtils.find(remaining, item_ -> comparator.compare(item_, item) == 0);
                if (found != null) {
                    toRemove.add(found);
                }
            }
            target.removeAll(toRemove);
            for (T item : source) {
                try {
                    T newItem = (T) item.getClass().newInstance();
                    newItem.copyNonEmpty(item, keepMeta, itemComparator);
                    T found = IterableUtils.find(target, item_ -> comparator.compare(item_, newItem) == 0);
                    if (found != null) {
                        target.add(found);
                    } else {
                        target.add(item);
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T, U> void fixMap(
            Map<T, U> source,
            Map<T, U> target) {
        //TODO finish
        target.putAll(source);
    }

    @SuppressWarnings("unchecked")
    public static <T extends BaseEntity> T fixItem(
            T source,
            T target,
            boolean keepMeta,
            String suffix,
            CompositeElement clonedFrom,
            boolean keepStored) {
        T target_;
        if (source != null) {
            if (keepStored) {
                target_ = OrpheusDbJPAEntityManagerUtils.find(source);
                if (target_ == null) {
                    target_ = target;
                }
            } else {
                target_ = target;
            }
            if (target_ == null) {
                target_ = source;
            } else {
                target_.override(source, keepMeta, suffix, clonedFrom, null);
            }
        } else {
            target_ = null;
        }
        return target_;
    }
}
