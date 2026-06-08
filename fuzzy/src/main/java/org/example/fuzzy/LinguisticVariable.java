package org.example.fuzzy;

import net.sourceforge.jFuzzyLogic.rule.Variable;
import net.sourceforge.jFuzzyLogic.rule.LinguisticTerm;
import org.example.fuzzy.set.FuzzySet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LinguisticVariable {
    private final Variable internalVariable;
    private final Map<String, FuzzySet> labels;

    public LinguisticVariable(String attributeName) {
        this.internalVariable = new Variable(attributeName);
        this.labels = new HashMap<>();
    }

    public void addLabel(String labelName, FuzzySet set) {
        labels.put(labelName, set);
        LinguisticTerm term = new LinguisticTerm(labelName, set.getMembershipFunction());
        internalVariable.add(term);
    }

    public String getAttributeName() {
        return internalVariable.getName();
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

    public Variable getInternalVariable() {
        return internalVariable;
    }
}
