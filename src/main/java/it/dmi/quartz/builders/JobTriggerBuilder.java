package it.dmi.quartz.builders;

import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quartz.*;

@Slf4j
public class JobTriggerBuilder {

    private static final int DEFAULT_CONFIG_DELAY = 10;
    private static final int DEFAULT_AZIONE_DELAY = 10;

    public static Trigger buildTrigger(@NotNull QuartzTask task, TriggerKey key) {
        switch (task) {
            case Azione a -> {
                return buildTrigger(a, key);
            }
            case Configurazione c -> {
                return buildTrigger(c, key);
            }
        }
    }

    private static Trigger buildTrigger(@NotNull Azione azione, TriggerKey triggerKey) {
        var trigger = TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .startAt(DateBuilder.futureDate(DEFAULT_AZIONE_DELAY, DateBuilder.IntervalUnit.SECOND))
                .build();
        log.debug("Trigger for Azione {} created.", azione.getId());
        return trigger;
    }

    private static @Nullable Trigger buildTrigger(@NotNull Configurazione config, TriggerKey triggerKey) {
        var sched = config.getSchedulazione();
        if (sched == null || sched.isEmpty()) {
            var trigger = TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .startAt(DateBuilder.futureDate(DEFAULT_CONFIG_DELAY, DateBuilder.IntervalUnit.SECOND))
                    .build();
            log.debug("Trigger for Config {} ({}s delay) created.", config.getId(), DEFAULT_CONFIG_DELAY);
            return trigger;
        } else {
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
        }
    }
}
