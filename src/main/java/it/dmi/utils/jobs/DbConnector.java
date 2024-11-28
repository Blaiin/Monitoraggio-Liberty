package it.dmi.utils.jobs;

import it.dmi.structure.exceptions.impl.persistence.DatabaseConnectionException;
import it.dmi.structure.internal.info.DBInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class DbConnector {

    public static @Nullable Connection connect(@NotNull DBInfo dbInfo)
            throws DatabaseConnectionException {
        try {
            if (!dbInfo.jndi().isEmpty()) {
                DataSource source = (DataSource) new InitialContext().lookup(dbInfo.jndi());
                if (source != null) return source.getConnection();
                else log.debug("No JNDI was provided.");
            }
            //Fallback to classic db connection method
            if ((dbInfo.url() != null && !dbInfo.url().isEmpty()) &&
                    (dbInfo.user() != null && !dbInfo.user().isEmpty()) &&
                    (dbInfo.password() != null && !dbInfo.password().isEmpty()))
                return DriverManager.getConnection(dbInfo.url(), dbInfo.user(), dbInfo.password());
            final String msg = "Could not make a connection to database, " +
                    "neither JNDI or canonical parameters were valid";
            log.error(msg);
            throw new DatabaseConnectionException(msg);
        } catch (SQLException | NamingException exc) {
            log.error("Failed to connect to database.", exc);
            throw new DatabaseConnectionException(exc);
        }
    }

}
