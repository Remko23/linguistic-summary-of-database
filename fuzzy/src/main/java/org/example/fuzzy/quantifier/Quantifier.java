package org.example.fuzzy.quantifier;

import org.example.fuzzy.set.FuzzySet;

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

    public double getMembership(double value) {
        return fuzzySet.getMembership(value);
    }
}
