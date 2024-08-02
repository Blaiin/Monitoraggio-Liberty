package it.sogei.utils;

import it.sogei.quartz.ejb.ManagerEJB;
import it.sogei.quartz.jobs.InternalQueryJob;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NullChecks {

    public static boolean requireNonNull(InternalQueryJob.DBInfo dbInfo) {
        if (dbInfo.url() == null) {
            log.error("Url is null.");
            return false;
        }
        if (dbInfo.username() == null) {
            log.error("Username is null.");
            return false;
        }
        if (dbInfo.password() == null) {
            log.error("Password is null.");
            return false;
        }
        if (dbInfo.query() == null) {
            log.error("Query is null.");
            return false;
        }
        return true;
    }

    public static boolean requireNonNull(ManagerEJB.JobInfo jobInfo) {
        if (jobInfo == null) {
            log.error("JobInfo is null.");
            return false;
        }
        if (jobInfo.jobDetail() == null) {
            log.error("JobDetail is null.");
            return false;
        }
        if (jobInfo.trigger() == null) {
            log.error("Trigger is null.");
            return false;
        }
        return true;
    }
}
