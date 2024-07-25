package it.sogei.utils;

import it.sogei.quartz.ejb.ManagerEJB;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NullChecks {

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
