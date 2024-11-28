package it.dmi.quartz.jobs.sql;

import it.dmi.structure.exceptions.impl.internal.InvalidStateException;
import it.dmi.structure.exceptions.impl.persistence.DatabaseConnectionException;
import it.dmi.structure.exceptions.impl.persistence.InvalidCredentialsException;
import it.dmi.structure.exceptions.impl.persistence.QueryFailureException;
import it.dmi.structure.internal.info.DBInfo;
import it.dmi.utils.Utils;
import it.dmi.utils.jobs.DbConnector;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.quartz.JobExecutionException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;

@Slf4j
public abstract class BaseSQLJob {

    private final Map<String, String> DRIVER_MAP = Map.of(
            "postgres", "org.postgresql.Driver",
            "oracle", "oracle.driver.OracleDriver"
    );

    protected void resolveException(@NotNull Throwable exc) throws JobExecutionException {
        final String msg = switch (exc) {
            case QueryFailureException qfE -> "Error while executing query. " + qfE.getMessage();
            case DatabaseConnectionException dcE -> "Error while connecting to database. " + dcE.getMessage();
            case InvalidCredentialsException icE -> "Could not connect to database. " + icE.getMessage();
            case SQLException sqlE -> "Query execution had problems. " + sqlE.getMessage();
            case JobExecutionException jeE -> "Jobs encountered an error while executing. " + jeE.getMessage();
            case NullPointerException npE -> "Necessary value was null. " + npE.getMessage();
            case InvalidStateException isE -> "Active state for object was illegal." + isE.getMessage();
            default -> String.format("Nested exception: %s", exc.getMessage());
        };
        handleException(msg, exc);
    }

    protected ResultSet queryDB(DBInfo dbInfo) throws DatabaseConnectionException,
            InvalidCredentialsException, QueryFailureException {
        if (dbInfo == null)
            throw new InvalidCredentialsException("Necessary values for DB Connection were null, check logs");
        try (Connection connection = DbConnector.connect(dbInfo)){
            log.debug("Connection established successfully.");
            Objects.requireNonNull(connection,
                    String.format("Could not connect to database: %s.", dbInfo.url()));
            PreparedStatement statement = connection.prepareStatement(dbInfo.sqlScript());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet != null) {
                log.debug("Query executed successfully.");
                return resultSet;
            } else throw new QueryFailureException("Query failed, result set was null.");
        } catch (SQLException e) {
            throw new DatabaseConnectionException(e);
        }
    }

    protected void loadDriver(DBInfo dbInfo) {
        if (dbInfo != null) {
            Map.Entry<String, String> driverClass = DRIVER_MAP.entrySet().stream()
                    .filter(entry -> dbInfo.driverName().contains(entry.getKey()))
                    .findFirst()
                    .orElseThrow(() -> raiseException("Unsupported database type."));
            loadDriverClass(driverClass);
            return;
        }
        throw raiseException("Not able to load driver class.");
    }

    private RuntimeException raiseException(String message) {
        log.error(message);
        throw new IllegalArgumentException(message);
    }

    private void loadDriverClass(Map.@NotNull Entry<String, String> entry) {
        try {
            Class.forName(entry.getValue());
            log.debug("{} driver loading was successful.", Utils.Strings.capitalize(entry.getKey()));
        } catch (ClassNotFoundException e) {
            log.error("Loading driver class failed: {}", Utils.Strings.capitalize(entry.getKey()), e);
            throw new RuntimeException(e);
        }
    }

    private void handleException(String msg, Throwable exc) throws JobExecutionException {
        log.error(msg, exc);
        throw new JobExecutionException(msg, exc);
    }

}
