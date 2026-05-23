package org.example.summary;

import org.example.fuzzy.set.FuzzySet;

public class FuzzyStatement {
    private final String attributeName;
    private final String label;
    private final FuzzySet fuzzySet;

    public FuzzyStatement(String attributeName, String label, FuzzySet fuzzySet) {
        this.attributeName = attributeName;
        this.label = label;
        this.fuzzySet = fuzzySet;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getLabel() {
        return label;
    }

    public FuzzySet getFuzzySet() {
        return fuzzySet;
    }
}
