package org.example.fuzzy.set;

import net.sourceforge.jFuzzyLogic.membership.MembershipFunction;
import net.sourceforge.jFuzzyLogic.membership.MembershipFunctionContinuous;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FuzzySet {
    private final ClassicSet universe;
    private final MembershipFunction membershipFunction;

    public FuzzySet(ClassicSet universe, MembershipFunction membershipFunction) {
        this.universe = universe;
        this.membershipFunction = membershipFunction;
    }

    public double getMembership(double x) {
        if (!universe.contains(x)) {
            return 0.0;
        }
        return membershipFunction.membership(x);
    }

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

    public boolean isEmpty() {
        if (universe.isContinuous()) {
            double step = (universe.getMaxBound() - universe.getMinBound()) / 500.0;
            if (step <= 0) return getMembership(universe.getMinBound()) <= 0.0;
            for (double x = universe.getMinBound(); x <= universe.getMaxBound(); x += step) {
                if (getMembership(x) > 0.0) return false;
            }
            return true;
        } else {
            if (universe.getDiscreteElements() == null) return true;
            for (double x : universe.getDiscreteElements()) {
                if (getMembership(x) > 0.0) return false;
            }
            return true;
        }
    }

    public boolean isConvex() {
        if (!universe.isContinuous()) {
            if (universe.getDiscreteElements() == null || universe.getDiscreteElements().isEmpty()) return true;
            List<Double> elements = new ArrayList<>(universe.getDiscreteElements());
            Collections.sort(elements);
            boolean goingDown = false;
            double prevVal = getMembership(elements.get(0));
            for (int i = 1; i < elements.size(); i++) {
                double val = getMembership(elements.get(i));
                if (val < prevVal - 1e-6) {
                    goingDown = true;
                } else if (val > prevVal + 1e-6 && goingDown) {
                    return false;
                }
                prevVal = val;
            }
            return true;
        } else {
            double step = (universe.getMaxBound() - universe.getMinBound()) / 500.0;
            if (step <= 0) return true;
            boolean goingDown = false;
            double prevVal = getMembership(universe.getMinBound());
            for (double x = universe.getMinBound() + step; x <= universe.getMaxBound(); x += step) {
                double val = getMembership(x);
                if (val < prevVal - 1e-6) {
                    goingDown = true;
                } else if (val > prevVal + 1e-6 && goingDown) {
                    return false;
                }
                prevVal = val;
            }
            return true;
        }
    }

    public double getHeight() {
        double max = 0.0;
        if (universe.isContinuous()) {
            double step = (universe.getMaxBound() - universe.getMinBound()) / 500.0;
            if (step <= 0) return getMembership(universe.getMinBound());
            for (double x = universe.getMinBound(); x <= universe.getMaxBound(); x += step) {
                max = Math.max(max, getMembership(x));
            }
        } else {
            if (universe.getDiscreteElements() == null) return 0.0;
            for (double x : universe.getDiscreteElements()) {
                max = Math.max(max, getMembership(x));
            }
        }
        return max;
    }

    public boolean isNormal() {
        return Math.abs(getHeight() - 1.0) < 1e-6;
    }

    public ClassicSet getSupport() {
        if (universe.isContinuous()) {
            double min = universe.getMaxBound();
            double max = universe.getMinBound();
            boolean found = false;
            double step = (universe.getMaxBound() - universe.getMinBound()) / 500.0;
            if (step <= 0) {
                 if (getMembership(universe.getMinBound()) > 0) return new ClassicSet(universe.getMinBound(), universe.getMaxBound());
                 return new ClassicSet(0, 0);
            }
            for (double x = universe.getMinBound(); x <= universe.getMaxBound(); x += step) {
                if (getMembership(x) > 0.0) {
                    min = Math.min(min, x);
                    max = Math.max(max, x);
                    found = true;
                }
            }
            if (!found) return new ClassicSet(0, 0);
            return new ClassicSet(min, max);
        } else {
            Set<Double> supp = new HashSet<>();
            if (universe.getDiscreteElements() != null) {
                for (double x : universe.getDiscreteElements()) {
                    if (getMembership(x) > 0.0) supp.add(x);
                }
            }
            return new ClassicSet(supp);
        }
    }

    public ClassicSet getAlphaCut(double alpha) {
        if (universe.isContinuous()) {
            double min = universe.getMaxBound();
            double max = universe.getMinBound();
            boolean found = false;
            double step = (universe.getMaxBound() - universe.getMinBound()) / 500.0;
            if (step <= 0) {
                 if (getMembership(universe.getMinBound()) >= alpha) return new ClassicSet(universe.getMinBound(), universe.getMaxBound());
                 return new ClassicSet(0, 0);
            }
            for (double x = universe.getMinBound(); x <= universe.getMaxBound(); x += step) {
                if (getMembership(x) >= alpha) {
                    min = Math.min(min, x);
                    max = Math.max(max, x);
                    found = true;
                }
            }
            if (!found) return new ClassicSet(0, 0);
            return new ClassicSet(min, max);
        } else {
            Set<Double> alphaCut = new HashSet<>();
            if (universe.getDiscreteElements() != null) {
                for (double x : universe.getDiscreteElements()) {
                    if (getMembership(x) >= alpha) alphaCut.add(x);
                }
            }
            return new ClassicSet(alphaCut);
        }
    }
}
