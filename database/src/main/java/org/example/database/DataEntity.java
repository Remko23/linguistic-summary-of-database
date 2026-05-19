package org.example.database;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single database record with multiple numerical attributes.
 * These attributes will be extracted and evaluated using fuzzy logic.
 */
public class DataEntity {
    private final int id;
    private final String label;
    private final Map<String, Double> numericAttributes;

    public DataEntity(int id, String label) {
        this.id = id;
        this.label = label;
        this.numericAttributes = new HashMap<>();
    }

    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public void addAttribute(String name, double value) {
        numericAttributes.put(name, value);
    }

    public Double getAttribute(String name) {
        return numericAttributes.get(name);
    }

    public Map<String, Double> getNumericAttributes() {
        return new HashMap<>(numericAttributes);
    }

    @Override
    public String toString() {
        return "DataEntity{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", numericAttributes=" + numericAttributes +
                '}';
    }
}
