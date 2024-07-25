package it.sogei.quartz.jobs;

import org.quartz.Job;

public sealed interface IQueryJob extends Job permits InternalQueryJob, RestQueryJob, QueryJob {
    default void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
