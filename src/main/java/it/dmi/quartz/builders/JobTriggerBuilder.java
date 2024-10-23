package it.dmi.quartz.builders;

import it.dmi.data.entities.Azione;
import it.dmi.data.entities.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import it.dmi.utils.NullChecks;
import jakarta.enterprise.context.RequestScoped;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

@Slf4j
@RequestScoped
public class JobTriggerBuilder {

    private final static int defaultConfigDelay = 10;
    private final static int defaultAzioneDelay = 10;

    public Trigger buildTrigger(QuartzTask task, TriggerKey key) {
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

    private Trigger buildConfigTrigger(Configurazione config, TriggerKey triggerKey) {
        var nOrESched = NullChecks.nullOrEmpty(config.getSchedulazione());
        if (!nOrESched) log.debug("Scheduling trigger for config . {}", config.getId());
        else log.debug("Scheduling trigger for config n. {} in {} seconds.", config.getId(), defaultConfigDelay);
        return !nOrESched ? TriggerBuilder.newTrigger()
                                .withIdentity(triggerKey)
                                .withSchedule(CronScheduleBuilder
                                .cronSchedule(config.getSchedulazione()))
                                .build()
                            :   TriggerBuilder.newTrigger()
                                .withIdentity(triggerKey)
                                .startAt(DateBuilder.futureDate(defaultConfigDelay, DateBuilder.IntervalUnit.SECOND))
                                .build();
    }
}
