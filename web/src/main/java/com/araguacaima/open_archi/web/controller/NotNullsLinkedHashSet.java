package com.araguacaima.open_archi.web.controller;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.lang3.StringUtils;


import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

/**
 * Created by alejandro on 13/10/14.
 */

public class NotNullsLinkedHashSet<T> extends LinkedHashSet<T> {

    private static final long serialVersionUID = 4462897959138488249L;
    private boolean traverseFields;
    private static Predicate NULL_PREDICATE = new Predicate() {
        @Override
        public boolean evaluate(Object e) {
            try {
                final Map<String, Object> map = PropertyUtils.describe(e);
                for (final Map.Entry<String, Object> entry : map.entrySet()) {
                    if (!("class".equals(entry.getKey()) && Class.class.isAssignableFrom(entry.getValue().getClass()))
                        && entry.getValue() != null) {
                        return false;
                    }
                }
            } catch (final IllegalAccessException iae) {
                iae.printStackTrace();
            } catch (final InvocationTargetException ite) {
                ite.printStackTrace();
            } catch (final NoSuchMethodException nsme) {
                nsme.printStackTrace();
            }
            return true;
        }
    };

    public static Predicate NOT_EMPTY_PREDICATE = e -> {
        try {
            final Map<String, Object> map = PropertyUtils.describe(e);
            for (final Map.Entry<String, Object> entry : map.entrySet()) {
                if (!("class".equals(entry.getKey()) && Class.class.isAssignableFrom(entry.getValue().getClass()))
                        && entry.getValue() != null && StringUtils.isNotBlank(entry.getValue().toString())) {
                    return false;
                }
            }
        } catch (final IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
            exception.printStackTrace();
        }
        return true;
    };

    private Predicate predicate = NULL_PREDICATE;

    public NotNullsLinkedHashSet() {
        this(false);
    }

    public NotNullsLinkedHashSet(final boolean traverseFields) {
        super();
        this.traverseFields = traverseFields;
    }

    public NotNullsLinkedHashSet(Predicate predicate) {
        this();
        if (predicate != null) {
            this.predicate = predicate;
        }
    }
    
    public NotNullsLinkedHashSet(final boolean traverseFields, Predicate allFieldsAreNull) {
        this(traverseFields);
        if (allFieldsAreNull != null) {
            this.predicate = allFieldsAreNull;
        }
    }

    public NotNullsLinkedHashSet(final boolean traverseFields,
                                 Predicate allFieldsAreNull,
                                 final Collection<? extends T> elements) {
        this(traverseFields, allFieldsAreNull);
        this.addAll(elements);
    }


    public NotNullsLinkedHashSet(final Collection<? extends T> elements) {
        this(false, null);
        this.addAll(elements);
    }

    @Override
    public boolean add(final T e) {
        return e != null && (!this.traverseFields || !predicate.evaluate(e)) && super.add(e);
    }

    public int indexOf(final T e) {
        int result = 0;

        for (final T element : this) {
            if (element.equals(e)) {
                return result;
            }
            result++;
        }

        return -1;
    }


    public int indexOf(Predicate e) {
        int result = 0;

        for (final T element : this) {
            if (e.evaluate(element)) {
                return result;
            }
            result++;
        }

        return -1;
    }

    @Override
    public boolean addAll(final Collection<? extends T> elements) {
        boolean result = true;
        if (elements == null) {
            return false;
        }
        for (final T element : elements) {
            result = add(element) && result;
        }
        return result;
    }

}