package org.example.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import org.example.database.DataEntity;
import org.example.database.JdbcRecordRepository;
import org.example.database.RecordRepository;
import org.example.fuzzy.FclLoader;
import org.example.fuzzy.LinguisticVariable;
import org.example.fuzzy.membership.GaussianMembershipFunction;
import org.example.fuzzy.membership.TrapezoidalMembershipFunction;
import org.example.fuzzy.membership.TriangularMembershipFunction;
import org.example.fuzzy.quantifier.AbsoluteQuantifier;
import org.example.fuzzy.quantifier.Quantifier;
import org.example.fuzzy.quantifier.RelativeQuantifier;
import org.example.fuzzy.set.ClassicSet;
import org.example.fuzzy.set.FuzzySet;
import org.example.summary.FuzzyStatement;
import org.example.summary.LinguisticSummaryDTO;
import org.example.summary.OptimalSummaryOptimizer;
import org.example.summary.SummaryGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class MainWindowController {

    @FXML
    private ToggleButton viewToggle;
    @FXML
    private VBox basicView;
    @FXML
    private VBox advancedView;
    @FXML
    private VBox quantifierCheckboxes;
    @FXML
    private VBox summarizerCheckboxes;
    @FXML
    private VBox qualifierCheckboxes;
    @FXML
    private VBox weightsContainer;
    @FXML
    private ComboBox<String> sortCombo;
    @FXML
    private TableView<SummaryRow> summaryTable;
    @FXML
    private CheckBox selectAllCheckbox;
    @FXML
    private Label resultCountLabel;
    @FXML
    private ComboBox<LabelDefinition.LabelType> advTypeCombo;
    @FXML
    private TextField advAttributeField;
    @FXML
    private TextField advLabelField;
    @FXML
    private ComboBox<LabelDefinition.MfType> advMfCombo;
    @FXML
    private VBox advParamsContainer;
    @FXML
    private TextField advUnivMinField;
    @FXML
    private TextField advUnivMaxField;
    @FXML
    private TableView<LabelDefinition> labelsTable;

    @FXML
    private Label statusLabel;

    private final RecordRepository repository = new JdbcRecordRepository();
    private final SummaryGenerator generator = new SummaryGenerator();
    private List<LinguisticVariable> allVariables = new ArrayList<>();

    private final Map<CheckBox, Quantifier> quantifierMap = new LinkedHashMap<>();
    private final Map<CheckBox, FuzzyStatement> summarizerMap = new LinkedHashMap<>();
    private final Map<CheckBox, FuzzyStatement> qualifierMap = new LinkedHashMap<>();
    private final Slider[] weightSliders = new Slider[11];

    private final ObservableList<SummaryRow> summaryRows = FXCollections.observableArrayList();
    private final ObservableList<LabelDefinition> labelDefinitions = FXCollections.observableArrayList();
    private final List<TextField> advParamFields = new ArrayList<>();

    private static final String[] MEASURE_NAMES = {
            "T1 – Stopień prawdziwości",
            "T2 – Stopień nieprecyzyjności",
            "T3 – Stopień pokrycia",
            "T4 – Trafność",
            "T5 – Długość podsumowania",
            "T6 – Stopień nieprecyzyjności kwantyfikatora",
            "T7 – Stopień kardynalności kwantyfikatora",
            "T8 – Stopień kardynalności sumaryzatora",
            "T9 – Stopień nieprecyzyjności kwalifikatora",
            "T10 – Stopień kardynalności kwalifikatora",
            "T11 – Długość kwalifikatora"
    };

    private static final Map<String, String> ATTR_DISPLAY_NAMES = new LinkedHashMap<>();
    static {
        ATTR_DISPLAY_NAMES.put("a_r", "Atrakcyjność wizualna");
        ATTR_DISPLAY_NAMES.put("a_h", "Bogactwo źródeł");
        ATTR_DISPLAY_NAMES.put("t_r", "Unikalność słów");
        ATTR_DISPLAY_NAMES.put("w_l", "Średnia dł. słowa");
        ATTR_DISPLAY_NAMES.put("a_s", "Subiektywność artykułu");
        ATTR_DISPLAY_NAMES.put("a_e", "Nacech. emocj. artykułu");
        ATTR_DISPLAY_NAMES.put("t_s", "Subiektywność tytułu");
        ATTR_DISPLAY_NAMES.put("t_e", "Nacech. emocj. tytułu");
        ATTR_DISPLAY_NAMES.put("p", "Stosunek poz. słów");
        ATTR_DISPLAY_NAMES.put("s", "Popularność");
    }

    @FXML
    public void initialize() {
        loadLinguisticVariables();
        buildCheckboxes();
        buildWeightSliders();
        buildSortCombo();
        buildSummaryTableColumns();
        buildAdvancedView();
        buildLabelsTableColumns();
        populateLabelDefinitionsFromFcl();

        statusLabel.setText("Gotowy. Wybierz konfigurację i wygeneruj podsumowania.");
    }

    private void loadLinguisticVariables() {
        try {
            allVariables = FclLoader.loadLinguisticVariables("ksr.fcl");
            statusLabel.setText("Załadowano " + allVariables.size() + " zmiennych lingwistycznych z pliku FCL.");
        } catch (Exception e) {
            statusLabel.setText("Błąd ładowania pliku FCL: " + e.getMessage());
        }
    }

    private void buildCheckboxes() {
        quantifierCheckboxes.getChildren().clear();
        summarizerCheckboxes.getChildren().clear();
        qualifierCheckboxes.getChildren().clear();
        quantifierMap.clear();
        summarizerMap.clear();
        qualifierMap.clear();

        for (LinguisticVariable var : allVariables) {
            String name = var.getAttributeName();

            if (name.equals("kwantyfikator_wzgledny")) {
                Label header = new Label("Względne:");
                header.setStyle("-fx-font-weight: bold; -fx-text-fill: #9ca3af; -fx-font-size: 11px;");
                quantifierCheckboxes.getChildren().add(header);
                for (String label : var.getLabels()) {
                    Quantifier q = new RelativeQuantifier(label, var.getLabelSet(label));
                    CheckBox cb = new CheckBox(label.replace("_", " "));
                    cb.setSelected(true);
                    quantifierMap.put(cb, q);
                    quantifierCheckboxes.getChildren().add(cb);
                }
            } else if (name.equals("kwantyfikator_bezwzgledny")) {
                Label header = new Label("Bezwzględne:");
                header.setStyle("-fx-font-weight: bold; -fx-text-fill: #9ca3af; -fx-font-size: 11px;");
                quantifierCheckboxes.getChildren().add(header);
                for (String label : var.getLabels()) {
                    Quantifier q = new AbsoluteQuantifier(label, var.getLabelSet(label));
                    CheckBox cb = new CheckBox(label.replace("_", " "));
                    cb.setSelected(true);
                    quantifierMap.put(cb, q);
                    quantifierCheckboxes.getChildren().add(cb);
                }
            } else {
                String displayName = ATTR_DISPLAY_NAMES.getOrDefault(name, name);
                Label header = new Label(displayName + ":");
                header.setStyle("-fx-font-weight: bold; -fx-text-fill: #9ca3af; -fx-font-size: 11px;");
                summarizerCheckboxes.getChildren().add(header);

                for (String label : var.getLabels()) {
                    FuzzyStatement stmt = new FuzzyStatement(name, label, var.getLabelSet(label));

                    CheckBox cbSum = new CheckBox(label.replace("_", " "));
                    cbSum.setSelected(false);
                    summarizerMap.put(cbSum, stmt);
                    summarizerCheckboxes.getChildren().add(cbSum);

                    CheckBox cbQual = new CheckBox(displayName + " – " + label.replace("_", " "));
                    cbQual.setSelected(false);
                    qualifierMap.put(cbQual, stmt);
                    qualifierCheckboxes.getChildren().add(cbQual);
                }
            }
        }
    }

    private void buildWeightSliders() {
        weightsContainer.getChildren().clear();
        for (int i = 0; i < 11; i++) {
            Label lbl = new Label(MEASURE_NAMES[i]);
            lbl.setStyle("-fx-font-size: 11px;");

            Slider slider = new Slider(0, 1, 1.0);
            slider.setShowTickLabels(true);
            slider.setShowTickMarks(true);
            slider.setMajorTickUnit(0.25);
            slider.setMinorTickCount(4);
            slider.setBlockIncrement(0.05);
            slider.setMaxWidth(Double.MAX_VALUE);

            Label valueLabel = new Label("1.00");
            valueLabel.setMinWidth(35);
            valueLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7c3aed; -fx-font-weight: bold;");
            slider.valueProperty().addListener(
                    (obs, oldVal, newVal) -> valueLabel.setText(String.format("%.2f", newVal.doubleValue())));

            HBox row = new HBox(8, lbl, slider, valueLabel);
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            HBox.setHgrow(slider, javafx.scene.layout.Priority.ALWAYS);

            weightSliders[i] = slider;
            weightsContainer.getChildren().add(row);
        }
    }

    private void buildSortCombo() {
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add("T (Ogólny)");
        for (int i = 1; i <= 11; i++) {
            items.add("T" + i);
        }
        sortCombo.setItems(items);
        sortCombo.getSelectionModel().selectFirst();
    }

    @SuppressWarnings("unchecked")
    private void buildSummaryTableColumns() {
        summaryTable.setItems(summaryRows);
        summaryTable.setEditable(true);
        summaryTable.getColumns().clear();

        TableColumn<SummaryRow, Boolean> selectCol = new TableColumn<>("");
        selectCol.setCellValueFactory(cd -> cd.getValue().selectedProperty());
        selectCol.setCellFactory(CheckBoxTableCell.forTableColumn(selectCol));
        selectCol.setEditable(true);
        selectCol.setPrefWidth(35);
        selectCol.setSortable(false);

        TableColumn<SummaryRow, Number> idxCol = new TableColumn<>("Lp.");
        idxCol.setCellValueFactory(cd -> cd.getValue().indexProperty());
        idxCol.setPrefWidth(40);

        TableColumn<SummaryRow, String> textCol = new TableColumn<>("Podsumowanie");
        textCol.setCellValueFactory(cd -> cd.getValue().summaryTextProperty());
        textCol.setPrefWidth(350);

        TableColumn<SummaryRow, Number> scoreCol = new TableColumn<>("T");
        scoreCol.setCellValueFactory(cd -> cd.getValue().overallScoreProperty());
        scoreCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.4f", item.doubleValue()));
            }
        });
        scoreCol.setPrefWidth(70);

        summaryTable.getColumns().addAll(selectCol, idxCol, textCol, scoreCol);

        for (int i = 1; i <= 11; i++) {
            final int tIdx = i;
            TableColumn<SummaryRow, Number> tCol = new TableColumn<>("T" + i);
            tCol.setCellValueFactory(cd -> cd.getValue().measureProperty(tIdx));
            tCol.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Number item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? "" : String.format("%.4f", item.doubleValue()));
                }
            });
            tCol.setPrefWidth(65);
            summaryTable.getColumns().add(tCol);
        }
    }

    @FXML
    private void handleGenerate(ActionEvent event) {
        statusLabel.setText("Generowanie podsumowań...");

        List<Quantifier> selectedQuantifiers = new ArrayList<>();
        for (Map.Entry<CheckBox, Quantifier> e : quantifierMap.entrySet()) {
            if (e.getKey().isSelected())
                selectedQuantifiers.add(e.getValue());
        }

        List<FuzzyStatement> selectedSummarizers = new ArrayList<>();
        for (Map.Entry<CheckBox, FuzzyStatement> e : summarizerMap.entrySet()) {
            if (e.getKey().isSelected())
                selectedSummarizers.add(e.getValue());
        }

        List<FuzzyStatement> selectedQualifiers = new ArrayList<>();
        for (Map.Entry<CheckBox, FuzzyStatement> e : qualifierMap.entrySet()) {
            if (e.getKey().isSelected())
                selectedQualifiers.add(e.getValue());
        }

        if (selectedQuantifiers.isEmpty()) {
            showAlert("Brak kwantyfikatorów", "Wybierz co najmniej jeden kwantyfikator.");
            return;
        }
        if (selectedSummarizers.isEmpty()) {
            showAlert("Brak sumaryzatorów", "Wybierz co najmniej jeden sumaryzator.");
            return;
        }

        List<DataEntity> records;
        try {
            records = repository.getAllRecords();
        } catch (Exception e) {
            showAlert("Błąd bazy danych", "Nie udało się załadować danych: " + e.getMessage());
            return;
        }

        if (records.isEmpty()) {
            showAlert("Brak danych", "Baza danych nie zwróciła żadnych rekordów.");
            return;
        }

        List<LinguisticSummaryDTO> summaries = generator.generateSingleSubject(
                records, selectedQuantifiers, selectedQualifiers, selectedSummarizers);

        Map<Integer, Double> weights = getWeightsFromSliders();
        summaries = OptimalSummaryOptimizer.optimize(summaries, weights);

        summaryRows.clear();
        for (int i = 0; i < summaries.size(); i++) {
            summaryRows.add(new SummaryRow(i + 1, summaries.get(i)));
        }

        resultCountLabel.setText(summaries.size() + " wyników");
        statusLabel.setText("Wygenerowano " + summaries.size() + " podsumowań dla " + records.size() + " rekordów.");
    }

    @FXML
    private void handleRecalculate(ActionEvent event) {
        if (summaryRows.isEmpty()) {
            statusLabel.setText("Najpierw wygeneruj podsumowania.");
            return;
        }

        Map<Integer, Double> weights = getWeightsFromSliders();

        List<LinguisticSummaryDTO> dtos = new ArrayList<>();
        for (SummaryRow row : summaryRows) {
            dtos.add(row.getDto());
        }
        dtos = OptimalSummaryOptimizer.optimize(dtos, weights);

        summaryRows.clear();
        for (int i = 0; i < dtos.size(); i++) {
            summaryRows.add(new SummaryRow(i + 1, dtos.get(i)));
        }

        statusLabel.setText("Przeliczono wyniki z nowymi wagami.");
    }

    @FXML
    private void handleSort(ActionEvent event) {
        if (summaryRows.isEmpty())
            return;

        String selected = sortCombo.getValue();
        if (selected == null)
            return;

        Comparator<SummaryRow> comparator;
        if (selected.equals("T (Ogólny)")) {
            comparator = Comparator.comparingDouble(SummaryRow::getOverallScore).reversed();
        } else {
            int tIdx = Integer.parseInt(selected.substring(1));
            comparator = Comparator.comparingDouble((SummaryRow r) -> r.getMeasure(tIdx)).reversed();
        }

        FXCollections.sort(summaryRows, comparator);

        for (int i = 0; i < summaryRows.size(); i++) {
            summaryRows.get(i).indexProperty().set(i + 1);
        }

        statusLabel.setText("Posortowano po " + selected + ".");
    }

    @FXML
    private void handleSelectAll(ActionEvent event) {
        boolean val = selectAllCheckbox.isSelected();
        for (SummaryRow row : summaryRows) {
            row.setSelected(val);
        }
    }

    @FXML
    private void handleSaveSelected(ActionEvent event) {
        List<SummaryRow> selected = new ArrayList<>();
        for (SummaryRow row : summaryRows) {
            if (row.isSelected())
                selected.add(row);
        }

        if (selected.isEmpty()) {
            showAlert("Brak zaznaczonych", "Zaznacz co najmniej jedno podsumowanie do zapisania.");
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Zapisz podsumowania do pliku");
        fc.setInitialFileName("podsumowania.txt");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki tekstowe", "*.txt"));

        File file = fc.showSaveDialog(summaryTable.getScene().getWindow());
        if (file == null)
            return;

        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            out.println("=== Podsumowania Lingwistyczne – KSR ===");
            out.println("Wagi: " + getWeightsFromSliders());
            out.println("Liczba zapisanych: " + selected.size());
            out.println();

            for (SummaryRow row : selected) {
                out.printf("Lp. %d | T=%.4f%n", row.getIndex(), row.getOverallScore());
                out.printf("  \"%s\"%n", row.getSummaryText());
                for (int i = 1; i <= 11; i++) {
                    out.printf("  T%d=%.4f", i, row.getMeasure(i));
                    if (i < 11)
                        out.print(", ");
                }
                out.println();
                out.println();
            }

            statusLabel.setText("Zapisano " + selected.size() + " podsumowań do: " + file.getName());
        } catch (IOException e) {
            showAlert("Błąd zapisu", "Nie udało się zapisać pliku: " + e.getMessage());
        }
    }

    @FXML
    private void handleToggleView(ActionEvent event) {
        boolean advanced = viewToggle.isSelected();
        basicView.setVisible(!advanced);
        basicView.setManaged(!advanced);
        advancedView.setVisible(advanced);
        advancedView.setManaged(advanced);

        statusLabel.setText(advanced
                ? "Widok zaawansowany"
                : "Widok podstawowy");
    }

    private void buildAdvancedView() {
        advTypeCombo.setItems(FXCollections.observableArrayList(LabelDefinition.LabelType.values()));
        advTypeCombo.getSelectionModel().selectFirst();
        advMfCombo.setItems(FXCollections.observableArrayList(LabelDefinition.MfType.values()));
        advMfCombo.getSelectionModel().selectFirst();
        updateAdvParamFields();
    }

    @SuppressWarnings("unchecked")
    private void buildLabelsTableColumns() {
        labelsTable.setItems(labelDefinitions);
        labelsTable.getColumns().clear();

        TableColumn<LabelDefinition, String> typeCol = new TableColumn<>("Typ");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeCol.setPrefWidth(140);

        TableColumn<LabelDefinition, String> attrCol = new TableColumn<>("Atrybut");
        attrCol.setCellValueFactory(new PropertyValueFactory<>("attributeName"));
        attrCol.setPrefWidth(120);

        TableColumn<LabelDefinition, String> labelCol = new TableColumn<>("Etykieta");
        labelCol.setCellValueFactory(new PropertyValueFactory<>("labelName"));
        labelCol.setPrefWidth(120);

        TableColumn<LabelDefinition, String> mfCol = new TableColumn<>("Funkcja przynal.");
        mfCol.setCellValueFactory(new PropertyValueFactory<>("mfType"));
        mfCol.setPrefWidth(120);

        TableColumn<LabelDefinition, String> paramsCol = new TableColumn<>("Parametry");
        paramsCol.setCellValueFactory(new PropertyValueFactory<>("parameters"));
        paramsCol.setPrefWidth(200);

        labelsTable.getColumns().addAll(typeCol, attrCol, labelCol, mfCol, paramsCol);
    }

    private void populateLabelDefinitionsFromFcl() {
        for (LinguisticVariable var : allVariables) {
            String attrName = var.getAttributeName();

            LabelDefinition.LabelType type;
            if (attrName.equals("kwantyfikator_wzgledny")) {
                type = LabelDefinition.LabelType.RELATIVE_QUANTIFIER;
            } else if (attrName.equals("kwantyfikator_bezwzgledny")) {
                type = LabelDefinition.LabelType.ABSOLUTE_QUANTIFIER;
            } else {
                type = LabelDefinition.LabelType.SUMMARIZER;
            }

            for (String label : var.getLabels()) {
                FuzzySet fs = var.getLabelSet(label);
                double uMin = fs.getUniverse().getMinBound();
                double uMax = fs.getUniverse().getMaxBound();

                labelDefinitions.add(new LabelDefinition(
                        type, attrName, label,
                        LabelDefinition.MfType.TRAPEZOIDAL,
                        "FCL predef.",
                        uMin, uMax,
                        true));
            }
        }
    }

    @FXML
    private void handleMfTypeChange(ActionEvent event) {
        updateAdvParamFields();
    }

    private void updateAdvParamFields() {
        advParamsContainer.getChildren().clear();
        advParamFields.clear();

        LabelDefinition.MfType mfType = advMfCombo.getValue();
        if (mfType == null)
            mfType = LabelDefinition.MfType.TRIANGULAR;

        String[] paramNames;
        switch (mfType) {
            case TRIANGULAR:
                paramNames = new String[] { "a", "b", "c" };
                break;
            case TRAPEZOIDAL:
                paramNames = new String[] { "a", "b", "c", "d" };
                break;
            case GAUSSIAN:
                paramNames = new String[] { "średnia", "sigma (odchylenie)" };
                break;
            default:
                paramNames = new String[] {};
        }

        for (String pName : paramNames) {
            Label lbl = new Label(pName + ":");
            lbl.setStyle("-fx-font-size: 11px;");
            TextField tf = new TextField();
            tf.setPromptText("0.0");
            advParamFields.add(tf);
            advParamsContainer.getChildren().addAll(lbl, tf);
        }
    }

    @FXML
    private void handleAddLabel(ActionEvent event) {
        LabelDefinition.LabelType type = advTypeCombo.getValue();
        String attrName = advAttributeField.getText().trim();
        String labelName = advLabelField.getText().trim();
        LabelDefinition.MfType mfType = advMfCombo.getValue();

        if (attrName.isEmpty() || labelName.isEmpty()) {
            showAlert("Brak danych", "Podaj nazwę atrybutu i etykiety.");
            return;
        }

        double uMin, uMax;
        try {
            uMin = Double.parseDouble(advUnivMinField.getText().trim());
            uMax = Double.parseDouble(advUnivMaxField.getText().trim());
        } catch (NumberFormatException e) {
            showAlert("Błąd", "Podaj poprawne wartości liczbowe dla granic przedziału.");
            return;
        }

        double[] params = new double[advParamFields.size()];
        for (int i = 0; i < advParamFields.size(); i++) {
            try {
                params[i] = Double.parseDouble(advParamFields.get(i).getText().trim());
            } catch (NumberFormatException e) {
                showAlert("Błąd", "Podaj poprawne wartości liczbowe dla parametrów funkcji przynależności.");
                return;
            }
        }

        ClassicSet universe = new ClassicSet(uMin, uMax);
        net.sourceforge.jFuzzyLogic.membership.MembershipFunction mf;

        switch (mfType) {
            case TRIANGULAR:
                mf = new TriangularMembershipFunction(params[0], params[1], params[2]);
                break;
            case TRAPEZOIDAL:
                mf = new TrapezoidalMembershipFunction(params[0], params[1], params[2], params[3]);
                break;
            case GAUSSIAN:
                mf = new GaussianMembershipFunction(params[0], params[1]);
                break;
            default:
                showAlert("Błąd", "Nieznany typ funkcji przynależności.");
                return;
        }

        FuzzySet fuzzySet = new FuzzySet(universe, mf);

        LinguisticVariable targetVar = null;
        String varName = attrName;
        if (type == LabelDefinition.LabelType.RELATIVE_QUANTIFIER) {
            varName = "kwantyfikator_wzgledny";
        } else if (type == LabelDefinition.LabelType.ABSOLUTE_QUANTIFIER) {
            varName = "kwantyfikator_bezwzgledny";
        }

        for (LinguisticVariable v : allVariables) {
            if (v.getAttributeName().equals(varName)) {
                targetVar = v;
                break;
            }
        }

        if (targetVar == null) {
            targetVar = new LinguisticVariable(varName);
            allVariables.add(targetVar);
        }

        targetVar.addLabel(labelName, fuzzySet);

        String paramStr = Arrays.toString(params);
        labelDefinitions.add(new LabelDefinition(
                type, varName, labelName,
                mfType, paramStr,
                uMin, uMax,
                false));

        buildCheckboxes();

        advLabelField.clear();

        statusLabel.setText("Dodano nową etykietę: " + labelName + " (atrybut: " + varName + ").");
    }

    @FXML
    private void handleRemoveLabel(ActionEvent event) {
        LabelDefinition selected = labelsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Brak wyboru", "Zaznacz etykietę do usunięcia.");
            return;
        }
        if (selected.isPredefined()) {
            showAlert("Nie można usunąć", "Etykiety predefiniowane (z pliku FCL) nie mogą być usunięte.");
            return;
        }

        labelDefinitions.remove(selected);
        buildCheckboxes();

        statusLabel.setText("Usunięto etykietę: " + selected.getLabelName());
    }

    private Map<Integer, Double> getWeightsFromSliders() {
        Map<Integer, Double> weights = new HashMap<>();
        for (int i = 0; i < 11; i++) {
            weights.put(i + 1, weightSliders[i].getValue());
        }
        return weights;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
