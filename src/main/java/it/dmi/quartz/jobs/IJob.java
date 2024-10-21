package it.dmi.quartz.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface IJob extends org.quartz.Job {

    Logger log = LoggerFactory.getLogger(IJob.class);

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
