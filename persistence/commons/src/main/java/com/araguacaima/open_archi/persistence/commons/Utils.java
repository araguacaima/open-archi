package com.araguacaima.open_archi.persistence.commons;

import com.araguacaima.commons.utils.ReflectionUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Random;

public class Utils {

    private static ReflectionUtils reflectionUtils = ReflectionUtils.getInstance();

    public static String randomHexColor() {

        // create random object - reuse this as often as possible
        Random random = new Random();

        // create a big random number - maximum is ffffff (hex) = 16777215 (dez)
        int nextInt = random.nextInt(256 * 256 * 256);

        // format it as hexadecimal string (with hashtag and leading zeros)

        return (String.format("#%06x", nextInt));
    }

    public static boolean filterMethod(Field field) {
        if (field.getAnnotation(JsonIgnore.class) != null
                || field.getAnnotation(Transient.class) != null) {
            return false;
        }
        if ((field.getModifiers() & Modifier.STATIC) != 0) {
            return false;
        }
        Class aClass = null;
        try {
            aClass = reflectionUtils.extractGenerics(field);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return aClass != null && (reflectionUtils.getFullyQualifiedJavaTypeOrNull(aClass) == null && !aClass.isEnum() && !Enum.class.isAssignableFrom(aClass));
    }
}
