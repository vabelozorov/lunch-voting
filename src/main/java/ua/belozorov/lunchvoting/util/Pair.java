package ua.belozorov.lunchvoting.util;

/**
 * <h2></h2>
 *
 * @author vabelozorov on 14.02.17.
 */
public final class Pair<A,B> {
    private final A a;
    private final B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }
}
