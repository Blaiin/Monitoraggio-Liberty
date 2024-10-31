package it.dmi.quartz.builders;

import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import it.dmi.utils.NullChecks;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

@Slf4j
public class JobTriggerBuilder {

    private final static int defaultConfigDelay = 10;
    private final static int defaultAzioneDelay = 10;

    public static Trigger buildTrigger(QuartzTask task, TriggerKey key) {
        switch (task) {
            case Azione a -> {
                return buildTrigger(a, key);
            }
            case Configurazione c -> {
                return buildTrigger(c, key);
            }
        }
    }

    private static Trigger buildTrigger(Azione azione, TriggerKey triggerKey) {
        log.debug("Scheduling trigger for Azione {}", azione.getId());
        return TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .startAt(DateBuilder.futureDate(defaultAzioneDelay, DateBuilder.IntervalUnit.SECOND))
                .build();
    }

    private static Trigger buildTrigger(Configurazione config, TriggerKey triggerKey) {
        var nullOrEmpty = NullChecks.nullOrEmpty(config.getSchedulazione());
        if (!nullOrEmpty) {
            log.debug("Scheduling trigger for config . {}", config.getId());
            return TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder
                        .cronSchedule(config.getSchedulazione()))
                    .build();
        } else {
            log.debug("Scheduling trigger for config n. {} in {} seconds.", config.getId(), defaultConfigDelay);
            return TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .startAt(DateBuilder.futureDate(defaultConfigDelay, DateBuilder.IntervalUnit.SECOND))
                    .build();
        }
    }
}
