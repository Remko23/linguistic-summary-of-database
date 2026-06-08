package org.example.summary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class OptimalSummaryOptimizer {
    public static List<LinguisticSummaryDTO> optimize(List<LinguisticSummaryDTO> summaries,
            Map<Integer, Double> weights) {
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
                dto.setOverallScore(dto.getMeasure(1));
            }
        }

        results.sort(Comparator.comparingDouble(LinguisticSummaryDTO::getOverallScore).reversed());
        return results;
    }
}
