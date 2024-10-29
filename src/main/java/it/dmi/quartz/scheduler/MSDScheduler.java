package it.dmi.quartz.scheduler;

import it.dmi.data.api.service.ConfigurazioneService;
import it.dmi.data.entities.Soglia;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.utils.file.PropsLoader;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

@Getter
@Singleton
@Startup
@Slf4j
public class MSDScheduler {


    private Scheduler configScheduler;

    private Scheduler azioneScheduler;

    @Inject
    @Getter(AccessLevel.NONE)
    private ConfigurazioneService configurazioneService;

    @PostConstruct
    public void initialize() {
        try {
            var configsList = configurazioneService.getAll();
            int configCount = configsList.size();
            log.info("Detected {} CONFIGS to be scheduled.", configCount);

            int azioniCount = configsList.stream()
                    .flatMap(Configurazione::getSoglieAsStream)
                    .mapToInt(Soglia::getAzioniSize)
                    .sum();
            log.info("Detected {} possible AZIONI to be executed.", azioniCount);

            var props = PropsLoader.loadQuartzProperties();
            StdSchedulerFactory configFactory = new StdSchedulerFactory(props.configProps());
            StdSchedulerFactory azioneFactory = new StdSchedulerFactory(props.configProps());

            configScheduler = configFactory.getScheduler();
            azioneScheduler = azioneFactory.getScheduler();

            configScheduler.start();
            azioneScheduler.start();

            log.debug("Initialized Quartz (Configurazione) scheduler");
            log.info("Initialized Quartz (Configurazione) scheduler");
        } catch (SchedulerException | RuntimeException e) {
            log.error("Failed to start Quartz scheduler", e);
            throw new RuntimeException("Failed to start Quartz scheduler", e);
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            if (configScheduler != null) {
                log.debug("Shutting down (Configurazione) scheduler...");
                configScheduler.shutdown();
            }
            if (azioneScheduler != null) {
                log.debug("Shutting down (Azione) scheduler...");
                azioneScheduler.shutdown();
            }
        } catch (SchedulerException e) {
            log.error("Failed to shutdown remote Quartz scheduler", e);
            throw new RuntimeException("Failed to shutdown remote Quartz scheduler", e);
        }
    }

}
