package it.dmi.quartz.jobs.sql;

import it.dmi.data.dto.OutputDTO;
import it.dmi.structure.exceptions.impl.persistence.DatabaseConnectionException;
import it.dmi.structure.exceptions.impl.persistence.InvalidCredentialsException;
import it.dmi.structure.exceptions.impl.persistence.QueryFailureException;
import it.dmi.structure.internal.info.DBInfo;
import it.dmi.structure.internal.info.Info;
import it.dmi.utils.NullChecks;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Objects;

import static it.dmi.utils.constants.NamingConstants.*;

public interface ISQLJob extends Job {

    Logger log = LoggerFactory.getLogger(ISQLJob.class);

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
                log.info("Query executed successfully.");
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

    default OutputDTO initializeOutputDTO(String id, LocalDateTime inizio) {
        return OutputDTO.builder()
                .configurazioneId(Long.valueOf(id))
                .inizio(Timestamp.valueOf(inizio))
                .build();
    }

    default void resolveException (Throwable e) throws JobExecutionException {
        switch (e) {
            case QueryFailureException qfE -> log.error("Error while executing query. {}", qfE.getMessage());
            case DatabaseConnectionException dcE -> log.error("Error while connecting to database. {}", dcE.getMessage());
            case InvalidCredentialsException icE -> log.error("Could not connect to database. {}", icE.getMessage());
            case SQLException sqlE -> log.error("Query execution had problems. {}", sqlE.getMessage());
            default -> log.error("{} [Nested exception: {}]", e.getMessage(), e.getCause());
        }

        throw new JobExecutionException(e);
    }

    default void loadDriver(Info dbInfo) {
        if(dbInfo == null) {
            log.error("Database info could not be retrieved or read.");
            throw new IllegalArgumentException("Database info could not be retrieved or read.");
        }
        String driverName = dbInfo.driverName();
        if(driverName == null) {
            log.error("Driver name cannot be null.");
            throw new IllegalArgumentException("Driver name cannot be null.");
        }
        if (driverName.contains("postgres")) {
            log.info("Loading PostgreSQL driver.");
            try {
                Class.forName("org.postgresql.Driver");
                log.info("PostgreSQL driver loaded successfully.");
            } catch (ClassNotFoundException e) {
                log.error("Failed to load PostgreSQL driver.", e);
                throw new RuntimeException(e);
            }
        } else if (driverName.contains("oracle")) {
            log.info("Loading Oracle driver.");
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                log.info("Oracle driver loaded successfully.");
            } catch (ClassNotFoundException e) {
                log.error("Failed to load Oracle driver.", e);
                throw new RuntimeException(e);
            }
        } else {
            log.error("Unsupported database type.");
            throw new IllegalArgumentException("Unsupported database type.");
        }
    }
}
