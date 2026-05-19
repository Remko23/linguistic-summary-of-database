package org.example.fuzzy.quantifier;

import org.example.fuzzy.set.FuzzySet;

/**
 * Represents a relative fuzzy quantifier (e.g., "most", "almost all", "around 50%").
 * Defined over the range [0.0, 1.0] representing ratios of subsets.
 */
public class RelativeQuantifier extends Quantifier {

    public RelativeQuantifier(String name, FuzzySet fuzzySet) {
        super(name, fuzzySet);
    }
}
