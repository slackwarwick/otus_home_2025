package aop;

import aop.framework.Ioc;
import aop.logged.TestLogging;
import aop.logged.TestLogging2;
import aop.logged.TestLoggingInterface;
import aop.logged.TestLoggingInterface2;

public class AopLogDemo {
    public static void main(String[] params) {
        TestLoggingInterface obj = Ioc.createTestLogging(TestLoggingInterface.class, TestLogging.class);
        obj.calculation(1);
        obj.calculation(1, 2);
        obj.calculation(1, 11, 111);

        TestLoggingInterface2 obj2 = Ioc.createTestLogging(TestLoggingInterface2.class, TestLogging2.class);
        obj2.calculation(1);
        obj2.calculation(1, 2);
        obj2.calculation(1, 11, 111);
    }
}
