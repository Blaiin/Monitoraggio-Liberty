package it.dmi.quartz.builders;

import it.dmi.data.entities.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import it.dmi.structure.internal.JobInfo;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.quartz.*;

import java.util.Objects;

import static it.dmi.utils.constants.NamingConstants.GROUP;
import static it.dmi.utils.constants.NamingConstants.JOB;

@Slf4j
@RequestScoped
public class JobInfoBuilder {

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
            Trigger trigger = triggerBuilder.buildConfigTrigger(config, new TriggerKey(jobName, groupName));
            Objects.requireNonNull(jobDetail, "JobDetail is required.");
            Objects.requireNonNull(trigger, "Trigger is required.");
            log.info("Job scheduled for Configurazione n. {}.", config.getId());
            return new JobInfo(jobDetail, trigger);
        } catch (IllegalArgumentException e) {
            log.error("Failed to build job: ", e);
            return new JobInfo(null, null);
        } catch (SchedulerException e) {
            log.error("Failed to build job info, defaulting to no-op. Cause: ", e);
            return new JobInfo(null, null);
        } catch (NullPointerException e) {
            if (e.getMessage().contains("JobDetail is required.")) {
                log.error("Could not create job, skipping.");
            } else {
                log.error("Failed to build job info, defaulting to no-op. Cause: ", e);
            }
            return new JobInfo(null, null);
        } catch (JSQLParserException e) {
            log.error("Failed to build job: ", e);
        }
        return null;
    }

    public JobInfo buildJobInfo(Scheduler scheduler, QuartzTask task) {
        String id = String.valueOf(task.getStringID());
        String jobName = JOB + id;
        String groupName = GROUP + id;
        try {
            JobDetail jobDetail = detailBuilder.buildJobDetail(scheduler, task, new JobKey(jobName, groupName));

            //TODO fix cast to Configurazione, implement proper handling of QuartzTask
            Trigger trigger = triggerBuilder.buildConfigTrigger((Configurazione) task, new TriggerKey(jobName, groupName));

            Objects.requireNonNull(jobDetail, "JobDetail is required.");
            Objects.requireNonNull(trigger, "Trigger is required.");
            log.info("Job scheduled for Azione n. {}.", task.getStringID());
            return new JobInfo(jobDetail, trigger);
        } catch (IllegalArgumentException | SchedulerException | JSQLParserException e) {
            log.error("Failed to build job: ", e);
            return new JobInfo(null, null);
        } catch (NullPointerException e) {
            if (e.getMessage().contains("JobDetail is required.")) {
                log.error("Could not create job, skipping.");
            } else {
                log.error("Failed to build job info, defaulting to no-op. Cause: ", e);
            }
            return new JobInfo(null, null);
        }
    }

}
