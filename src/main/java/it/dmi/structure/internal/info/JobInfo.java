package it.dmi.structure.internal.info;

import org.quartz.JobDetail;
import org.quartz.Trigger;

public record JobInfo(JobDetail jobDetail, Trigger trigger, boolean alreadyDefined) implements Info {

    public JobInfo(JobDetail detail, Trigger trigger) {
        this(detail, trigger, false);
    }
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isValid() {
        return jobDetail != null && trigger != null;
    }
}
