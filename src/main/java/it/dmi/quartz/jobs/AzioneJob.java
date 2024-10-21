package it.dmi.quartz.jobs;

import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Slf4j
public class AzioneJob implements IJob {

    @Override
    public void execute (JobExecutionContext jobExecutionContext) throws JobExecutionException {

    }

}
