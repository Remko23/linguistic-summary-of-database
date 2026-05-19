package org.example.fuzzy;

import org.example.fuzzy.set.ClassicSet;
import org.example.fuzzy.set.FuzzySet;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility to calculate attributes of fuzzy sets such as:
 * support, alpha-cut, normality, height.
 */
public class FuzzyProperties {

    /**
     * Calculates height of the fuzzy set by checking a dense discretization of continuous sets
     * or all discrete points.
     */
    public static double height(FuzzySet set) {
        ClassicSet universe = set.getUniverse();
        if (!universe.isContinuous()) {
            double max = 0.0;
            for (double val : universe.getDiscreteElements()) {
                max = Math.max(max, set.getMembership(val));
            }
            return max;
        } else {
            // Discretize continuous bounds to approximate maximum height
            double max = 0.0;
            double step = (universe.getMaxBound() - universe.getMinBound()) / 1000.0;
            for (double x = universe.getMinBound(); x <= universe.getMaxBound(); x += step) {
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
        ClassicSet universe = set.getUniverse();
        if (universe.isContinuous()) {
            // For continuous, we approximate the boundaries where membership > 0
            double start = universe.getMaxBound();
            double end = universe.getMinBound();
            double step = (universe.getMaxBound() - universe.getMinBound()) / 1000.0;
            
            for (double x = universe.getMinBound(); x <= universe.getMaxBound(); x += step) {
                if (set.getMembership(x) > 0.0) {
                    start = Math.min(start, x);
                    end = Math.max(end, x);
                }
            }
            if (start > end) return new ClassicSet(0, 0); // Empty support
            return new ClassicSet(start, end);
        } else {
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
        ClassicSet universe = set.getUniverse();
        if (universe.isContinuous()) {
            double start = universe.getMaxBound();
            double end = universe.getMinBound();
            double step = (universe.getMaxBound() - universe.getMinBound()) / 1000.0;
            
            for (double x = universe.getMinBound(); x <= universe.getMaxBound(); x += step) {
                if (set.getMembership(x) >= alpha) {
                    start = Math.min(start, x);
                    end = Math.max(end, x);
                }
            }
            if (start > end) return new ClassicSet(0, 0); // Empty
            return new ClassicSet(start, end);
        } else {
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
