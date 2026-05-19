package org.example.fuzzy.membership;

/**
 * Implements a triangular membership function.
 * Defined by three parameters: a (left foot), b (peak), c (right foot).
 */
public class TriangularMembershipFunction implements MembershipFunction {
    private final double a;
    private final double b;
    private final double c;

    public TriangularMembershipFunction(double a, double b, double c) {
        if (a > b || b > c) {
            throw new IllegalArgumentException("Parameters must satisfy a <= b <= c");
        }
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public double getMembership(double x) {
        if (x <= a || x >= c) {
            return 0.0;
        } else if (x == b) {
            return 1.0;
        } else if (x > a && x < b) {
            return (x - a) / (b - a);
        } else { // x > b && x < c
            return (c - x) / (c - b);
        }
    }

    public double getA() { return a; }
    public double getB() { return b; }
    public double getC() { return c; }
}
