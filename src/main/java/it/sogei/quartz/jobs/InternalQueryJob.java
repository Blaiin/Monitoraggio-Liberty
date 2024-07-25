package it.sogei.quartz.jobs;

import it.sogei.data_access.shared.RestDataCache;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.*;
import java.util.*;

@Slf4j
public non-sealed class InternalQueryJob implements IQueryJob{

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        DBInfo dbInfo = buildDBInfo(jobExecutionContext);
        loadDriver();
        try (var resultSet = queryDB(dbInfo)) {
            List<String> names = new ArrayList<>();
            if (resultSet != null) {
                log.info("Reading result set...");
                while (resultSet.next()) {
                    names.add(resultSet.getString("name"));
                }
            }
            if (!names.isEmpty()) {
                log.info("Data found.");
                RestDataCache.put(jobExecutionContext.getJobDetail().getJobDataMap().getString("id"), names);
            } else {
                log.error("No data found.");
            }
        } catch (NullPointerException e) {
            log.error("Result set was null, skipping", e);
            throw new JobExecutionException(e);
        } catch (Exception e) {
            log.error("Failed to execute query", e);
            throw new JobExecutionException(e);
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
