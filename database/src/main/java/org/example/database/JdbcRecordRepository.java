package org.example.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Standard JDBC implementation of RecordRepository.
 * Interacts with PostgreSQL through DbConnectionManager.
 * Falls back to mock data if connection fails or database table is absent.
 */
public class JdbcRecordRepository implements RecordRepository {

    @Override
    public List<DataEntity> getAllRecords() {
        return getRecordsByCriteria("1=1");
    }

    @Override
    public List<DataEntity> getRecordsByCriteria(String criteria) {
        List<DataEntity> list = new ArrayList<>();
        String sql = "SELECT * FROM client_data WHERE " + criteria;

        try (Connection conn = DbConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            while (rs.next()) {
                int id = rs.getInt("id");
                String label = rs.getString("label");
                DataEntity entity = new DataEntity(id, label);

                for (int i = 1; i <= columnCount; i++) {
                    String colName = meta.getColumnName(i);
                    if (!colName.equalsIgnoreCase("id") && !colName.equalsIgnoreCase("label")) {
                        entity.addAttribute(colName, rs.getDouble(i));
                    }
                }
                list.add(entity);
            }

        } catch (SQLException e) {
            System.err.println("Database query failed: " + e.getMessage() + ". Generating mock data instead.");
            return generateMockData();
        }

        return list;
    }

    /**
     * Helper to generate a realistic mock dataset for testing and evaluation
     * without requiring an active PostgreSQL instance.
     */
    private List<DataEntity> generateMockData() {
        List<DataEntity> mockList = new ArrayList<>();
        String[] types = {"A", "B", "C"};
        
        for (int i = 1; i <= 100; i++) {
            DataEntity entity = new DataEntity(i, "Client-" + i);
            
            // Adding a few numeric fields for fuzzy clustering/linguistic summarizing:
            // e.g. age: [18, 80], income: [2000, 15000], savingRate: [0.0, 0.5]
            double age = 18 + (Math.random() * 62);
            double income = 2000 + (Math.random() * 13000);
            double savingRate = Math.random() * 0.5;
            
            entity.addAttribute("age", age);
            entity.addAttribute("income", income);
            entity.addAttribute("savingRate", savingRate);
            
            mockList.add(entity);
        }
        return mockList;
    }
}
