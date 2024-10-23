package it.dmi.structure.internal.info;

import org.quartz.JobDetail;
import org.quartz.Trigger;

public record JobInfo(JobDetail jobDetail, Trigger trigger) implements Info {
}
