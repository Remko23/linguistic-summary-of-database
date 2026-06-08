package org.example.summary;

import org.example.database.DataEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class SubjectPartitioner {

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
