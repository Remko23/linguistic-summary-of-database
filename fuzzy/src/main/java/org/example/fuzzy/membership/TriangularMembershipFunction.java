package org.example.fuzzy.membership;

import net.sourceforge.jFuzzyLogic.membership.MembershipFunctionTriangular;
import net.sourceforge.jFuzzyLogic.membership.Value;

public class TriangularMembershipFunction extends MembershipFunctionTriangular {
    
    public TriangularMembershipFunction(double a, double b, double c) {
        super(new Value(a), new Value(b), new Value(c));
    }

    public double getMembership(double x) {
        return membership(x);
    }

    public double getA() {
        return getParameter(0);
    }

    public double getB() {
        return getParameter(1);
    }

    public double getC() {
        return getParameter(2);
    }
}
