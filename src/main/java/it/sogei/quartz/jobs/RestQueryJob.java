package it.sogei.quartz.jobs;

import it.sogei.data_access.shared.SharedDataCache;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public non-sealed class RestQueryJob implements IQueryJob{
    @Override
    public void execute (JobExecutionContext jobExecutionContext) throws JobExecutionException {
        DBInfo dbInfo = buildDBInfo(jobExecutionContext);
        try {
            loadDriver();
        } catch (ClassNotFoundException e) {
            log.error("Failed to load driver", e);
        }

        try (var resultSet = queryDB(dbInfo)) {
            List<String> results = new ArrayList<>();
            if (resultSet != null) {
                log.info("Reading result set...");
                while (resultSet.next()) {
                    results.add(resultSet.getString("name"));
                }
            }
            if (!results.isEmpty()) {
                log.info("Data found.");
                SharedDataCache.put(jobExecutionContext.getJobDetail().getJobDataMap().getString("id"), results);
            } else {
                log.error("No data found.");
            }
        } catch (NullPointerException e) {
            log.error("Result set was null, skipping", e);
        } catch (Exception e) {
            log.error("Failed to execute query", e);
        }
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
                throw new NullPointerException("Query failed.");
            }
        } catch (SQLException e) {
            log.error("Failed to execute query", e);
            return null;
        } catch (NullPointerException e) {
            if (e.getMessage().contains("password"))
                log.error("Connection was not possible.", e);
            else log.error("Failed to execute query", e);
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
