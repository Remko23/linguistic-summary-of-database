package org.example.fuzzy.quantifier;

import org.example.fuzzy.set.FuzzySet;

/**
 * Represents an absolute fuzzy quantifier (e.g., "about 5", "at least 10").
 * Defined over absolute counts of elements.
 */
public class AbsoluteQuantifier extends Quantifier {

    public AbsoluteQuantifier(String name, FuzzySet fuzzySet) {
        super(name, fuzzySet);
    }
}
