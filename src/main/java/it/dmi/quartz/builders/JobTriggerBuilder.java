package it.dmi.quartz.builders;

import it.dmi.data.entities.Azione;
import it.dmi.data.entities.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import jakarta.enterprise.context.RequestScoped;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

@Slf4j
@RequestScoped
public class JobTriggerBuilder {

    private final static int defaultConfigDelay = 10;
    private final static int defaultAzioneDelay = 10;

    private Trigger buildTrigger (QuartzTask task, TriggerKey key) throws SchedulerException {
        if (task instanceof Azione) {
            return TriggerBuilder.newTrigger()
                    .withIdentity(key)
                    .startNow()
                    .build();
        } else if (task instanceof Configurazione) {
            return buildConfigTrigger((Configurazione) task, key);
        } else {
            log.error("Task type not supported.");
            throw new IllegalArgumentException("Task type not supported.");
        }
    }

    public Trigger buildConfigTrigger (Configurazione config, TriggerKey triggerKey) {
        if(config.getSchedulazione() != null) {
            return TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder
                            .cronSchedule(config.getSchedulazione()))
                    .build();
        }
        log.info("Scheduling trigger for config n. {} in {} seconds.", config.getId(), defaultConfigDelay);
        return TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .startAt(DateBuilder.futureDate(defaultConfigDelay, DateBuilder.IntervalUnit.SECOND))
                .build();

    }
}
