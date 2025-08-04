package aop;

public class TestLogging implements TestLoggingInterface {
    @Override
    @Log
    public void calculation(int param1) {}

    @Override
    // @Log no annotation!
    public void calculation(int param1, int param2) {}

    @Override
    @Log
    public void calculation(int param1, int param2, int param3) {}
}
