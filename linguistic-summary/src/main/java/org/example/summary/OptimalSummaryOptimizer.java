package org.example.summary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Ranks and selects the optimal linguistic summaries from a pool
 * of generated candidates using a customizable weighted criteria scoring function.
 */
public class OptimalSummaryOptimizer {

    /**
     * Calculates the overall score for each summary, updates the DTO,
     * and returns the list sorted descending by their score.
     * @param summaries candidate list
     * @param weights map containing weight for each index 1..11. Sum of weights should ideally be 1.0.
     */
    public static List<LinguisticSummaryDTO> optimize(List<LinguisticSummaryDTO> summaries, Map<Integer, Double> weights) {
        List<LinguisticSummaryDTO> results = new ArrayList<>(summaries);

        for (LinguisticSummaryDTO dto : results) {
            double score = 0.0;
            double weightSum = 0.0;

            for (int i = 1; i <= 11; i++) {
                double weight = weights.getOrDefault(i, 0.0);
                score += dto.getMeasure(i) * weight;
                weightSum += weight;
            }

            if (weightSum > 0.0) {
                dto.setOverallScore(score / weightSum);
            } else {
                dto.setOverallScore(dto.getMeasure(1)); // Fallback to T1 (degree of truth)
            }
        }

        // Sort descending
        results.sort(Comparator.comparingDouble(LinguisticSummaryDTO::getOverallScore).reversed());
        return results;
    }
}
