package it.dmi.quartz.listeners;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;

@Slf4j
public class AzioneJobListener implements JobListener {

    @Override
    public String getName () {
        return "";
    }

    @Override
    public void jobToBeExecuted (JobExecutionContext jobExecutionContext) {

    }

    @Override
    public void jobExecutionVetoed (JobExecutionContext jobExecutionContext) {

    }

    @Override
    public void jobWasExecuted (JobExecutionContext jobExecutionContext, JobExecutionException e) {

    }
}
