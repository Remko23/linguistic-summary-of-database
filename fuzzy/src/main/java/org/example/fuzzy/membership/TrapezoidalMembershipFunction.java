package org.example.fuzzy.membership;

import net.sourceforge.jFuzzyLogic.membership.MembershipFunctionTrapetzoidal;
import net.sourceforge.jFuzzyLogic.membership.Value;

public class TrapezoidalMembershipFunction extends MembershipFunctionTrapetzoidal {

    public TrapezoidalMembershipFunction(double a, double b, double c, double d) {
        super(new Value(a), new Value(b), new Value(c), new Value(d));
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

    public double getD() {
        return getParameter(3);
    }
}
