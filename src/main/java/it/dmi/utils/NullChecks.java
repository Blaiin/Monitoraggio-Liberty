package it.dmi.utils;

import it.dmi.quartz.jobs.Info;
import it.dmi.structure.internal.JobInfo;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Objects;

@Slf4j
public class NullChecks {

    public static void requireNonNull(Info info) {
        Objects.requireNonNull(info);
        checkComponents(info);
    }

    public static boolean requireNonNull(JobInfo info) {
        if(info.jobDetail() == null || info.trigger() == null) {
            log.error("JobInfo has not allowed null values.");
            return false;
        }
        return true;
    }

    private static void checkComponents(Object clasz) {
        Class<?> clazz = clasz.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(clasz);
                if (value == null) {
                    log.error("Field '{}' in class '{}' is null.", field.getName(), clazz.getSimpleName());
                    return;
                }
            } catch (IllegalAccessException e) {
                log.error("Error accessing field '{}': {}", field.getName(), e.getMessage());
            }
        }
    }

}
