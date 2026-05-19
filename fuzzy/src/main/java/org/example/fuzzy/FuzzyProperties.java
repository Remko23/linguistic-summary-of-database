package org.example.fuzzy;

import org.example.fuzzy.set.ClassicSet;
import org.example.fuzzy.set.FuzzySet;
import net.sourceforge.jFuzzyLogic.membership.MembershipFunction;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility to calculate attributes of fuzzy sets such as support, alpha-cut, normality, and height.
 * Leverages jFuzzyLogic's MembershipFunction properties natively.
 */
public class FuzzyProperties {

    /**
     * Calculates height of the fuzzy set.
     */
    public static double height(FuzzySet set) {
        MembershipFunction mf = set.getMembershipFunction();
        if (mf.isDiscrete()) {
            ClassicSet universe = set.getUniverse();
            double max = 0.0;
            for (double val : universe.getDiscreteElements()) {
                max = Math.max(max, set.getMembership(val));
            }
            return max;
        } else {
            mf.estimateUniverse();
            double uMin = mf.getUniverseMin();
            double uMax = mf.getUniverseMax();
            
            // Fall back to ClassicSet bounds if jFuzzyLogic bounds are uninitialized
            if (Double.isNaN(uMin) || Double.isNaN(uMax) || (uMin == 0.0 && uMax == 0.0)) {
                uMin = set.getUniverse().getMinBound();
                uMax = set.getUniverse().getMaxBound();
            }
            
            double max = 0.0;
            double step = (uMax - uMin) / 1000.0;
            for (double x = uMin; x <= uMax; x += step) {
                max = Math.max(max, set.getMembership(x));
            }
            return max;
        }
    }

    /**
     * Determines if a fuzzy set is normal (height equals 1.0).
     */
    public static boolean isNormal(FuzzySet set) {
        return Math.abs(height(set) - 1.0) < 1e-6;
    }

    /**
     * Calculates the support (nośnik) of a fuzzy set as a ClassicSet.
     * Elements where membership > 0.
     */
    public static ClassicSet support(FuzzySet set) {
        MembershipFunction mf = set.getMembershipFunction();
        if (!mf.isDiscrete()) {
            mf.estimateUniverse();
            double uMin = mf.getUniverseMin();
            double uMax = mf.getUniverseMax();
            
            if (Double.isNaN(uMin) || Double.isNaN(uMax) || (uMin == 0.0 && uMax == 0.0)) {
                uMin = set.getUniverse().getMinBound();
                uMax = set.getUniverse().getMaxBound();
            }
            
            double start = uMax;
            double end = uMin;
            double step = (uMax - uMin) / 1000.0;
            
            for (double x = uMin; x <= uMax; x += step) {
                if (set.getMembership(x) > 0.0) {
                    start = Math.min(start, x);
                    end = Math.max(end, x);
                }
            }
            if (start > end) return new ClassicSet(0, 0);
            return new ClassicSet(start, end);
        } else {
            ClassicSet universe = set.getUniverse();
            Set<Double> elements = new HashSet<>();
            for (double val : universe.getDiscreteElements()) {
                if (set.getMembership(val) > 0.0) {
                    elements.add(val);
                }
            }
            return new ClassicSet(elements);
        }
    }

    /**
     * Calculates the alpha-cut of a fuzzy set as a ClassicSet.
     * Elements where membership >= alpha.
     */
    public static ClassicSet alphaCut(FuzzySet set, double alpha) {
        MembershipFunction mf = set.getMembershipFunction();
        if (!mf.isDiscrete()) {
            mf.estimateUniverse();
            double uMin = mf.getUniverseMin();
            double uMax = mf.getUniverseMax();
            
            if (Double.isNaN(uMin) || Double.isNaN(uMax) || (uMin == 0.0 && uMax == 0.0)) {
                uMin = set.getUniverse().getMinBound();
                uMax = set.getUniverse().getMaxBound();
            }
            
            double start = uMax;
            double end = uMin;
            double step = (uMax - uMin) / 1000.0;
            
            for (double x = uMin; x <= uMax; x += step) {
                if (set.getMembership(x) >= alpha) {
                    start = Math.min(start, x);
                    end = Math.max(end, x);
                }
            }
            if (start > end) return new ClassicSet(0, 0);
            return new ClassicSet(start, end);
        } else {
            ClassicSet universe = set.getUniverse();
            Set<Double> elements = new HashSet<>();
            for (double val : universe.getDiscreteElements()) {
                if (set.getMembership(val) >= alpha) {
                    elements.add(val);
                }
            }
            return new ClassicSet(elements);
        }
    }
}
