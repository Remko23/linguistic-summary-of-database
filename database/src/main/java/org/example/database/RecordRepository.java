package org.example.database;

import java.util.List;

/**
 * Access interface providing abstraction over SQL queries.
 */
public interface RecordRepository {
    
    /**
     * Fetches all numerical entities from the database.
     */
    List<DataEntity> getAllRecords();
    
    /**
     * Fetches numerical entities based on custom SQL criteria.
     */
    List<DataEntity> getRecordsByCriteria(String criteria);
}
