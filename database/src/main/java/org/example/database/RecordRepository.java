package org.example.database;

import java.util.List;

public interface RecordRepository {

    List<DataEntity> getAllRecords();

    List<DataEntity> getRecordsByCriteria(String criteria);
}
