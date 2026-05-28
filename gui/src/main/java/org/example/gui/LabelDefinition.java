package org.example.gui;

import javafx.beans.property.*;

public class LabelDefinition {

    public enum LabelType {
        SUMMARIZER("Sumaryzator"),
        QUALIFIER("Kwalifikator"),
        RELATIVE_QUANTIFIER("Kwantyfikator względny"),
        ABSOLUTE_QUANTIFIER("Kwantyfikator bezwzględny");

        private final String displayName;

        LabelType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    public enum MfType {
        TRIANGULAR("Trójkątna"),
        TRAPEZOIDAL("Trapezoidalna"),
        GAUSSIAN("Gaussowska");

        private final String displayName;

        MfType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    private final StringProperty type;
    private final StringProperty attributeName;
    private final StringProperty labelName;
    private final StringProperty mfType;
    private final StringProperty parameters;
    private final boolean predefined;
    private final double universeMin;
    private final double universeMax;

    public LabelDefinition(LabelType type, String attributeName, String labelName,
            MfType mfType, String parameters,
            double universeMin, double universeMax,
            boolean predefined) {
        this.type = new SimpleStringProperty(type.getDisplayName());
        this.attributeName = new SimpleStringProperty(attributeName);
        this.labelName = new SimpleStringProperty(labelName);
        this.mfType = new SimpleStringProperty(mfType.getDisplayName());
        this.parameters = new SimpleStringProperty(parameters);
        this.universeMin = universeMin;
        this.universeMax = universeMax;
        this.predefined = predefined;
    }

    public StringProperty typeProperty() {
        return type;
    }

    public StringProperty attributeNameProperty() {
        return attributeName;
    }

    public StringProperty labelNameProperty() {
        return labelName;
    }

    public StringProperty mfTypeProperty() {
        return mfType;
    }

    public StringProperty parametersProperty() {
        return parameters;
    }

    public String getType() {
        return type.get();
    }

    public String getAttributeName() {
        return attributeName.get();
    }

    public String getLabelName() {
        return labelName.get();
    }

    public String getMfType() {
        return mfType.get();
    }

    public String getParameters() {
        return parameters.get();
    }

    public double getUniverseMin() {
        return universeMin;
    }

    public double getUniverseMax() {
        return universeMax;
    }

    public boolean isPredefined() {
        return predefined;
    }
}
