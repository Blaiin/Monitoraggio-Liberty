package it.dmi.utils;

import it.dmi.structure.exceptions.impl.persistence.InvalidCredentialsException;
import it.dmi.structure.internal.info.DBInfo;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.Objects;

@Slf4j
public class NullChecks {


    public static void requireNonNull(DBInfo dbInfo) throws InvalidCredentialsException {
        Objects.requireNonNull(dbInfo);
        checkDBComponents(dbInfo);
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
