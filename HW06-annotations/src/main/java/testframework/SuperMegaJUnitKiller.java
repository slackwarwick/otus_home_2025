package testframework;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SuperMegaJUnitKiller {
    private enum Stage { BEFORE, TEST, AFTER }

    private SuperMegaJUnitKiller() {
        throw new UnsupportedOperationException();
    }

    public static void testEmAll(String className) {
        Class<?> clazz = getClass(className);
        List<Method> testMethods = new ArrayList<>();
        List<Method> beforeMethods = new ArrayList<>();
        List<Method> afterMethods = new ArrayList<>();
        findMethods(clazz, beforeMethods, afterMethods, testMethods);

        int total = 0, passed = 0, failed = 0;
        for (Method test : testMethods) {
            ++total;
            Set<Stage> passedStages = new HashSet<>();
            Object object = createObject(clazz);
            if (object != null) {
                try {
                    executeMethods(object, beforeMethods, Stage.BEFORE, passedStages);
                    executeMethods(object, List.of(test), Stage.TEST, passedStages);
                } catch (Throwable e) {
                    // empty
                } finally {
                    try {
                        executeMethods(object, afterMethods, Stage.AFTER, passedStages);
                    } catch (Throwable e) {
                        // empty
                    }
                }
            }
            if (passedStages.size() == Stage.values().length) {
                ++passed;
            } else {
                ++failed;
            }
        }
        System.out.printf("-------%nTotal: %d, passed: %d, failed: %d%n", total, passed, failed);
    }

    private static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (Throwable e) {
            throw new IllegalArgumentException("Cannot find class " + className);
        }
    }

    private static void executeMethods(Object object, List<Method> methods, Stage stage, Set<Stage> passedStages) throws InvocationTargetException, IllegalAccessException {
        for (Method method : methods) {
            method.invoke(object);
        }
        passedStages.add(stage);
    }

    private static Object createObject(Class<?> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (Throwable e) {
            return null;
        }
    }

    private static void findMethods(Class<?> clazz, List<Method> beforeMethods, List<Method> afterMethods, List<Method> testMethods) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getDeclaredAnnotations().length != 1)
                continue;
            Annotation annotation = method.getDeclaredAnnotations()[0];
            if (annotation instanceof Before)
                beforeMethods.add(method);
            else if (annotation instanceof After)
                afterMethods.add(method);
            else if (annotation instanceof Test)
                testMethods.add(method);
        }
    }
}
