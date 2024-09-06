package it.sogei.quartz.jobs;

import it.sogei.data_access.shared.RestDataCache;
import it.sogei.structure.data.dto.OutputDTO;
import it.sogei.utils.NullChecks;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static it.sogei.utils.constants.NamingConstants.*;

@Slf4j
public non-sealed class DynamicQueryJob implements IQueryJob {

    @Override
    public void execute (JobExecutionContext jobExecutionContext) throws JobExecutionException {
        var dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        var id = dataMap.getString("id");
        log.info("Trying to execute job for Configurazione named: {}.",
                dataMap.getString("nome"));
        DBInfo dbInfo = buildDBInfo(dataMap);
        loadDriver(dbInfo);
        var output = new OutputDTO();
        var inizio = LocalDateTime.now();
        output.setInizio(Timestamp.valueOf(inizio));
        output.setConfigurazioneId(Long.valueOf(id));
        try (var resultSet = queryDB(dbInfo)) {
            List<String> results = new ArrayList<>();
            if (resultSet != null) {
                log.info("Reading result set...");
                int i = 1;
                do {
                    while (resultSet.next()) {
                        results.add(resultSet.getString(i));
                    }
                    i++;
                } while (i < resultSet.getMetaData().getColumnCount());
            }
            if (!results.isEmpty()) {
                log.info("Data found.");
                var fine = LocalDateTime.now();
                output.setEsito('0');
                output.setContenuto(results);
                output.setFine(Timestamp.valueOf(fine));
                output.setDurata(Duration.between(inizio, fine).toSeconds());
                RestDataCache.put(id, results);
                RestDataCache.put(OUTPUT + id, Collections.singletonList(output));
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
        try (Connection connection = DriverManager
                .getConnection(dbInfo.url(),
                                dbInfo.username(),
                                dbInfo.password())) {
            log.info("Connection established successfully.");
            PreparedStatement statement = connection.prepareStatement(dbInfo.sqlScript());
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
            if (e.getMessage().contains(USERNAME))
                log.error("Connection to database not possible, invalid username.", e);
            else if (e.getMessage().contains(PASSWORD))
                log.error("Connection to database not possible, invalid password.", e);
            else log.error("Unknown error, failed to execute query", e);
            return null;
        }
    }

    private DBInfo buildDBInfo(JobDataMap map) {
        var id = map.getString("id");
        return new DBInfo(map.getString("driverName" + id),
                            map.getString("url" + id),
                            map.getString("username" + id),
                            map.getString("password" + id),
                            map.getString("sqlScript" + id));
    }
    public record DBInfo(String driverName, String url, String username, String password, String sqlScript)
            implements Info {}
}
