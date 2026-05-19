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
import org.example.summary.OptimalSummaryOptimizer;
import org.example.summary.SummaryGenerator;

import java.util.*;

/**
 * Handles terminal commands to trigger test scenarios, loading records
 * and evaluating summaries interactively without starting the JavaFX GUI.
 */
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
        System.out.println("==================================================");
        System.out.println(" KSR Linguistic Summary - Console Runner Started");
        System.out.println("==================================================");
        System.out.println("Available commands: 'generate', 'exit'");

        while (true) {
            System.out.print("\nKSR-TUI> ");
            String cmd = scanner.nextLine().trim().toLowerCase();

            if (cmd.equals("exit")) {
                System.out.println("Exiting Console Runner...");
                break;
            } else if (cmd.equals("generate")) {
                runDemoScenario();
            } else {
                System.out.println("Unknown command: '" + cmd + "'. Try 'generate' or 'exit'.");
            }
        }
    }

    private void runDemoScenario() {
        System.out.println("Fetching records from database...");
        List<DataEntity> records = repository.getAllRecords();
        System.out.println("Loaded " + records.size() + " records successfully.");

        // Define a relative quantifier "Most"
        ClassicSet relUniv = new ClassicSet(0.0, 1.0);
        TriangularMembershipFunction triMost = new TriangularMembershipFunction(0.5, 0.8, 1.0);
        FuzzySet fuzzyMost = new FuzzySet(relUniv, triMost);
        Quantifier most = new RelativeQuantifier("Most", fuzzyMost);

        // Define a linguistic variable "income"
        ClassicSet incUniv = new ClassicSet(0.0, 20000.0);
        TriangularMembershipFunction triHigh = new TriangularMembershipFunction(8000.0, 15000.0, 20000.0);
        FuzzySet fuzzyHigh = new FuzzySet(incUniv, triHigh);
        LinguisticVariable incomeVar = new LinguisticVariable("income");
        incomeVar.addLabel("High", fuzzyHigh);

        List<Quantifier> quantifiers = Collections.singletonList(most);
        List<LinguisticVariable> summarizers = Collections.singletonList(incomeVar);

        System.out.println("Generating summaries...");
        List<LinguisticSummaryDTO> list = generator.generateSingleSubject(
                records, quantifiers, new ArrayList<>(), summarizers);

        // Optimize with default weights
        Map<Integer, Double> weights = new HashMap<>();
        weights.put(1, 0.5); // T1: 50% importance
        weights.put(2, 0.5); // T2: 50% importance
        List<LinguisticSummaryDTO> optimized = OptimalSummaryOptimizer.optimize(list, weights);

        System.out.println("\nGenerated & Ranked Summaries:");
        ResultPrinter.printResults(optimized);
    }
}
