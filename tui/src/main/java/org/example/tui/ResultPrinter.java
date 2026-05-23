package org.example.tui;

import org.example.summary.LinguisticSummaryDTO;

import java.util.List;

public class ResultPrinter {
    public static void printResults(List<LinguisticSummaryDTO> results) {
        if (results == null || results.isEmpty()) {
            System.out.println("No summaries generated.");
            return;
        }

        int maxLen = 20;
        for (LinguisticSummaryDTO dto : results) {
            maxLen = Math.max(maxLen, dto.getSummaryText().length());
        }

        String separator = "-".repeat(maxLen + 38);
        System.out.println(separator);
        System.out.printf("| %-" + maxLen + "s | %-10s | %-8s | %-8s |\n", "Summary Text", "Score", "T1", "T2");
        System.out.println(separator);
        for (LinguisticSummaryDTO dto : results) {
            System.out.printf("| %-" + maxLen + "s | %-10.4f | %-8.4f | %-8.4f |\n",
                    dto.getSummaryText(),
                    dto.getOverallScore(),
                    dto.getMeasure(1),
                    dto.getMeasure(2));
        }
        System.out.println(separator);
    }

    public static void saveToFile(List<LinguisticSummaryDTO> results, String filename) {
        if (results == null || results.isEmpty()) return;
        
        try (java.io.PrintWriter out = new java.io.PrintWriter(new java.io.FileWriter(filename))) {
            int maxLen = 20;
            for (LinguisticSummaryDTO dto : results) {
                maxLen = Math.max(maxLen, dto.getSummaryText().length());
            }

            String separator = "-".repeat(maxLen + 38);
            out.println(separator);
            out.printf("| %-" + maxLen + "s | %-10s | %-8s | %-8s |\n", "Summary Text", "Score", "T1", "T2");
            out.println(separator);
            for (LinguisticSummaryDTO dto : results) {
                out.printf("| %-" + maxLen + "s | %-10.4f | %-8.4f | %-8.4f |\n",
                        dto.getSummaryText(),
                        dto.getOverallScore(),
                        dto.getMeasure(1),
                        dto.getMeasure(2));
            }
            out.println(separator);
            System.out.println("Wyniki pomyślnie zapisano do pliku: " + filename);
        } catch (java.io.IOException e) {
            System.err.println("Błąd podczas zapisywania do pliku: " + e.getMessage());
        }
    }
}
