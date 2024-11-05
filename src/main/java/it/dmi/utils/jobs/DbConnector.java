package it.dmi.utils.jobs;

import it.dmi.structure.exceptions.impl.persistence.DatabaseConnectionException;
import it.dmi.structure.internal.info.DBInfo;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class DbConnector {

    public static Connection connect(DBInfo dbInfo) throws DatabaseConnectionException {
        try {
            return DriverManager.getConnection(dbInfo.url(), dbInfo.user(), dbInfo.password());
        } catch (SQLException e) {
            log.error("Failed to connect to database.", e);
            throw new DatabaseConnectionException(e);
        }
    }

}
