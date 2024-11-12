package it.dmi.structure.internal.info;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.quartz.JobDataMap;

import static it.dmi.utils.constants.NamingConstants.*;

@Slf4j
public record DBInfo(String driverName, String url, String user, String password,
                     String sqlScript, String jndi) implements Info {

    public static @Nullable DBInfo create(JobDataMap map) {
        try {
            String id = map.getString(ID),
                    jndi = map.getString(JNDI + id),
                    driver = map.getString(DRIVER_NAME + id),
                    url = map.getString(URL + id),
                    username = map.getString(USERNAME + id),
                    password = map.getString(PASSWORD + id),
                    script = map.getString(SQL_SCRIPT + id);
            return new DBInfo(
                    driver,
                    url,
                    username,
                    password,
                    script,
                    jndi);
        } catch (NullPointerException e) {
            log.error("Error while generating components intended for connection to database. {}",
                    e.getMessage(), e);
            return null;
        }
    }
}

