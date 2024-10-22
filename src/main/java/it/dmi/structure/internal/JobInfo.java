package it.dmi.structure.internal;

import org.quartz.JobDetail;
import org.quartz.Trigger;

public record JobInfo(JobDetail jobDetail, Trigger trigger) {

}
