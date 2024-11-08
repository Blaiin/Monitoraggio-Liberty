package it.dmi.quartz.builders;

import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import it.dmi.utils.NullChecks;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

@Slf4j
public class JobTriggerBuilder {

    private static final int DEFAULT_CONFIG_DELAY = 10;
    private static final int DEFAULT_AZIONE_DELAY = 6;

    //Questo numero rappresenta le volte che il job verrà eseguito DOPO la PRIMA esecuzione
    private static final int SINGLE_EXECUTION = 0;

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
        var trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .startAt(DateBuilder.futureDate(DEFAULT_AZIONE_DELAY, DateBuilder.IntervalUnit.SECOND))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withRepeatCount(SINGLE_EXECUTION))
                .build();
        log.debug("Trigger for Azione {} created.", azione.getId());
        return trigger;
    }

    private static Trigger buildTrigger(Configurazione config, TriggerKey triggerKey) {
        var nullOrEmpty = NullChecks.nullOrEmpty(config.getSchedulazione());
        if (!nullOrEmpty) {
            try {
                var trigger = TriggerBuilder.newTrigger()
                        .withIdentity(triggerKey)
                        .withSchedule(CronScheduleBuilder
                            .cronSchedule(config.getSchedulazione()))
                        .build();
                log.debug("Trigger for Config {} created.", config.getId());
                return trigger;
            } catch (RuntimeException e) {
                if (e.getMessage().contains("CronExpression") && e.getMessage().contains("is invalid."))
                    log.error("Error while building trigger for Config {}", config.getStrID(), e);
                return null;
            }
        } else {
            var trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .startAt(DateBuilder.futureDate(DEFAULT_CONFIG_DELAY, DateBuilder.IntervalUnit.SECOND))
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                            .withRepeatCount(SINGLE_EXECUTION))
                    .build();
            log.debug("Trigger for Config {} ({}s delay) created.", config.getId(), DEFAULT_CONFIG_DELAY);
            return trigger;
        }
    }
}
