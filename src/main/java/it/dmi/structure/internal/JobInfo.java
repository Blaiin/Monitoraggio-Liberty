package it.dmi.structure.internal;

import org.quartz.JobDetail;
import org.quartz.Trigger;

public class JobInfo {

    private final JobDetail jobDetail;
    private final Trigger trigger;

    public JobInfo(JobDetail jobDetail, Trigger trigger) {
        this.jobDetail = jobDetail;
        this.trigger = trigger;
    }

    public JobDetail jobDetail() {
        return jobDetail;
    }

    public Trigger trigger() {
        return trigger;
    }

}
