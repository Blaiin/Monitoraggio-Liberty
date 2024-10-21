package it.dmi.quartz.jobs;

import it.dmi.data_access.shared.RestDataCache;
import it.dmi.structure.data.dto.OutputDTO;
import it.dmi.structure.data.entities.Configurazione;
import it.dmi.utils.NullChecks;
import it.dmi.utils.ResultsProcessor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static it.dmi.utils.constants.Esito.POSITIVE;
import static it.dmi.utils.constants.NamingConstants.*;

@Slf4j
public class SelectJob implements IJob {

    private final ResultsProcessor resultsProcessor = new ResultsProcessor();

    @Override
    public void execute (JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String id = dataMap.getString(ID);
        Configurazione config = (Configurazione) dataMap.get(CONFIG);
        log.info("Trying to execute job for Configurazione id: {}, name: {}.",
                id, dataMap.getString(NOME));
        DBInfo dbInfo = buildDBInfo(dataMap);
        loadDriver(dbInfo);
        OutputDTO output = new OutputDTO();
        LocalDateTime inizio = LocalDateTime.now();
        output.setInizio(Timestamp.valueOf(inizio));
        output.setConfigurazioneId(Long.valueOf(id));
        try (ResultSet resultSet = queryDB(dbInfo)) {
            Map<String, List<Object>> results = resultsProcessor.processSelectResultSet(resultSet);
            if (!results.isEmpty()) {
                log.info("Data found.");
                LocalDateTime fine = LocalDateTime.now();
                output.setEsito(POSITIVE.getValue());
                output.setContenuto(results);
                output.setFine(Timestamp.valueOf(fine));
                output.setDurata(Duration.between(inizio, fine).getSeconds());
                RestDataCache.put(id, results);
                RestDataCache.put(OUTPUT + id, Collections.singletonList(output));
            } else {
                log.error("No data found.");
            }
        } catch (NullPointerException e) {
            if (e.getMessage().contains("ResultSet")) {
                log.error("Result set was null, skipping.", e);
            } else {
                log.error("Unknown error, failed to execute query.", e);
            }
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
        try (Connection connection = connect(dbInfo)) {
            log.info("Connection established successfully.");
            Objects.requireNonNull(connection,
                    String.format("Could not connect to database: %s.", dbInfo.getUrl()));
            PreparedStatement statement = connection.prepareStatement(dbInfo.getSqlScript());
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

    private Connection connect(DBInfo dbInfo) {
        try {
            return DriverManager.getConnection(dbInfo.getUrl(), dbInfo.getUser(), dbInfo.getPassword());
        } catch (SQLException e) {
            log.error("Failed to connect to database.", e);
            return null;
        }
    }

    private DBInfo buildDBInfo(JobDataMap map) {
        String id = map.getString(ID);
        return new DBInfo(map.getString(DRIVER_NAME + id),
                            map.getString(URL + id),
                            map.getString(USERNAME + id),
                            map.getString(PASSWORD + id),
                            map.getString(SQL_SCRIPT + id));
    }
}
