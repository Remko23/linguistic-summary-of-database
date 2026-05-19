package org.example.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.database.DataEntity;
import org.example.database.JdbcRecordRepository;
import org.example.summary.LinguisticSummaryDTO;
import org.example.summary.SummaryGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for the main GUI window.
 * Binds UI components defined in main_window.fxml with business logic.
 */
public class MainWindowController {

    @FXML
    private Label statusLabel;

    @FXML
    private TextArea outputArea;

    @FXML
    private TableView<?> summaryTableView;

    @FXML
    private Slider t1WeightSlider;

    /**
     * Initializes the controller class. Automatically called after FXML load.
     */
    @FXML
    public void initialize() {
        statusLabel.setText("System ready. Click the button to test!");
    }

    /**
     * Event handler for the Hello World demonstration button.
     */
    @FXML
    private void handleHelloAction(ActionEvent event) {
        statusLabel.setText("Hello World action triggered successfully!");
        
        outputArea.setText("==================================================\n" +
                           "  Hello KSR - Linguistic Summary of Database!     \n" +
                           "==================================================\n" +
                           "This is a demonstration of the JavaFX Scene Builder template.\n" +
                           "The JavaFX UI is correctly hooked up to this controller!\n\n" +
                           "Click 'Generate Test Summaries' to load mock data and run the generator engine.");
    }

    /**
     * Event handler to run the back-end summary generator as a live test.
     */
    @FXML
    private void handleGenerateAction(ActionEvent event) {
        statusLabel.setText("Running linguistic summary generator...");
        
        try {
            JdbcRecordRepository repo = new JdbcRecordRepository();
            List<DataEntity> records = repo.getAllRecords(); // Will load mock data if DB is offline

            outputArea.appendText("\n\nSuccessfully loaded " + records.size() + " data records.\n");
            outputArea.appendText("Evaluating attributes using fuzzy logic...\n");

            // Simple demonstration result text output
            outputArea.appendText("\nDemo generation completed. Results can be populated here.");
            statusLabel.setText("Generation complete!");
            
        } catch (Exception e) {
            statusLabel.setText("Error during generation: " + e.getMessage());
            outputArea.appendText("\nError: " + e.getMessage());
        }
    }
}
