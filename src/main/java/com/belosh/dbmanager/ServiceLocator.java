package com.belosh.dbmanager;

import java.util.HashMap;
import java.util.Map;

public class ServiceLocator {

    private static final Map<String, Object> LOCATOR = new HashMap<>();

    public static void register(String name, Object service) {
        LOCATOR.put(name, service);
    }

    public static <T> T get(String name,  Class<T> clazz) {
        Object object = LOCATOR.get(name);
        if (object != null && clazz.isAssignableFrom(object.getClass())) {
            return clazz.cast(object);
        }
        throw new RuntimeException("Locator doesn't contain service with name: " + name + " and class: " + clazz);
    }
}
