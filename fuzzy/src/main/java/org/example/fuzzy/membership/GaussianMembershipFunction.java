package org.example.fuzzy.membership;

import net.sourceforge.jFuzzyLogic.membership.MembershipFunctionGaussian;
import net.sourceforge.jFuzzyLogic.membership.Value;

public class GaussianMembershipFunction extends MembershipFunctionGaussian {

    public GaussianMembershipFunction(double mean, double sigma) {
        super(new Value(mean), new Value(sigma));
    }

    public double getMembership(double x) {
        return membership(x);
    }

    public double getMean() {
        return getParameter(0);
    }

    public double getSigma() {
        return getParameter(1);
    }
}
