package phanastrae.operation_starcleave.services;

import java.util.ServiceLoader;

public class Services {

    public static <T> T load(Class<T> clazz) {
        T service = ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Could not load service for " + clazz.getName()));
        return service;
    }
}
