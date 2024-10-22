package it.dmi.structure.internal;

import it.dmi.quartz.jobs.Info;

public record DBInfo(String driverName, String url, String user, String password,
                     String sqlScript) implements Info {

}
