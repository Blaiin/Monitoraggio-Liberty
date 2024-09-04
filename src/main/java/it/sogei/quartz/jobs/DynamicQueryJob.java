package it.sogei.quartz.jobs;

import it.sogei.data_access.shared.RestDataCache;
import it.sogei.utils.NullChecks;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public non-sealed class DynamicQueryJob implements IQueryJob {

    @Override
    public void execute (JobExecutionContext jobExecutionContext) throws JobExecutionException {
        DBInfo dbInfo = buildDBInfo(jobExecutionContext);
        loadDriver(dbInfo);
        try (var resultSet = queryDB(dbInfo)) {
            List<String> result = new ArrayList<>();
            if (resultSet != null) {
                log.info("Reading result set...");
                int i = 1;
                do {
                    while (resultSet.next()) {
                        result.add(resultSet.getString(i));
                    }
                    i++;
                } while (i < resultSet.getMetaData().getColumnCount());
            }
            if (!result.isEmpty()) {
                log.info("Data found.");
                RestDataCache.put(jobExecutionContext.getJobDetail().getJobDataMap().getString("id"), result);
            } else {
                log.error("No data found.");
            }
        } catch (NullPointerException e) {
            log.error("Result set was null, skipping", e);
            throw new JobExecutionException(e);
        } catch (IllegalArgumentException e) {
            log.error("Query execution failed due to driver not being able to load.", e);
            throw new JobExecutionException(e);
        } catch (Exception e) {
            log.error("Failed to execute query", e);
            throw new JobExecutionException(e);
        }
    }

    private ResultSet queryDB(DBInfo dbInfo) {
        NullChecks.requireNonNull(dbInfo);
        try (Connection connection = DriverManager.getConnection(dbInfo.url, dbInfo.username, dbInfo.password)) {
            log.info("Connection established successfully.");
            PreparedStatement statement = connection.prepareStatement(dbInfo.sqlScript);
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
            log.error("Failed to execute query.", e);
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
        return new DBInfo(dataMap.getString(id + "driverName"),
                            dataMap.getString(id + "url"),
                            dataMap.getString(id + "username"),
                            dataMap.getString(id + "password"),
                            dataMap.getString(id + "sqlScript"));
    }
    public record DBInfo(String driverName, String url, String username, String password, String sqlScript) {}
}
