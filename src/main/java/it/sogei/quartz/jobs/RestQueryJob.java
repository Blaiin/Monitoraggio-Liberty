package it.sogei.quartz.jobs;

import it.sogei.data_access.shared.RestDataCache;
import it.sogei.utils.exceptions.QueryFailureException;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.*;
import java.util.*;

@Slf4j
public non-sealed class RestQueryJob implements IQueryJob {

    @Override
    public void execute (JobExecutionContext jobExecutionContext) throws JobExecutionException {
        DBInfo dbInfo = buildDBInfo(jobExecutionContext);
        loadDriver(dbInfo.url());
        try (var resultSet = queryDB(dbInfo)) {
            if (resultSet != null) {
                Collection<?> results = processResultSet(dbInfo.query, resultSet);
                if (results instanceof List && !results.isEmpty()) {
                    log.info("Data found.");
                    RestDataCache.put(jobExecutionContext
                            .getJobDetail()
                            .getJobDataMap()
                            .getString("id"), (List<?>) results);
                } else {
                    log.error("No data found.");
                }
            }
        } catch (NullPointerException e) {
            log.error("Result set was null, skipping", e);
            throw new JobExecutionException(e);
        } catch (Exception e) {
            log.error("Failed to execute query", e);
            throw new JobExecutionException(e);
        }
    }
    private Collection<?> processResultSet(String query, ResultSet resultSet) {
        List<Map<?, ?>> results = new ArrayList<>();
        boolean count = query.trim().toLowerCase().startsWith("select count");
        boolean select = query.trim().toLowerCase().startsWith("select");
        if (count) {
            try {
                while (resultSet.next()) {
                    results.add(Map.of("count", resultSet.getInt(1)));
                }
            } catch (SQLException e) {
                log.error("Failed to process result set", e);
            }
        }
        else if (select) {
            try {
                int columns = resultSet.getMetaData().getColumnCount();
                while (resultSet.next()) {
                    for (int i = 1; i <= columns; i++) {
                        results.add(Map.of(resultSet.getMetaData().getColumnName(i), resultSet.getObject(i)));
                    }
                }
            } catch (SQLException e) {
                log.error("Failed to process result set", e);
                results.add(Map.of("error", "Failed to process result set: " + e.getMessage()));
            }
        } else {
            log.error("Query type not supported.");
            results.add(Map.of("error", "Query type not supported."));
        }

        return results;
    }
    private ResultSet queryDB(DBInfo dbInfo) {
        Objects.requireNonNull(dbInfo.url);
        Objects.requireNonNull(dbInfo.username);
        Objects.requireNonNull(dbInfo.password);
        Objects.requireNonNull(dbInfo.query);
        try (Connection connection = DriverManager.getConnection(dbInfo.url, dbInfo.username, dbInfo.password)) {
            log.info("Connection established successfully.");
            PreparedStatement statement = connection.prepareStatement(dbInfo.query);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet != null) {
                log.info("Query executed successfully.");
                return resultSet;
            }
            else {
                log.error("Query failed.");
                throw new QueryFailureException("Query failed.");
            }
        } catch (SQLException e) {
            log.error("Failed to execute query.", e);
            return null;
        } catch (NullPointerException e) {
            if (e.getMessage().contains("password"))
                log.error("Connection was not possible.", e);
            else log.error("Failed to execute query.", e);
            return null;
        } catch (QueryFailureException e) {
            log.error("Failed to execute query", e);
            return null;
        }
    }
    private DBInfo buildDBInfo(JobExecutionContext context) {
        var dataMap = context.getJobDetail().getJobDataMap();
        var id = dataMap.getString("id");
        return new DBInfo(dataMap.getString("url"), dataMap.getString("username"),
                dataMap.getString("password"), dataMap.getString(id + "query"));
    }
    record DBInfo(String url, String username, String password, String query) {}

}
