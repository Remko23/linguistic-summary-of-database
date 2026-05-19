package org.example.fuzzy.membership;

/**
 * Implements a trapezoidal membership function.
 * Defined by four parameters: a (left foot), b (left shoulder), c (right shoulder), d (right foot).
 */
public class TrapezoidalMembershipFunction implements MembershipFunction {
    private final double a;
    private final double b;
    private final double c;
    private final double d;

    public TrapezoidalMembershipFunction(double a, double b, double c, double d) {
        if (a > b || b > c || c > d) {
            throw new IllegalArgumentException("Parameters must satisfy a <= b <= c <= d");
        }
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    @Override
    public double getMembership(double x) {
        if (x <= a || x >= d) {
            return 0.0;
        } else if (x >= b && x <= c) {
            return 1.0;
        } else if (x > a && x < b) {
            return (x - a) / (b - a);
        } else { // x > c && x < d
            return (d - x) / (d - c);
        }
    }

    public double getA() { return a; }
    public double getB() { return b; }
    public double getC() { return c; }
    public double getD() { return d; }
}
