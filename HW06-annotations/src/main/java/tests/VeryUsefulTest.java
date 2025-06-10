package tests;

import testframework.After;
import testframework.Before;
import testframework.Test;

public class VeryUsefulTest {
    @Before
    public void prepare1() {
        System.out.println(this + " Before1");
    }

    @Before
    public void prepare2() {
        System.out.println(this + " Before2");
    }

    @After
    public void close1() {
        System.out.println(this + " After1");
    }

    @After
    public void close2() {
        System.out.println(this + " After2");
    }

    @Test
    public void test1() {
        System.out.println(this + " Test1");
    }

    @Test
    public void test2() {
        System.out.println(this + " Test2");
    }

    @Test
    public void test3() {
        System.out.println(this + " Test3 (failing)");
        throw new IllegalStateException("");
    }
}
