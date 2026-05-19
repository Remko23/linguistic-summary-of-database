package org.example.fuzzy.set;

import org.example.fuzzy.membership.MembershipFunction;

/**
 * Represents a Fuzzy Set defined over a universe of discourse (ClassicSet)
 * with a specific MembershipFunction.
 */
public class FuzzySet {
    private final ClassicSet universe;
    private final MembershipFunction membershipFunction;

    public FuzzySet(ClassicSet universe, MembershipFunction membershipFunction) {
        this.universe = universe;
        this.membershipFunction = membershipFunction;
    }

    /**
     * Gets membership value, ensuring it stays within the universe limits.
     */
    public double getMembership(double x) {
        if (!universe.contains(x)) {
            return 0.0;
        }
        return membershipFunction.getMembership(x);
    }

    /**
     * Performs standard fuzzy union (using Zadeh's max T-conorm).
     */
    public FuzzySet union(FuzzySet other) {
        return new FuzzySet(this.universe, x -> Math.max(this.getMembership(x), other.getMembership(x)));
    }

    /**
     * Performs standard fuzzy intersection (using Zadeh's min T-norm).
     */
    public FuzzySet intersect(FuzzySet other) {
        return new FuzzySet(this.universe, x -> Math.min(this.getMembership(x), other.getMembership(x)));
    }

    /**
     * Performs standard fuzzy complement.
     */
    public FuzzySet complement() {
        return new FuzzySet(this.universe, x -> 1.0 - this.getMembership(x));
    }

    public ClassicSet getUniverse() {
        return universe;
    }

    public MembershipFunction getMembershipFunction() {
        return membershipFunction;
    }
}
