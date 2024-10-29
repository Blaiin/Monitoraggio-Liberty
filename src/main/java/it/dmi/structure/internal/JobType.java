package it.dmi.structure.internal;

import lombok.Getter;

@Getter
public enum JobType {

    SQL("SQL"),
    PROGRAM("PROGRAM"),
    CLASS("CLASS"),
    OTHER("NOT VALID");
    private final String jobType;

    JobType (String jobType) {
        this.jobType = jobType;
    }
}
