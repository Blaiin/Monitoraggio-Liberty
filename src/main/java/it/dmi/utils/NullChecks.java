package it.dmi.utils;

import it.dmi.structure.exceptions.impl.persistence.InvalidCredentialsException;
import it.dmi.structure.exceptions.impl.quartz.JobBuildingException;
import it.dmi.structure.internal.info.DBInfo;
import it.dmi.structure.internal.info.JobInfo;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Objects;

@Slf4j
public class NullChecks {

    public static boolean requireNonNull(JobInfo jobInfo) {
        try {
            Objects.requireNonNull(jobInfo);
            return checkJobComponents(jobInfo);
        } catch (JobBuildingException | NullPointerException e) {
            return false;
        }
    }
    public static void requireNonNull(DBInfo dbInfo) throws InvalidCredentialsException {
        Objects.requireNonNull(dbInfo);
        checkDBComponents(dbInfo);
    }

    private static boolean checkJobComponents(JobInfo info) throws JobBuildingException {
        if(info.jobDetail() == null || info.trigger() == null) {
            log.error("JobInfo has not allowed null values.");
            throw new JobBuildingException("Necessary info (detail or trigger) not built properly.");
        }
        return true;
    }

    private static void checkDBComponents(DBInfo info) throws InvalidCredentialsException {
        Class<?> clazz = info.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(info);
                if (value == null) {
                    log.error("Credentials are null, database connection will fail");
                    throw new InvalidCredentialsException("Invalid or null credentials for Database connection.");
                }
            } catch (IllegalAccessException e) {
                log.error("Error accessing field '{}': {}", field.getName(), e.getMessage());
                return;
            }
        }
    }

    public static boolean nullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

}
