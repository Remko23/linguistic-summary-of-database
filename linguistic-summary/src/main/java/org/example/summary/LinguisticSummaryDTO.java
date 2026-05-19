package org.example.summary;

import java.util.HashMap;
import java.util.Map;

/**
 * Data Transfer Object encapsulating a single generated linguistic summary.
 * It contains the textual statement, the individual quality measures (T1 to T11),
 * and the aggregated weighted quality score.
 */
public class LinguisticSummaryDTO {
    private final String summaryText;
    private final Map<Integer, Double> qualityMeasures;
    private double overallScore;

    public LinguisticSummaryDTO(String summaryText) {
        this.summaryText = summaryText;
        this.qualityMeasures = new HashMap<>();
        this.overallScore = 0.0;
    }

    public String getSummaryText() {
        return summaryText;
    }

    public void setMeasure(int index, double value) {
        if (index < 1 || index > 11) {
            throw new IllegalArgumentException("Quality measures are index 1 to 11");
        }
        qualityMeasures.put(index, value);
    }

    public double getMeasure(int index) {
        return qualityMeasures.getOrDefault(index, 0.0);
    }

    public Map<Integer, Double> getQualityMeasures() {
        return new HashMap<>(qualityMeasures);
    }

    public double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(double overallScore) {
        this.overallScore = overallScore;
    }

    @Override
    public String toString() {
        return String.format("Summary: \"%s\" (Score: %.4f, T1: %.2f, T2: %.2f)", 
                summaryText, overallScore, getMeasure(1), getMeasure(2));
    }
}
