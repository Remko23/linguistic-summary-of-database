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
    private ComboBox<String> multiP1AttrCombo;
    @FXML
    private ComboBox<String> multiP1LabelCombo;
    @FXML
    private ComboBox<String> multiP2AttrCombo;
    @FXML
    private ComboBox<String> multiP2LabelCombo;
    @FXML
    private CheckBox multiForm1Check;
    @FXML
    private CheckBox multiForm2Check;
    @FXML
    private CheckBox multiForm3Check;
    @FXML
    private CheckBox multiForm4Check;

    @FXML
    private Label statusLabel;

    private final RecordRepository repository = new JdbcRecordRepository();
    private final SummaryGenerator generator = new SummaryGenerator();
    private List<LinguisticVariable> allVariables = new ArrayList<>();

    private final Map<CheckBox, Quantifier> quantifierMap = new LinkedHashMap<>();
    private final Map<CheckBox, FuzzyStatement> summarizerMap = new LinkedHashMap<>();
    private final Map<CheckBox, FuzzyStatement> qualifierMap = new LinkedHashMap<>();
    private final Slider[] weightSliders = new Slider[11];
    private final TextField[] weightFields = new TextField[11];

    private static final double[] DEFAULT_WEIGHTS = {
            1.0, 0.5, 0.35, 0.4, 0.1,
            0.25, 0.25, 0.25,
            0.0, 0.0, 0.0
    };

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
        ATTR_DISPLAY_NAMES.put("w_l", "Średnia długość słowa");
        ATTR_DISPLAY_NAMES.put("a_s", "Subiektywność artykułu");
        ATTR_DISPLAY_NAMES.put("a_e", "Nacechowanie emocjonalne artykułu");
        ATTR_DISPLAY_NAMES.put("t_s", "Subiektywność tytułu");
        ATTR_DISPLAY_NAMES.put("t_e", "Nacechowanie emocjonalne tytułu");
        ATTR_DISPLAY_NAMES.put("p", "Stosunek pozytywnych słów");
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
        buildMultiSubjectCombos();

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
                    CheckBox cb = new CheckBox(SummaryGenerator.mapQuantifierName(label));
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
                    CheckBox cb = new CheckBox(SummaryGenerator.mapQuantifierName(label));
                    cb.setSelected(true);
                    quantifierMap.put(cb, q);
                    quantifierCheckboxes.getChildren().add(cb);
                }
            } else {
                String displayName = ATTR_DISPLAY_NAMES.getOrDefault(name, name);
                CheckBox varSumCheckbox = new CheckBox(displayName);
                varSumCheckbox.getStyleClass().add("variable-checkbox");
                varSumCheckbox.setAllowIndeterminate(true);
                varSumCheckbox.setSelected(false);

                VBox sumLabelsBox = new VBox(3);
                List<CheckBox> sumLabelCheckboxes = new ArrayList<>();

                for (String label : var.getLabels()) {
                    FuzzyStatement stmt = new FuzzyStatement(name, label, var.getLabelSet(label));
                    CheckBox cbSum = new CheckBox(label.replace("_", " "));
                    cbSum.setSelected(false);
                    summarizerMap.put(cbSum, stmt);
                    sumLabelCheckboxes.add(cbSum);
                    sumLabelsBox.getChildren().add(cbSum);
                }

                bindVariableCheckbox(varSumCheckbox, sumLabelCheckboxes);

                TitledPane sumPane = new TitledPane();
                sumPane.setGraphic(varSumCheckbox);
                sumPane.setText(null);
                sumPane.setContent(sumLabelsBox);
                sumPane.setExpanded(false);
                sumPane.setAnimated(false);
                sumPane.getStyleClass().add("variable-pane");
                summarizerCheckboxes.getChildren().add(sumPane);

                CheckBox varQualCheckbox = new CheckBox(displayName);
                varQualCheckbox.getStyleClass().add("variable-checkbox");
                varQualCheckbox.setAllowIndeterminate(true);
                varQualCheckbox.setSelected(false);

                VBox qualLabelsBox = new VBox(3);
                List<CheckBox> qualLabelCheckboxes = new ArrayList<>();

                for (String label : var.getLabels()) {
                    FuzzyStatement stmt = new FuzzyStatement(name, label, var.getLabelSet(label));
                    CheckBox cbQual = new CheckBox(label.replace("_", " "));
                    cbQual.setSelected(false);
                    qualifierMap.put(cbQual, stmt);
                    qualLabelCheckboxes.add(cbQual);
                    qualLabelsBox.getChildren().add(cbQual);
                }

                bindVariableCheckbox(varQualCheckbox, qualLabelCheckboxes);

                TitledPane qualPane = new TitledPane();
                qualPane.setGraphic(varQualCheckbox);
                qualPane.setText(null);
                qualPane.setContent(qualLabelsBox);
                qualPane.setExpanded(false);
                qualPane.setAnimated(false);
                qualPane.getStyleClass().add("variable-pane");
                qualifierCheckboxes.getChildren().add(qualPane);
            }
        }
    }

    private void bindVariableCheckbox(CheckBox parent, List<CheckBox> children) {
        final boolean[] updating = { false };

        parent.setOnAction(e -> {
            if (updating[0])
                return;
            updating[0] = true;
            boolean selected = parent.isSelected();
            if (parent.isIndeterminate()) {
                parent.setIndeterminate(false);
                parent.setSelected(true);
                for (CheckBox cb : children)
                    cb.setSelected(true);
            } else {
                for (CheckBox cb : children)
                    cb.setSelected(selected);
            }
            updating[0] = false;
        });

        for (CheckBox child : children) {
            child.selectedProperty().addListener((obs, oldVal, newVal) -> {
                if (updating[0])
                    return;
                updating[0] = true;
                long selectedCount = children.stream().filter(CheckBox::isSelected).count();
                if (selectedCount == 0) {
                    parent.setIndeterminate(false);
                    parent.setSelected(false);
                } else if (selectedCount == children.size()) {
                    parent.setIndeterminate(false);
                    parent.setSelected(true);
                } else {
                    parent.setIndeterminate(true);
                }
                updating[0] = false;
            });
        }
    }

    private void buildWeightSliders() {
        weightsContainer.getChildren().clear();
        for (int i = 0; i < 11; i++) {
            Label lbl = new Label(MEASURE_NAMES[i]);
            lbl.setStyle("-fx-font-size: 11px;");

            double defaultWeight = DEFAULT_WEIGHTS[i];

            Slider slider = new Slider(0, 1, defaultWeight);
            slider.setShowTickLabels(true);
            slider.setShowTickMarks(true);
            slider.setMajorTickUnit(0.25);
            slider.setMinorTickCount(4);
            slider.setBlockIncrement(0.05);
            slider.setMaxWidth(Double.MAX_VALUE);

            TextField weightField = new TextField(String.format("%.2f", defaultWeight));
            weightField.getStyleClass().add("weight-field");

            slider.valueProperty().addListener(
                    (obs, oldVal, newVal) -> weightField.setText(String.format("%.2f", newVal.doubleValue())));

            Runnable applyFieldValue = () -> {
                try {
                    double val = Double.parseDouble(weightField.getText().replace(',', '.').trim());
                    val = Math.max(0.0, Math.min(1.0, val));
                    slider.setValue(val);
                    weightField.setText(String.format("%.2f", val));
                } catch (NumberFormatException ex) {
                    weightField.setText(String.format("%.2f", slider.getValue()));
                }
            };
            weightField.setOnAction(e -> applyFieldValue.run());
            weightField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
                if (!isFocused)
                    applyFieldValue.run();
            });

            HBox row = new HBox(8, lbl, slider, weightField);
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            HBox.setHgrow(slider, javafx.scene.layout.Priority.ALWAYS);

            weightSliders[i] = slider;
            weightFields[i] = weightField;
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
        FXCollections.sort(summaryRows, Comparator.comparingDouble(SummaryRow::getOverallScore).reversed());
        for (int i = 0; i < summaryRows.size(); i++) {
            summaryRows.get(i).indexProperty().set(i + 1);
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
        FXCollections.sort(summaryRows, Comparator.comparingDouble(SummaryRow::getOverallScore).reversed());
        for (int i = 0; i < summaryRows.size(); i++) {
            summaryRows.get(i).indexProperty().set(i + 1);
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
    private void handleSaveTop25(ActionEvent event) {
        if (summaryRows.isEmpty()) {
            showAlert("Brak wyników", "Najpierw wygeneruj podsumowania.");
            return;
        }

        List<SummaryRow> visibleRows = summaryTable.getItems();
        int count = Math.min(25, visibleRows.size());
        List<SummaryRow> top5 = new ArrayList<>(visibleRows.subList(0, count));

        String sortedBy = sortCombo.getValue() != null ? sortCombo.getValue() : "T (Ogólny)";

        FileChooser fc = new FileChooser();
        fc.setTitle("Zapisz TOP 25 do pliku");
        fc.setInitialFileName("top5.txt");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki tekstowe", "*.txt"));

        File file = fc.showSaveDialog(summaryTable.getScene().getWindow());
        if (file == null)
            return;

        try (PrintWriter out = new PrintWriter(new FileWriter(file))) {
            out.println("Sortowanie: " + sortedBy);
            out.println("Wagi: " + getWeightsFromSliders());
            out.println();

            for (int i = 0; i < top5.size(); i++) {
                SummaryRow row = top5.get(i);
                out.printf("%d. T=%.4f%n", i + 1, row.getOverallScore());
                out.printf("   \"%s\"%n", row.getSummaryText());
                for (int j = 1; j <= 11; j++) {
                    out.printf("   T%d=%.4f", j, row.getMeasure(j));
                    if (j < 11)
                        out.print(", ");
                }
                out.println();
                out.println();
            }

            statusLabel.setText("Zapisano TOP " + top5.size() + " do: " + file.getName());
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

    private void buildMultiSubjectCombos() {
        List<String> attrNames = new ArrayList<>();
        for (LinguisticVariable var : allVariables) {
            String name = var.getAttributeName();
            if (!name.equals("kwantyfikator_wzgledny") && !name.equals("kwantyfikator_bezwzgledny")) {
                String display = ATTR_DISPLAY_NAMES.getOrDefault(name, name);
                attrNames.add(display);
            }
        }
        ObservableList<String> attrItems = FXCollections.observableArrayList(attrNames);
        multiP1AttrCombo.setItems(FXCollections.observableArrayList(attrItems));
        multiP2AttrCombo.setItems(FXCollections.observableArrayList(attrItems));
    }

    @FXML
    private void handleMultiP1AttrChange(ActionEvent event) {
        updateMultiLabelCombo(multiP1AttrCombo, multiP1LabelCombo);
    }

    @FXML
    private void handleMultiP2AttrChange(ActionEvent event) {
        updateMultiLabelCombo(multiP2AttrCombo, multiP2LabelCombo);
    }

    private void updateMultiLabelCombo(ComboBox<String> attrCombo, ComboBox<String> labelCombo) {
        labelCombo.getItems().clear();
        String selectedDisplay = attrCombo.getValue();
        if (selectedDisplay == null)
            return;

        String attrKey = getAttrKeyByDisplay(selectedDisplay);
        if (attrKey == null)
            return;

        for (LinguisticVariable var : allVariables) {
            if (var.getAttributeName().equals(attrKey)) {
                for (String label : var.getLabels()) {
                    labelCombo.getItems().add(label.replace("_", " "));
                }
                break;
            }
        }
        if (!labelCombo.getItems().isEmpty()) {
            labelCombo.getSelectionModel().selectFirst();
        }
    }

    private String getAttrKeyByDisplay(String displayName) {
        for (Map.Entry<String, String> e : ATTR_DISPLAY_NAMES.entrySet()) {
            if (e.getValue().equals(displayName))
                return e.getKey();
        }
        return displayName;
    }

    private FuzzyStatement buildSubjectStatement(ComboBox<String> attrCombo, ComboBox<String> labelCombo) {
        String attrDisplay = attrCombo.getValue();
        String labelDisplay = labelCombo.getValue();
        if (attrDisplay == null || labelDisplay == null)
            return null;

        String attrKey = getAttrKeyByDisplay(attrDisplay);
        String labelKey = labelDisplay.replace(" ", "_");

        for (LinguisticVariable var : allVariables) {
            if (var.getAttributeName().equals(attrKey)) {
                for (String l : var.getLabels()) {
                    if (l.equals(labelKey)) {
                        return new FuzzyStatement(attrKey, l, var.getLabelSet(l));
                    }
                }
            }
        }
        return null;
    }

    @FXML
    private void handleGenerateMultiSubject(ActionEvent event) {
        statusLabel.setText("Generowanie podsumowań wielopodmiotowych...");

        FuzzyStatement p1 = buildSubjectStatement(multiP1AttrCombo, multiP1LabelCombo);
        FuzzyStatement p2 = buildSubjectStatement(multiP2AttrCombo, multiP2LabelCombo);
        if (p1 == null || p2 == null) {
            showAlert("Brak podmiotów", "Wybierz atrybut i etykietę dla obu podmiotów P₁ i P₂.");
            return;
        }

        String p1Name = ATTR_DISPLAY_NAMES.getOrDefault(p1.getAttributeName(), p1.getAttributeName())
                + " " + p1.getLabel().replace("_", " ");
        String p2Name = ATTR_DISPLAY_NAMES.getOrDefault(p2.getAttributeName(), p2.getAttributeName())
                + " " + p2.getLabel().replace("_", " ");

        boolean[] enabledForms = {
                multiForm1Check.isSelected(),
                multiForm2Check.isSelected(),
                multiForm3Check.isSelected(),
                multiForm4Check.isSelected()
        };
        if (!enabledForms[0] && !enabledForms[1] && !enabledForms[2] && !enabledForms[3]) {
            showAlert("Brak form", "Wybierz co najmniej jedną formę podsumowania wielopodmiotowego.");
            return;
        }

        List<Quantifier> selectedQuantifiers = new ArrayList<>();
        if (enabledForms[0] || enabledForms[1]) {
            for (Map.Entry<CheckBox, Quantifier> e : quantifierMap.entrySet()) {
                if (e.getKey().isSelected() && e.getValue() instanceof RelativeQuantifier) {
                    selectedQuantifiers.add(e.getValue());
                }
            }
            if (selectedQuantifiers.isEmpty()) {
                showAlert("Brak kwantyfikatorów",
                        "Formy 1 i 2 wymagają kwantyfikatorów względnych. Zaznacz co najmniej jeden.");
                return;
            }
        }

        List<FuzzyStatement> selectedSummarizers = new ArrayList<>();
        for (Map.Entry<CheckBox, FuzzyStatement> e : summarizerMap.entrySet()) {
            if (e.getKey().isSelected())
                selectedSummarizers.add(e.getValue());
        }
        if (selectedSummarizers.isEmpty()) {
            showAlert("Brak sumaryzatorów", "Wybierz co najmniej jeden sumaryzator.");
            return;
        }

        List<FuzzyStatement> selectedQualifiers = new ArrayList<>();
        if (enabledForms[1] || enabledForms[3]) {
            for (Map.Entry<CheckBox, FuzzyStatement> e : qualifierMap.entrySet()) {
                if (e.getKey().isSelected())
                    selectedQualifiers.add(e.getValue());
            }
            if (selectedQualifiers.isEmpty()) {
                showAlert("Brak kwalifikatorów", "Formy 2 i 4 wymagają kwalifikatorów. Zaznacz co najmniej jeden.");
                return;
            }
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

        List<LinguisticSummaryDTO> summaries = generator.generateMultiSubjectAll(
                records, p1, p2, p1Name, p2Name,
                selectedQuantifiers, selectedQualifiers, selectedSummarizers, enabledForms);

        Map<Integer, Double> weights = getWeightsFromSliders();
        summaries = OptimalSummaryOptimizer.optimize(summaries, weights);

        summaryRows.clear();
        for (int i = 0; i < summaries.size(); i++) {
            summaryRows.add(new SummaryRow(i + 1, summaries.get(i)));
        }
        FXCollections.sort(summaryRows, Comparator.comparingDouble(SummaryRow::getOverallScore).reversed());
        for (int i = 0; i < summaryRows.size(); i++) {
            summaryRows.get(i).indexProperty().set(i + 1);
        }

        resultCountLabel.setText(summaries.size() + " wyników");
        statusLabel.setText("Wygenerowano " + summaries.size() + " podsumowań wielopodmiotowych (P₁=" + p1Name + ", P₂="
                + p2Name + ").");
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

    private String escapeJson(String s) {
        if (s == null)
            return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
