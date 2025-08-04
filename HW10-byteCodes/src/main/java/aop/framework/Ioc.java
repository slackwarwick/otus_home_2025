package aop.framework;

import aop.logged.TestLogging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.*;
import java.util.Arrays;

public class Ioc {
    private static final Logger logger = LoggerFactory.getLogger(Ioc.class);

    private Ioc() {}

    public static <T> T createTestLogging(Class<T> ifaceClazz, Class<? extends T> implClazz) {
        T object;
        try {
            Constructor<? extends T> constructor = implClazz.getConstructor();
            object = constructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new IocException(e);
        }
        InvocationHandler handler = new DemoInvocationHandler<T>(object);
        return (T) Proxy.newProxyInstance(Ioc.class.getClassLoader(), new Class<?>[] {ifaceClazz}, handler);
    }

    static class DemoInvocationHandler<T> implements InvocationHandler {
        private final T myObject;
        private final Class<?> myClass;

        DemoInvocationHandler(T myObject) {
            this.myObject = myObject;
            this.myClass = myObject.getClass();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Method loggedMethod = getLoggedMethod(method);
            if (loggedMethod != null) {
                logger.info("executed method: {}", getMethodWithParameters(loggedMethod, args));
            }
            return method.invoke(myObject, args);
        }

        private String getMethodWithParameters(Method method, Object[] args) {
            if (args.length != method.getParameterCount()) {
                throw new IllegalArgumentException(String.format("Method params count: %d, values count: %d",
                        method.getParameterCount(), args.length));
            }
            StringBuilder b = new StringBuilder(myClass.getSimpleName() + "." + method.getName());
            for (int i = 0; i < method.getParameters().length; ++i) {
                b.append(", ").append(method.getParameters()[i].getName()).append(": ").append(args[i]);
            }
            return b.toString();
        }

        private Method getLoggedMethod(Method method) {
            for (Method declaredMethod : myClass.getDeclaredMethods()) {
                if (declaredMethod.getName().equals(method.getName())
                        && Arrays.equals(declaredMethod.getParameterTypes(), method.getParameterTypes())
                        && declaredMethod.isAnnotationPresent(Log.class)) {
                    return method;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return "DemoInvocationHandler{" + "myClass=" + myObject + '}';
        }
    }
}
