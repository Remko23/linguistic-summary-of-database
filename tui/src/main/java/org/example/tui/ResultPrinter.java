package org.example.tui;

import org.example.summary.LinguisticSummaryDTO;

import java.util.List;

/**
 * Standard utility to print linguistic summaries in a readable tabular format.
 */
public class ResultPrinter {

    /**
     * Prints summaries with details.
     */
    public static void printResults(List<LinguisticSummaryDTO> results) {
        if (results == null || results.isEmpty()) {
            System.out.println("No summaries generated.");
            return;
        }

        System.out.println("------------------------------------------------------------------------------------------------------");
        System.out.printf("| %-60s | %-10s | %-8s | %-8s |\n", "Summary Text", "Score", "T1", "T2");
        System.out.println("------------------------------------------------------------------------------------------------------");
        for (LinguisticSummaryDTO dto : results) {
            System.out.printf("| %-60s | %-10.4f | %-8.4f | %-8.4f |\n",
                    truncate(dto.getSummaryText(), 60),
                    dto.getOverallScore(),
                    dto.getMeasure(1),
                    dto.getMeasure(2));
        }
        System.out.println("------------------------------------------------------------------------------------------------------");
    }

    private static String truncate(String text, int length) {
        if (text.length() <= length) return text;
        return text.substring(0, length - 3) + "...";
    }
}
