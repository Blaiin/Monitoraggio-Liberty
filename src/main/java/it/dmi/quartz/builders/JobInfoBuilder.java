package it.dmi.quartz.builders;

import it.dmi.data.entities.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import it.dmi.structure.internal.info.JobInfo;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.Objects;

import static it.dmi.utils.constants.NamingConstants.GROUP;
import static it.dmi.utils.constants.NamingConstants.JOB;

@Slf4j
@RequestScoped
public class JobInfoBuilder extends MSDJobBuilder {

    @Inject
    private JobDetailBuilder detailBuilder;

    @Inject
    private JobTriggerBuilder triggerBuilder;

    public JobInfo buildJobInfo(Scheduler scheduler, Configurazione config) {
        String id = String.valueOf(config.getId());
        String jobName = JOB + id;
        String groupName = GROUP + id;
        try {
            JobDetail jobDetail = detailBuilder.buildJobDetail(scheduler, config, new JobKey(jobName, groupName));
            Trigger trigger = triggerBuilder.buildTrigger(config, new TriggerKey(jobName, groupName));
            Objects.requireNonNull(jobDetail, "JobDetail is required.");
            Objects.requireNonNull(trigger, "Trigger is required.");
            log.info("Job scheduled for Configurazione n. {}.", config.getId());
            return new JobInfo(jobDetail, trigger);
        } catch (Exception e) {
            return resolveJobBuildingException(e);
        }
    }

    public JobInfo buildJobInfo(Scheduler scheduler, QuartzTask task) {
        String id = String.valueOf(task.getStringID());
        String jobName = JOB + id;
        String groupName = GROUP + id;
        try {
            JobDetail jobDetail = detailBuilder.buildJobDetail(scheduler, task, new JobKey(jobName, groupName));

            //TODO fix cast to Configurazione, implement proper handling of QuartzTask
            Trigger trigger = triggerBuilder.buildTrigger(task, new TriggerKey(jobName, groupName));

            Objects.requireNonNull(jobDetail, "JobDetail is required.");
            Objects.requireNonNull(trigger, "Trigger is required.");
            log.info("Job scheduled for Azione n. {}.", task.getStringID());
            return new JobInfo(jobDetail, trigger);
        } catch (Exception e) {
            return resolveJobBuildingException(e);
        }
    }

}
