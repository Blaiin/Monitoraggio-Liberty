package it.dmi.quartz.jobs.sql;

import it.dmi.caches.JobDataCache;
import it.dmi.data.dto.OutputDTO;
import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import it.dmi.structure.exceptions.impl.persistence.DatabaseConnectionException;
import it.dmi.structure.exceptions.impl.persistence.InvalidCredentialsException;
import it.dmi.structure.exceptions.impl.persistence.QueryFailureException;
import it.dmi.structure.internal.info.DBInfo;
import it.dmi.utils.NullChecks;
import it.dmi.utils.TimeUtils;
import it.dmi.utils.Utils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Map;
import java.util.Objects;

import static it.dmi.structure.internal.Esito.NEGATIVE;
import static it.dmi.structure.internal.Esito.POSITIVE;
import static it.dmi.utils.constants.NamingConstants.*;

public interface ISQLJob extends Job {

    Logger log = LoggerFactory.getLogger(ISQLJob.class);

    Map<String, String> DRIVER_MAP = Map.of(
            "postgres", "org.postgresql.Driver",
            "oracle", "oracle.jdbc.driver.OracleDriver"
    );

    default ResultSet queryDB(DBInfo dbInfo) throws DatabaseConnectionException,
            InvalidCredentialsException, QueryFailureException {
        NullChecks.requireNonNull(dbInfo);
        try (Connection connection = connect(dbInfo)) {
            log.debug("Connection established successfully.");
            Objects.requireNonNull(connection,
                    String.format("Could not connect to database: %s.", dbInfo.url()));
            PreparedStatement statement = connection.prepareStatement(dbInfo.sqlScript());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet != null) {
                log.debug("Query executed successfully.");
                return resultSet;
            } else {
                throw new QueryFailureException("Query failed, result set was null.");
            }
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    default Connection connect(DBInfo dbInfo) throws DatabaseConnectionException {
        try {
            return DriverManager.getConnection(dbInfo.url(), dbInfo.user(), dbInfo.password());
        } catch (SQLException e) {
            log.error("Failed to connect to database.", e);
            throw new DatabaseConnectionException(e);
        }
    }

    default DBInfo buildDBInfo(JobDataMap map) {
        String id = map.getString(ID);
        return new DBInfo(map.getString(DRIVER_NAME + id),
                map.getString(URL + id),
                map.getString(USERNAME + id),
                map.getString(PASSWORD + id),
                map.getString(SQL_SCRIPT + id));
    }

    default OutputDTO initializeOutputDTO (QuartzTask task) {
        OutputDTO out = new OutputDTO();
        out.setInizio(TimeUtils.now());
        switch (task) {
            case Azione a -> out.setAzioneId(a.getId());
            case Configurazione c -> out.setConfigurazioneId(c.getId());
        }
        return out;
    }

    default OutputDTO finalizeOutputDTO(OutputDTO out, Map<String, ?> results) {
        if (out.getConfigurazioneId() != null)
            log.debug("Finalizing (select) output for Config {}", out.getConfigurazioneId());
        if (out.getAzioneId() != null)
            log.debug("Finalizing (select) output for Azione {}", out.getAzioneId());
        if(results.isEmpty()) {
            if (out.getConfigurazioneId() != null)
                log.warn("Esito (select) was negative for Config {}", out.getConfigurazioneId());
            if (out.getAzioneId() != null)
                log.warn("Esito (select) was negative for Azione {}", out.getAzioneId());
            out.setEsito(NEGATIVE.getValue());
        }
        else out.setEsito(POSITIVE.getValue());
        out.setContenuto(results);
        var fine = TimeUtils.now();
        out.setFine(fine);
        out.setDurata(TimeUtils.duration(out.getInizio(), fine));
        return out;
    }

    default OutputDTO finalizeOutputDTO(OutputDTO out, int result) {
        if (out.getConfigurazioneId() != null)
            log.debug("Finalizing (select count) output for Config {}", out.getConfigurazioneId());
        if (out.getAzioneId() != null)
            log.debug("Finalizing (select count) output for Azione {}", out.getAzioneId());
        if(result == 0) {
            if (out.getConfigurazioneId() != null)
                log.warn("Esito (select count) was negative for Config {}", out.getConfigurazioneId());
            if (out.getAzioneId() != null)
                log.warn("Esito (select count) was negative for Azione {}", out.getAzioneId());
            out.setEsito(NEGATIVE.getValue());
        }
        else out.setEsito(POSITIVE.getValue());
        out.setContenuto(Map.of("count", result));
        var fine = TimeUtils.now();
        out.setFine(fine);
        out.setDurata(TimeUtils.duration(out.getInizio(), fine));
        return out;
    }

    default void cacheOutputDTO(String id, OutputDTO out) {
        if (out.getAzioneId() == null && out.getConfigurazioneId() == null) {
            log.error("Invalid generated output, both id fields were null");
            return;
        }
        if (out.getConfigurazioneId() != null) {
            log.debug("Caching output for Config {}", out.getConfigurazioneId());
            JobDataCache.put(OUTPUT + id, out);
            return;
        }
        log.debug("Caching output for Azione {}", out.getAzioneId());
        JobDataCache.put(OUTPUT + id, out);
    }

    default void resolveException (Throwable e) throws JobExecutionException {
        switch (e) {
            case QueryFailureException qfE -> {
                final String msg = "Error while executing query. " + qfE.getMessage();
                log.error(msg, qfE);
                throw new JobExecutionException(msg, e);
            }
            case DatabaseConnectionException dcE -> {
                final String msg = "Error while connecting to database. " + dcE.getMessage();
                log.error(msg, dcE);
                throw new JobExecutionException(msg, e);
            }
            case InvalidCredentialsException icE -> {
                final String msg = "Could not connect to database. " + icE.getMessage();
                log.error(msg, icE);
                throw new JobExecutionException(e);
            }
            case SQLException sqlE -> {
                final String msg = "Query execution had problems. " + sqlE.getMessage();
                log.error(msg, sqlE);
                throw new JobExecutionException(e);
            }
            case JobExecutionException jeE -> {
                final String msg = "Jobs encountered an error while executing " + jeE.getMessage();
                log.error(msg, jeE);
                throw new JobExecutionException(e);
            }
            default -> {
                final String msg = String.format("%s [Nested exception: %s]", e.getMessage(), e);
                log.error(msg);
                throw new JobExecutionException(e);
            }
        }
    }

    default void loadDriver(DBInfo dbInfo) {
        if (dbInfo == null) {
            throw logAndThrow("Database info could not be retrieved or read.");
        }
        String driverName = dbInfo.driverName();
        if (driverName == null) {
            throw logAndThrow("Driver name cannot be null.");
        }

        Map.Entry<String, String> driverClass = DRIVER_MAP.entrySet().stream()
                .filter(entry -> driverName.contains(entry.getKey()))
                .findFirst()
                .orElseThrow(() -> logAndThrow("Unsupported database type."));
        loadDriverClass(driverClass);
    }

    private RuntimeException logAndThrow(String message) {
        log.error(message);
        throw new IllegalArgumentException(message);
    }

    private void loadDriverClass(Map.Entry<String, String> entry) {
        try {
            Class.forName(entry.getValue());
            log.debug("{} driver loading was successful.", Utils.Strings.capitalize(entry.getKey()));
        } catch (ClassNotFoundException e) {
            log.error("Loading driver class failed: {}", Utils.Strings.capitalize(entry.getKey()), e);
            throw new RuntimeException(e);
        }
    }

}
