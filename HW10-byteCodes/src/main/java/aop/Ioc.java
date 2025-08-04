package aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;

public class Ioc {
    private static final Logger logger = LoggerFactory.getLogger(Ioc.class);

    private Ioc() {}

    static TestLoggingInterface createTestLogging() {
        InvocationHandler handler = new DemoInvocationHandler(new TestLogging());
        return (TestLoggingInterface)
                Proxy.newProxyInstance(Ioc.class.getClassLoader(), new Class<?>[] {TestLoggingInterface.class}, handler);
    }

    static class DemoInvocationHandler implements InvocationHandler {
        private final TestLoggingInterface myObject;
        private final Class<? extends TestLoggingInterface> myClass;

        DemoInvocationHandler(TestLoggingInterface myObject) {
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
            StringBuilder b = new StringBuilder(method.getName());
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
