package org.example.fuzzy.set;

import java.util.Set;

/**
 * Represents a classical (crisp) set, defining a space of discourse or explicit subsets.
 */
public class ClassicSet {
    private final double minBound;
    private final double maxBound;
    private final Set<Double> discreteElements;
    private final boolean isContinuous;

    public ClassicSet(double minBound, double maxBound) {
        this.minBound = minBound;
        this.maxBound = maxBound;
        this.discreteElements = null;
        this.isContinuous = true;
    }

    public ClassicSet(Set<Double> discreteElements) {
        this.minBound = 0.0;
        this.maxBound = 0.0;
        this.discreteElements = discreteElements;
        this.isContinuous = false;
    }

    public boolean contains(double x) {
        if (isContinuous) {
            return x >= minBound && x <= maxBound;
        } else {
            return discreteElements != null && discreteElements.contains(x);
        }
    }

    public double getMinBound() { return minBound; }
    public double getMaxBound() { return maxBound; }
    public Set<Double> getDiscreteElements() { return discreteElements; }
    public boolean isContinuous() { return isContinuous; }
}
