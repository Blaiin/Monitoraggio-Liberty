package it.dmi.structure.internal.info;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;

import java.util.Objects;

import static it.dmi.utils.constants.NamingConstants.*;

@Slf4j
public record DBInfo(String driverName, String url, String user, String password,
                     String sqlScript) implements Info {

    public static DBInfo build(JobDataMap map) {
        try {
            String id = map.getString(ID), driver = map.getString(DRIVER_NAME + id),
                    url = map.getString(URL + id), username = map.getString(USERNAME + id),
                    password = map.getString(PASSWORD + id), script = map.getString(SQL_SCRIPT + id);
            return new DBInfo(
                    Objects.requireNonNull(driver, "Driver name cannot be null."),
                    Objects.requireNonNull(url, "URL cannot be null."),
                    Objects.requireNonNull(username, "Username cannot be null."),
                    Objects.requireNonNull(password, "Password cannot be null."),
                    Objects.requireNonNull(script, "Script was null, cannot connect to do nothing."));
        } catch (NullPointerException e) {
            log.error("Error while generating components intended for connection to database. {}",
                    e.getMessage(), e);
            return null;
        }
    }
}

