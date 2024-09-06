package it.sogei.utils;

import it.sogei.quartz.ejb.ManagerEJB;
import it.sogei.quartz.jobs.DynamicQueryJob;
import it.sogei.quartz.jobs.Info;
import it.sogei.quartz.jobs.InternalQueryJob;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;

@Slf4j
public class NullChecks {

    public static void requireNonNull(Info info) {
        switch (info) {
            case DynamicQueryJob.DBInfo dbInfo ->
                    checkComponents(dbInfo.getClass().getRecordComponents(), dbInfo);
            case InternalQueryJob.DBInfo dbInfo ->
                    checkComponents(dbInfo.getClass().getRecordComponents(), dbInfo);
            default -> log.error("DBInfo is null.");
        }
    }

    public static boolean requireNonNull(ManagerEJB.JobInfo info) {
        if(info.jobDetail() == null || info.trigger() == null) {
            log.error("JobInfo has not allowed null values.");
            return false;
        }
        return true;
    }

    private static void checkComponents(RecordComponent[] components, Object clasz) {
        try {
            for (RecordComponent component : components) {
                Object value = component.getAccessor().invoke(clasz);
                if (value == null) {
                    log.error("Component '{}' is null.", component.getName());
                    return;
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Error: {}", e.getMessage());
        }
    }

}
