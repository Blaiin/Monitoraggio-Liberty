package it.dmi.structure.internal;

import it.dmi.quartz.jobs.Info;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
public record DBInfo(@Getter(AccessLevel.NONE) String driverName, String url, String user, String password,
                     String sqlScript) implements Info {

}
