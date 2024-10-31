package it.dmi.quartz.builders;

import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import it.dmi.structure.internal.info.JobInfo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.Objects;

import static it.dmi.utils.constants.NamingConstants.*;

@Slf4j
public class JobInfoBuilder extends MSDJobBuilder {

    public static JobInfo buildJobInfo(Scheduler scheduler, QuartzTask task) {
        var id = task.getStringID();
        try {
            var identity = resolveJobAndGroupName(id, task);
            JobDetail jobDetail = JobDetailBuilder.buildJobDetail(scheduler, task,
                    new JobKey(identity.jobName(), identity.groupName()));
            Trigger trigger = JobTriggerBuilder.buildTrigger(task,
                    new TriggerKey(identity.triggerName(), identity.triggerGroup()));
            Objects.requireNonNull(jobDetail, "JobDetail is required.");
            Objects.requireNonNull(trigger, "Trigger is required.");
            log.info("Job scheduled for Azione n. {}.", task.getStringID());
            return new JobInfo(jobDetail, trigger);
        } catch (Exception e) {
            return resolveJobBuildingException(e);
        }
    }

    private static JobIdentity resolveJobAndGroupName(String id, QuartzTask task) {
        return switch (task) {
            case Configurazione c -> new JobIdentity(CONFIG_JOB + id, CONFIG_GROUP,
                    CONFIG_TRIGGER + id, CONFIG_TRIGGER_GROUP);
            case Azione a -> new JobIdentity(AZIONE_JOB + id, AZIONE_GROUP,
                    AZIONE_TRIGGER + id, AZIONE_TRIGGER_GROUP);
        };
    }

    private record JobIdentity(String jobName, String groupName, String triggerName, String triggerGroup) {}

}
