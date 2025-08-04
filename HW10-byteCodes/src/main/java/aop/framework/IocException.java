package aop.framework;

public class IocException extends RuntimeException {
    public IocException(Exception e) {
        super(e);
    }
}
