package aop;

public class AopLogDemo {
    public static void main(String[] params) {
        TestLoggingInterface obj = Ioc.createTestLogging();
        obj.calculation(1);
        obj.calculation(1, 2);
        obj.calculation(1, 11, 111);
    }
}
