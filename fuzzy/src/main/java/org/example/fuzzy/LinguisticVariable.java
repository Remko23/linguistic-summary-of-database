package org.example.fuzzy;

import org.example.fuzzy.set.FuzzySet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Binds a specific database attribute (e.g. "age") to a set of
 * linguistic labels (e.g. "young", "middle-aged", "elderly").
 * Each label maps to a corresponding FuzzySet definition.
 */
public class LinguisticVariable {
    private final String attributeName;
    private final Map<String, FuzzySet> labels;

    public LinguisticVariable(String attributeName) {
        this.attributeName = attributeName;
        this.labels = new HashMap<>();
    }

    /**
     * Adds a linguistic label with its associated FuzzySet.
     */
    public void addLabel(String labelName, FuzzySet set) {
        labels.put(labelName, set);
    }

    public String getAttributeName() {
        return attributeName;
    }

    public FuzzySet getLabelSet(String labelName) {
        return labels.get(labelName);
    }

    public Set<String> getLabels() {
        return labels.keySet();
    }

    public Map<String, FuzzySet> getLabelsMap() {
        return new HashMap<>(labels);
    }
}
