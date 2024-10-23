package it.dmi.structure.internal.info;

public sealed interface Info permits DBInfo, JobInfo{

    default String driverName() {
        return null;
    }
}
