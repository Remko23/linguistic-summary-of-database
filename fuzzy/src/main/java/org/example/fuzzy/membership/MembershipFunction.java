package org.example.fuzzy.membership;

/**
 * Interface representing a mathematical membership function for a fuzzy set.
 */
public interface MembershipFunction {
    
    /**
     * Calculates the degree of membership for a given value x.
     * @param x the crisp value to evaluate
     * @return membership degree in the range [0.0, 1.0]
     */
    double getMembership(double x);
}
