package ru.otus.appcontainer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

@SuppressWarnings("squid:S1068")
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    public AppComponentsContainerImpl(Class<?>... initialConfigClasses) {
        processConfig(initialConfigClasses);
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        return (C) getComponent(componentClass);
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        if (!appComponentsByName.containsKey(componentName)) {
            throw new IllegalArgumentException("No component of name " + componentName);
        }
        return (C) appComponentsByName.get(componentName);
    }

    private void processConfig(Class<?>... configClasses) {
        var orderedConfigClasses = checkAndReorder(configClasses);
        var classByMethod = new HashMap<Method, Class<?>>();
        var methodsByName = new HashMap<String, List<Method>>();
        var methodsByOrder = new HashMap<Integer, List<Method>>();
        for (var clazz : orderedConfigClasses) {
            var byName = getMethodsByName(clazz);
            classByMethod.putAll(getClassByMethod(byName.values(), clazz));
            methodsByName.putAll(byName);
            methodsByOrder.putAll(getMethodsByOrder(clazz));
        }
        checkDuplicates(methodsByName);
        var classInstances = new HashMap<Class<?>, Object>();
        for (var methods : methodsByOrder.values()) {
            for (var method: methods) {
                var configClass = classByMethod.get(method);
                var configInstance = classInstances.computeIfAbsent(configClass, v ->
                        createConfigInstance(configClass));
                var parameterInstances = getParameterInstances(method);
                var component = createComponent(method, configInstance, parameterInstances);
                appComponents.add(component);
                appComponentsByName.put(getComponentName(method), component);
            }
        }
    }

    private List<Class<?>> checkAndReorder(Class<?>... configClasses) {
        var result = new ArrayList<Class<?>>();
        for (Class<?> configClass : configClasses) {
            checkConfigClass(configClass);
            result.add(configClass);
        }
        result.sort(Comparator.comparing(c -> c.getAnnotation(AppComponentsContainerConfig.class).order()));
        return result;
    }

    private Map<Method, Class<?>> getClassByMethod(Collection<List<Method>> methods, Class<?> clazz) {
        var result = new HashMap<Method, Class<?>>();
        for (List<Method> mm : methods) {
            for (Method method : mm) {
                result.put(method, clazz);
            }
        }
        return result;
    }

    private void checkDuplicates(Map<String, List<Method>> methodsByName) {
        for (var methods : methodsByName.entrySet()) {
            if (methods.getValue().size() > 1) {
                throw new IllegalArgumentException("Two components with same name: " + methods.getKey());
            }
        }
    }

    private Object[] getParameterInstances(Method method) {
        return Arrays.stream(method.getParameterTypes())
                .map(this::getComponent)
                .toArray();
    }

    private Object createComponent(Method method, Object configInstance, Object[] parameterInstances) {
        try {
            return method.invoke(configInstance, parameterInstances);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException("createComponent", e);
        }
    }

    private Object createConfigInstance(Class<?> configClass) {
        try {
            return configClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("createConfigInstance", e);
        }
    }

    private Object getComponent(Class<?> type) {
        Object result = null;
        for (Object appComponent : appComponents) {
            if (type.isAssignableFrom(appComponent.getClass())) {
                if (result != null) {
                    throw new IllegalStateException("Duplicate component found of type " + type.getSimpleName());
                }
                result = appComponent;
            }
        }
        if (result == null) {
            throw new IllegalStateException("No component found of type " + type.getSimpleName());
        }
        return result;
    }

    private Map<Integer, List<Method>> getMethodsByOrder(Class<?> configClass) {
        return Arrays.stream(configClass.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(AppComponent.class))
                .collect(Collectors.groupingBy(this::getComponentOrder, HashMap::new, Collectors.toList()));
    }

    private Integer getComponentOrder(Method method) {
        return Objects.requireNonNull(method.getAnnotation(AppComponent.class)).order();
    }

    private String getComponentName(Method method) {
        return Objects.requireNonNull(method.getAnnotation(AppComponent.class)).name();
    }

    private Map<String, List<Method>> getMethodsByName(Class<?> configClass) {
        return Arrays.stream(configClass.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(AppComponent.class))
                .collect(Collectors.groupingBy(this::getComponentName, HashMap::new, Collectors.toList()));
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }
}
