package org.example.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcRecordRepository implements RecordRepository {

    @Override
    public List<DataEntity> getAllRecords() {
        return getRecordsByCriteria("1=1");
    }

    @Override
    public List<DataEntity> getRecordsByCriteria(String criteria) {
        List<DataEntity> list = new ArrayList<>();
        String sql = "SELECT * FROM online_news WHERE " + criteria;

        try (Connection conn = DbConnectionManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            
            boolean hasId = false;
            boolean hasLabel = false;
            for (int i = 1; i <= columnCount; i++) {
                if (meta.getColumnName(i).equalsIgnoreCase("id")) hasId = true;
                if (meta.getColumnName(i).equalsIgnoreCase("label")) hasLabel = true;
            }

            int rowId = 1;
            while (rs.next()) {
                int id = hasId ? rs.getInt("id") : rowId++;
                String label = hasLabel ? rs.getString("label") : "News-" + id;
                
                DataEntity entity = new DataEntity(id, label);

                for (int i = 1; i <= columnCount; i++) {
                    String colName = meta.getColumnName(i).toLowerCase();
                    if (!colName.equalsIgnoreCase("id") && !colName.equalsIgnoreCase("label")) {
                        try {
                            double val = rs.getDouble(i);
                            switch (colName) {
                                case "num_imgs": colName = "a_r"; break;
                                case "num_hrefs": colName = "a_h"; break;
                                case "n_unique_tokens": colName = "t_r"; break;
                                case "average_token_length": colName = "w_l"; break;
                                case "global_subjectivity": colName = "a_s"; break;
                                case "global_sentiment_polarity": colName = "a_e"; break;
                                case "title_subjectivity": colName = "t_s"; break;
                                case "title_sentiment_polarity": colName = "t_e"; break;
                                case "rate_positive_words": colName = "p"; break;
                                case "shares": colName = "s"; break;
                            }
                            entity.addAttribute(colName, val);
                        } catch (SQLException e) {
                            // skip non-numeric
                        }
                    }
                }
                list.add(entity);
            }

        } catch (SQLException e) {
            System.err.println("Database query failed: " + e.getMessage() + ". Generating mock data instead.");
        }

        return list;
    }
}
