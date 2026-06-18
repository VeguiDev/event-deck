package dev.vegui.eventdeck.services;

import java.util.HashMap;
import java.util.Map;

public class ServiceProvider {

    private Map<Class<?>, Object> services = new HashMap<>();

    public <T> void register(Class<T> service, T instance) {
        services.put(service, instance);
    }

    public <T> void unregister(Class<T> service) {
        services.remove(service);
    }

    public <T> T getService(Class<T> service) {
        return (T) services.get(service);
    }

}
