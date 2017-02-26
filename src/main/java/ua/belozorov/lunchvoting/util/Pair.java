package ua.belozorov.lunchvoting.util;

/**

 *
 * Created on 14.02.17.
 */
public final class Pair<A,B> {

    public static <A,B> Pair<A,B> pairOf(A a, B b) {
        return new Pair<>(a, b);
    }

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

    @Override
    public String toString() {
        return "Pair{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }
}
