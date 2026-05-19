package org.example.summary;

import org.example.database.DataEntity;
import org.example.fuzzy.LinguisticVariable;
import org.example.fuzzy.quantifier.Quantifier;
import org.example.fuzzy.set.FuzzySet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Generator engine combining database records, fuzzy quantifiers,
 * qualifiers, and summarizers to produce candidate linguistic summaries.
 */
public class SummaryGenerator {

    /**
     * Generates a list of single-subject linguistic summaries.
     * Combines all quantifiers, optional qualifiers, and summarizers.
     */
    public List<LinguisticSummaryDTO> generateSingleSubject(
            List<DataEntity> records,
            List<Quantifier> quantifiers,
            List<FuzzySet> qualifiers,
            List<LinguisticVariable> summarizers) {

        List<LinguisticSummaryDTO> results = new ArrayList<>();

        for (Quantifier q : quantifiers) {
            // Generuje z kwalifikatorami oraz bez (kwalifikator = null)
            List<FuzzySet> activeQualifiers = new ArrayList<>(qualifiers);
            activeQualifiers.add(null);

            for (FuzzySet qual : activeQualifiers) {
                for (LinguisticVariable sumVar : summarizers) {
                    for (String label : sumVar.getLabels()) {
                        FuzzySet sumSet = sumVar.getLabelSet(label);
                        List<FuzzySet> sumList = Collections.singletonList(sumSet);

                        // Budowanie zdania
                        String text = buildSentence(q.getName(), qual, sumVar.getAttributeName(), label);
                        LinguisticSummaryDTO dto = new LinguisticSummaryDTO(text);

                        // Obliczanie miar jakości T1 - T11
                        dto.setMeasure(1, QualityEvaluator.evaluateT1(records, q, qual, sumList));
                        dto.setMeasure(2, QualityEvaluator.evaluateT2(sumList));
                        dto.setMeasure(3, QualityEvaluator.evaluateT3(records, qual, sumList));
                        dto.setMeasure(4, QualityEvaluator.evaluateT4(sumList));
                        dto.setMeasure(5, QualityEvaluator.evaluateT5(sumList));
                        dto.setMeasure(6, QualityEvaluator.evaluateT6(q));
                        dto.setMeasure(7, QualityEvaluator.evaluateT7(q));
                        dto.setMeasure(8, QualityEvaluator.evaluateT8(sumList));
                        dto.setMeasure(9, QualityEvaluator.evaluateT9(sumList));
                        dto.setMeasure(10, QualityEvaluator.evaluateT10(qual));
                        dto.setMeasure(11, QualityEvaluator.evaluateT11(qual));

                        results.add(dto);
                    }
                }
            }
        }
        return results;
    }

    /**
     * Generates multi-subject summaries comparing two different target groups.
     * E.g. "More young clients than older clients have high incomes".
     */
    public List<LinguisticSummaryDTO> generateMultiSubject(
            List<DataEntity> group1,
            List<DataEntity> group2,
            String group1Name,
            String group2Name,
            List<Quantifier> quantifiers,
            List<LinguisticVariable> summarizers) {

        List<LinguisticSummaryDTO> results = new ArrayList<>();

        for (Quantifier q : quantifiers) {
            for (LinguisticVariable sumVar : summarizers) {
                for (String label : sumVar.getLabels()) {
                    FuzzySet sumSet = sumVar.getLabelSet(label);
                    List<FuzzySet> sumList = Collections.singletonList(sumSet);

                    // Multi-subject comparison text
                    String text = String.format("%s clients from group %s compared to %s are having attribute %s: %s",
                            q.getName(), group1Name, group2Name, sumVar.getAttributeName(), label);
                    
                    LinguisticSummaryDTO dto = new LinguisticSummaryDTO(text);

                    // Compute basic truth values for comparative set evaluations
                    double t1 = QualityEvaluator.evaluateT1(group1, q, null, sumList);
                    double t2 = QualityEvaluator.evaluateT1(group2, q, null, sumList);

                    dto.setMeasure(1, Math.max(t1, t2));
                    dto.setMeasure(2, QualityEvaluator.evaluateT2(sumList));
                    dto.setMeasure(3, 0.5); // Default placeholder metric for complex multi-subject properties
                    dto.setMeasure(4, QualityEvaluator.evaluateT4(sumList));
                    dto.setMeasure(5, QualityEvaluator.evaluateT5(sumList));
                    dto.setMeasure(6, QualityEvaluator.evaluateT6(q));
                    dto.setMeasure(7, QualityEvaluator.evaluateT7(q));
                    dto.setMeasure(8, QualityEvaluator.evaluateT8(sumList));
                    dto.setMeasure(9, QualityEvaluator.evaluateT9(sumList));
                    dto.setMeasure(10, 0.0);
                    dto.setMeasure(11, 0.0);

                    results.add(dto);
                }
            }
        }
        return results;
    }

    // Helper: forms linguistic sentence templates
    private String buildSentence(String quantifier, FuzzySet qualifier, String attribute, String label) {
        if (qualifier == null) {
            return String.format("%s of database records have attribute %s: %s.", 
                    quantifier, attribute, label);
        } else {
            return String.format("%s of database records who are qualifying as matching requirements have attribute %s: %s.",
                    quantifier, attribute, label);
        }
    }
}
