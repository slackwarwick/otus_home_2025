package aop.logged;

import aop.framework.Log;

public class TestLogging2 implements TestLoggingInterface2 {
    @Override
    @Log
    public void calculation(int param1) {}

    @Override
    @Log
    public void calculation(int param1, int param2) {}

    @Override
    // @Log no annotation!
    public void calculation(int param1, int param2, int param3) {}
}
