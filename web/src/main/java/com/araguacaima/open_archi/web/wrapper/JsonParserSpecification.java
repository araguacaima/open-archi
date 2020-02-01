package com.araguacaima.open_archi.web.wrapper;

import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

import static com.araguacaima.open_archi.web.common.Commons.reflectionUtils;
import static cz.jirutka.rsql.parser.ast.RSQLOperators.*;

class JsonParserSpecification {

    public static final String MARKED_FOR_DELETION = "###MARKED_FOR_DELETION###";
    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();
    private static Logger log = LoggerFactory.getLogger(JsonParserSpecification.class);

    private static boolean isWrapperType(Class<?> clazz) {
        return WRAPPER_TYPES.contains(clazz);
    }

    private static Set<Class<?>> getWrapperTypes() {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        ret.add(BigDecimal.class);
        ret.add(String.class);
        return ret;
    }

    private static Object iter(Object obj,
                               String field,
                               final Object value,
                               final ComparisonOperator operator,
                               final List<String> selectorList)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        if (field == null) {
            field = selectorList.get(0);
        }

        if (obj instanceof Collection) {
            Collection<Object> list = (Collection<Object>) obj;
            String finalField = field;
            CollectionUtils.filter(list, it -> {
                Object object = null;
                try {
                    object = iter(it, finalField, value, operator, selectorList);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    log.error(e.getMessage());
                }
                return object != null && !object.toString().contains(MARKED_FOR_DELETION);
            });

            if (list.size() == 0) {
                obj = MARKED_FOR_DELETION;
            }

        } else if (obj instanceof Map) {
            final String tokenizedSelector = selectorList.get(0);
            Map<String, Object> obj1 = (Map<String, Object>) obj;
            Set<Map.Entry<String, Object>> entries = obj1.entrySet();
            List<String> selectorSubList = selectorList.subList(1, selectorList.size());
            for (Map.Entry<String, Object> entry : entries) {
                String key = entry.getKey();
                Object value_ = entry.getValue();
                if (key.equals(tokenizedSelector)) {
                    Object result = iter(value_, key, value, operator, selectorSubList);
                    if (result == null || result.toString().contains(MARKED_FOR_DELETION)) {
                        obj1.put(key, MARKED_FOR_DELETION);
                    }
                    break;
                }
            }
        } else {
            return compareValue(obj, field, value, operator) ? obj : null;
        }
        return obj;
    }

    static Object parse(Object json, String selector, Object value, ComparisonOperator operator)
            throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return iter(json, null, value, operator, Arrays.asList(selector.split("\\.")));
    }

    private static Boolean compareValue(Object element, String field, Object value, ComparisonOperator op)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Object newElement = null;
        if (element == null) {
            return false;
        }
        if (element instanceof Collection) {
            List<Object> elements = new ArrayList<>();
            for (Object element_ : ((Collection) element)) {
                if (compareValue(element_, field, value, op)) {
                    elements.add(element_);
                }
            }
            return elements.size() > 0;
        } else if (element instanceof Map) {
            newElement = ((Map) element).get(field);
            if (newElement == null) {
                return compareValue(((Map) element).values(), field, value, op);
            }
        } else if (!isWrapperType(element.getClass())) {
            Field field_ = reflectionUtils.getField(element.getClass(), field);
            field_.setAccessible(true);
            Object newObject = field_.get(element);
            return compareValue(newObject, field, value, op);
        }

        if (newElement == null) {
            newElement = element;
        }

        if (newElement instanceof Number) {
            value = new BigDecimal(String.valueOf(value));
            newElement = new BigDecimal(String.valueOf(newElement));
        } else if (newElement instanceof Boolean) {
            value = Boolean.parseBoolean(String.valueOf(value));
        } else if (newElement instanceof Character || newElement instanceof String) {
            value = String.valueOf(value);
        } else {
            newElement = newElement.toString();
        }

        if (op == EQUAL) {
            String value_ = value.toString();
            String newElement_ = newElement.toString();
            if (value_.startsWith("*") && value_.endsWith("*")) {
                CharSequence obj = value_.subSequence(1, value_.length() - 1);
                String regex = "(?i:.*" + String.valueOf(obj) + ".*)";
                return newElement_.matches(regex);
            }
            if (value_.endsWith("*")) {
                return newElement_.startsWith(value_);
            }
            if (value_.startsWith("*")) {
                return newElement_.endsWith(value_);
            }
            return newElement.equals(value);
        } else if (op == NOT_EQUAL) {
            return !newElement.equals(value);
        } else if (op == GREATER_THAN) {
            return (int) MethodUtils.invokeMethod(newElement, "compareTo", value) > 0;
        } else if (op == GREATER_THAN_OR_EQUAL) {
            return (int) MethodUtils.invokeMethod(newElement, "compareTo", value) >= 0;
        } else if (op == LESS_THAN) {
            return (int) MethodUtils.invokeMethod(newElement, "compareTo", value) < 0;
        } else if (op == LESS_THAN_OR_EQUAL) {
            return (int) MethodUtils.invokeMethod(newElement, "compareTo", value) <= 0;
        } else {
            throw new IllegalArgumentException("Unknown operator: " + op);
        }
    }
}
