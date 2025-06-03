import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SuperMegaJUnitKiller {
    public static void main(String... args) {
        testEmAll("VeryUsefulTest");
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
            Object object = createObject(clazz);
            if (object == null) {
                ++failed;
            } else {
                boolean success = executeMethods(object, beforeMethods);
                if (success)
                    success = executeMethods(object, List.of(test));
                if (success)
                    success = executeMethods(object, afterMethods);
                if (success)
                    ++passed;
                else
                    ++failed;
            }
        }
        System.out.printf("Total: %d, passed: %d, failed: %d%n", total, passed, failed);
    }

    private static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (Throwable e) {
            throw new IllegalArgumentException("Cannot find class " + className);
        }
    }

    private static boolean executeMethods(Object object, List<Method> methods) {
        for (Method method : methods) {
            try {
                method.invoke(object);
            } catch (Throwable e) {
                return false;
            }
        }
        return true;
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

    private SuperMegaJUnitKiller() {
        throw new UnsupportedOperationException();
    }
}
