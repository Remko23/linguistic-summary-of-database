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
        System.out.println("      KSR Linguistic Summary - TUI MainMenu       ");
        System.out.println("==================================================");
        System.out.println("Available commands: 'test' 'exit'");

        while (true) {
            System.out.print("\nKSR-TUI> ");
            String cmd = scanner.nextLine().trim().toLowerCase();

            if (cmd.equals("exit")) {
                System.out.println("Exiting Console Runner...");
                break;
            } else if (cmd.equals("test")) {
                test();
            } else {
                System.out.println("Unknown command: '" + cmd + "'. Try 'test' or 'exit'.");
            }
        }
    }

    private void test() {
        System.out.println("yo");
    }
}
