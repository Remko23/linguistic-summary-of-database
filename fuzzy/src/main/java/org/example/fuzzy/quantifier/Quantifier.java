package org.example.fuzzy.quantifier;

import org.example.fuzzy.set.FuzzySet;

/**
 * Base class for absolute and relative fuzzy quantifiers.
 * A quantifier is essentially a fuzzy set representing linguistic concepts (e.g. "most", "about 5").
 */
public abstract class Quantifier {
    protected final String name;
    protected final FuzzySet fuzzySet;

    public Quantifier(String name, FuzzySet fuzzySet) {
        this.name = name;
        this.fuzzySet = fuzzySet;
    }

    public String getName() {
        return name;
    }

    public FuzzySet getFuzzySet() {
        return fuzzySet;
    }

    /**
     * Calculates the membership degree for a given value (either a count or a ratio).
     */
    public double getMembership(double value) {
        return fuzzySet.getMembership(value);
    }
}
