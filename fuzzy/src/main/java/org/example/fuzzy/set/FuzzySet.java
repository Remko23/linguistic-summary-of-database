package org.example.fuzzy.set;

import net.sourceforge.jFuzzyLogic.membership.MembershipFunction;
import net.sourceforge.jFuzzyLogic.membership.MembershipFunctionContinuous;

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
        return membershipFunction.membership(x);
    }

    /**
     * Performs standard fuzzy union (using Zadeh's max T-conorm).
     */
    public FuzzySet union(FuzzySet other) {
        return new FuzzySet(this.universe, new MembershipFunctionContinuous() {
            @Override
            public double membership(double x) {
                return Math.max(FuzzySet.this.getMembership(x), other.getMembership(x));
            }
            @Override
            public boolean checkParamters(StringBuffer sb) { 
                return true; 
            }
            @Override
            public void estimateUniverse() {}
        });
    }

    /**
     * Performs standard fuzzy intersection (using Zadeh's min T-norm).
     */
    public FuzzySet intersect(FuzzySet other) {
        return new FuzzySet(this.universe, new MembershipFunctionContinuous() {
            @Override
            public double membership(double x) {
                return Math.min(FuzzySet.this.getMembership(x), other.getMembership(x));
            }
            @Override
            public boolean checkParamters(StringBuffer sb) { 
                return true; 
            }
            @Override
            public void estimateUniverse() {}
        });
    }

    /**
     * Performs standard fuzzy complement.
     */
    public FuzzySet complement() {
        return new FuzzySet(this.universe, new MembershipFunctionContinuous() {
            @Override
            public double membership(double x) {
                return 1.0 - FuzzySet.this.getMembership(x);
            }
            @Override
            public boolean checkParamters(StringBuffer sb) { 
                return true; 
            }
            @Override
            public void estimateUniverse() {}
        });
    }

    public ClassicSet getUniverse() {
        return universe;
    }

    public MembershipFunction getMembershipFunction() {
        return membershipFunction;
    }
}
