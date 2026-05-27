package org.example.tui;

import org.example.database.DataEntity;
import org.example.database.JdbcRecordRepository;
import org.example.database.RecordRepository;
import org.example.fuzzy.LinguisticVariable;
import org.example.fuzzy.set.ClassicSet;
import org.example.fuzzy.membership.TriangularMembershipFunction;
import org.example.fuzzy.quantifier.Quantifier;
import org.example.fuzzy.quantifier.RelativeQuantifier;
import org.example.fuzzy.set.FuzzySet;
import org.example.summary.LinguisticSummaryDTO;
import org.example.summary.FuzzyStatement;
import org.example.summary.OptimalSummaryOptimizer;
import org.example.summary.SummaryGenerator;

import java.util.*;

public class ConsoleRunner {
    private final RecordRepository repository;
    private final SummaryGenerator generator;
    private final Scanner scanner;

    public ConsoleRunner() {
        this.repository = new JdbcRecordRepository();
        this.generator = new SummaryGenerator();
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("KSR Podsumowania Lingwistyczne");
        System.out.println("Komendy: 'test' 'exit'");

        while (true) {
            System.out.print("\nKSR> ");
            String cmd = scanner.nextLine().trim().toLowerCase();

            if (cmd.equals("exit")) {
                System.out.println("Wyłączanie...");
                break;
            } else if (cmd.equals("test")) {
                test();
            } else {
                System.out.println("Nieznana komenda: '" + cmd + "'. Wpisz 'test' lub 'exit'.");
            }
        }
    }

    private void test() {
        List<DataEntity> records = repository.getAllRecords();
        if (records.isEmpty()) {
            System.out.println("Brak rekordów.");
            return;
        }
        System.out.println("Załadowano " + records.size() + " rekordów.");

        System.out.println("Ładowanie zmiennych lingwistycznych z pliku FCL...");
        List<LinguisticVariable> allVars = org.example.fuzzy.FclLoader.loadLinguisticVariables("ksr.fcl");
        
        List<FuzzyStatement> summarizers = new ArrayList<>();
        List<Quantifier> quantifiers = new ArrayList<>();
        List<FuzzyStatement> qualifiers = new ArrayList<>();

        for (LinguisticVariable var : allVars) {
            String name = var.getAttributeName();
            if (name.equals("kwantyfikator_wzgledny")) {
                for (String label : var.getLabels()) {
                    quantifiers.add(new RelativeQuantifier(label, var.getLabelSet(label)));
                }
            } else if (name.equals("kwantyfikator_bezwzgledny")) {
                for (String label : var.getLabels()) {
                    quantifiers.add(new org.example.fuzzy.quantifier.AbsoluteQuantifier(label, var.getLabelSet(label)));
                }
            } else {
                for (String label : var.getLabels()) {
                    FuzzyStatement stmt = new FuzzyStatement(name, label, var.getLabelSet(label));
                    summarizers.add(stmt);
                    
                    if (name.equals("a_s") && label.equals("subiektywny")) {
                        qualifiers.add(stmt);
                    }
                }
            }
        }

        List<FuzzyStatement> activeSummarizers = new ArrayList<>();
        for (FuzzyStatement var : summarizers) {
            if (var.getAttributeName().equals("s") || var.getAttributeName().equals("a_s")) {
                activeSummarizers.add(var);
            }
        }

        System.out.println("Generowanie podsumowan...");
        List<LinguisticSummaryDTO> summaries = generator.generateSingleSubject(records, quantifiers, qualifiers, activeSummarizers);

        Map<Integer, Double> weights = new HashMap<>();
        for (int i = 1; i <= 11; i++) {
            weights.put(i, 1.0);
        }
        summaries = OptimalSummaryOptimizer.optimize(summaries, weights);

        List<LinguisticSummaryDTO> topSummaries = summaries.size() > 20 ? summaries.subList(0, 20) : summaries;

        ResultPrinter.printResults(topSummaries);
        ResultPrinter.saveToFile(topSummaries, "wyniki_podsumowan.txt");
    }
}
