package it.dmi.processors;

import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
public class ResultsProcessor {

    public static Map<String, List<Object>> processSelectResultSet(ResultSet resultSet)
            throws SQLException, NullPointerException {

        Map<String, List<Object>> results = new HashMap<>();
        Objects.requireNonNull(resultSet, "ResultSet for select query was null.");
        log.info("Reading select result set...");
        int columnCount = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                String columnName = resultSet.getMetaData().getColumnName(i);
                Object columnValue = resultSet.getObject(i);
                if (results.containsKey(columnName)) {
                    results.get(columnName).add(columnValue);
                } else {
                    List<Object> list = new ArrayList<>();
                    list.add(columnValue);
                    results.put(columnName, list);
                }
            }
        }
        return results;
    }

    public static Map<String, Integer> processCountResultSet(ResultSet set) {
        Map<String, Integer> results = new HashMap<>();
        Objects.requireNonNull(set, "ResultSet for count query was null.");
        log.debug("Reading count result set..");
        try {
            if (set.next()) {
                int count = set.getInt(1);
                results.put("count", count);
            }
        } catch (SQLException e) {
            log.error("Failed to process count result set.", e);
        }
        return results;
    }

}
