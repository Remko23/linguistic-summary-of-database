package org.example.summary;

import org.example.database.DataEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Utility tool for partitioning a dataset of DataEntity records.
 * Essential for generating multi-subject and comparative summaries.
 */
public class SubjectPartitioner {

    /**
     * Filters a list of records based on a custom Predicate (e.g. category matching).
     */
    public static List<DataEntity> filter(List<DataEntity> records, Predicate<DataEntity> criteria) {
        List<Double> matched = new ArrayList<>();
        List<DataEntity> result = new ArrayList<>();
        for (DataEntity record : records) {
            if (criteria.test(record)) {
                result.add(record);
            }
        }
        return result;
    }

    /**
     * Splits a list of records into two subsets based on a predicate.
     */
    public static List<List<DataEntity>> split(List<DataEntity> records, Predicate<DataEntity> criteria) {
        List<DataEntity> subject1 = new ArrayList<>();
        List<DataEntity> subject2 = new ArrayList<>();
        
        for (DataEntity record : records) {
            if (criteria.test(record)) {
                subject1.add(record);
            } else {
                subject2.add(record);
            }
        }
        
        List<List<DataEntity>> parts = new ArrayList<>();
        parts.add(subject1);
        parts.add(subject2);
        return parts;
    }
}
