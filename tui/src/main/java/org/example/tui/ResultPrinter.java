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

        String header = buildHeader(maxLen);
        String separator = "-".repeat(header.length());

        System.out.println(separator);
        System.out.println(header);
        System.out.println(separator);
        for (LinguisticSummaryDTO dto : results) {
            System.out.println(buildRow(dto, maxLen));
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

            String header = buildHeader(maxLen);
            String separator = "-".repeat(header.length());

            out.println(separator);
            out.println(header);
            out.println(separator);
            for (LinguisticSummaryDTO dto : results) {
                out.println(buildRow(dto, maxLen));
            }
            out.println(separator);
            System.out.println("Wyniki pomyślnie zapisano do pliku: " + filename);
        } catch (java.io.IOException e) {
            System.err.println("Błąd podczas zapisywania do pliku: " + e.getMessage());
        }
    }

    private static String buildHeader(int maxLen) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("| %-" + maxLen + "s | %-8s ", "Podsumowanie", "T"));
        for (int i = 1; i <= 11; i++) {
            sb.append(String.format("| %-6s ", "T" + i));
        }
        sb.append("|");
        return sb.toString();
    }

    private static String buildRow(LinguisticSummaryDTO dto, int maxLen) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("| %-" + maxLen + "s | %-8.4f ", dto.getSummaryText(), dto.getOverallScore()));
        for (int i = 1; i <= 11; i++) {
            sb.append(String.format("| %-6.4f ", dto.getMeasure(i)));
        }
        sb.append("|");
        return sb.toString();
    }
}
