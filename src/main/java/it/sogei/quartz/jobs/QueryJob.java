package it.sogei.quartz.jobs;

import it.sogei.data_access.shared.SharedDataCache;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public non-sealed class QueryJob implements IQueryJob {


    @Override
    public void execute(JobExecutionContext context) {
        DBInfo dbInfo = buildDBInfo(context);
        try {
            loadDriver();
        } catch (ClassNotFoundException e) {
            log.error("Failed to load driver", e);
        }
        List<String> names = new ArrayList<>();
        try (ResultSet resultSet = queryDB(dbInfo)) {
            Objects.requireNonNull(resultSet);
            while (resultSet.next()) {
                names.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            log.error("Failed to execute query", e);
        }
        if (!names.isEmpty()) SharedDataCache.put("names", names);
        else log.error("No data found.");

        log.info("QueryJob executed.");
    }

    private ResultSet queryDB(DBInfo dbInfo) {
        try (Connection connection = DriverManager.getConnection(dbInfo.url, dbInfo.username, dbInfo.password)) {
            log.info("Connection established successfully.");
            PreparedStatement statement = connection.prepareStatement(dbInfo.query);
            return statement.executeQuery();
        } catch (SQLException e) {
            log.error("Failed to execute query", e);
            return null;
        }
    }

    record DBInfo(String url, String username, String password, String query) {}
    
    private DBInfo buildDBInfo(JobExecutionContext context) {
        var dataMap = context.getMergedJobDataMap();
        return new DBInfo(dataMap.getString("url"), dataMap.getString("username"),
                dataMap.getString("password"), dataMap.getString("query"));
    }
}
