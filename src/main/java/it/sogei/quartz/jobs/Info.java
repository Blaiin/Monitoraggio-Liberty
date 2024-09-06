package it.sogei.quartz.jobs;

public sealed interface Info permits DynamicQueryJob.DBInfo, InternalQueryJob.DBInfo {
}
