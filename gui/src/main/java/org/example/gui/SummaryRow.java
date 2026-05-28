package org.example.gui;

import javafx.beans.property.*;
import org.example.summary.LinguisticSummaryDTO;

public class SummaryRow {
    private final BooleanProperty selected;
    private final IntegerProperty index;
    private final StringProperty summaryText;
    private final DoubleProperty overallScore;
    private final DoubleProperty[] measures;
    private final LinguisticSummaryDTO dto;

    public SummaryRow(int index, LinguisticSummaryDTO dto) {
        this.dto = dto;
        this.selected = new SimpleBooleanProperty(false);
        this.index = new SimpleIntegerProperty(index);
        this.summaryText = new SimpleStringProperty(dto.getSummaryText());
        this.overallScore = new SimpleDoubleProperty(dto.getOverallScore());
        this.measures = new DoubleProperty[11];
        for (int i = 0; i < 11; i++) {
            measures[i] = new SimpleDoubleProperty(dto.getMeasure(i + 1));
        }
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean value) {
        selected.set(value);
    }

    public IntegerProperty indexProperty() {
        return index;
    }

    public int getIndex() {
        return index.get();
    }

    public StringProperty summaryTextProperty() {
        return summaryText;
    }

    public String getSummaryText() {
        return summaryText.get();
    }

    public DoubleProperty overallScoreProperty() {
        return overallScore;
    }

    public double getOverallScore() {
        return overallScore.get();
    }

    public void setOverallScore(double value) {
        overallScore.set(value);
    }

    public DoubleProperty measureProperty(int tIndex) {
        if (tIndex < 1 || tIndex > 11)
            throw new IllegalArgumentException("Index must be 1-11");
        return measures[tIndex - 1];
    }

    public double getMeasure(int tIndex) {
        return measureProperty(tIndex).get();
    }

    public LinguisticSummaryDTO getDto() {
        return dto;
    }

    public void refreshFromDto() {
        overallScore.set(dto.getOverallScore());
    }
}
