package it.sogei.quartz.jobs;

import org.quartz.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public sealed interface IQueryJob extends Job permits InternalQueryJob, RestQueryJob, QueryJob {

    Logger log = LoggerFactory.getLogger(IQueryJob.class);

    default void loadDriver(String url) {
        if(url == null) {
            log.error("URL cannot be null.");
            throw new IllegalArgumentException("URL cannot be null.");
        }
        if (url.contains("jdbc:postgresql")) {
            log.info("Loading PostgreSQL driver...");
            try {
                Class.forName("org.postgresql.Driver");
                log.info("PostgreSQL driver loaded successfully.");
            } catch (ClassNotFoundException e) {
                log.error("Failed to load PostgreSQL driver.", e);
                throw new RuntimeException(e);
            }
        } else if (url.contains("oracle")) {
            log.info("Loading Oracle driver...");
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
