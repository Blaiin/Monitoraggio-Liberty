package it.dmi.quartz.scheduler;

import it.dmi.data.api.service.ConfigurazioneService;
import it.dmi.data.entities.Soglia;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.structure.exceptions.MSDRuntimeException;
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

    private Scheduler msdScheduler;

    @Inject
    @Getter(AccessLevel.NONE)
    private ConfigurazioneService configurazioneService;

    @PostConstruct
    public void initialize() {
        try {
            var configsList = configurazioneService.getAll();
            int configCount = configsList.size();
            log.info("Detected {} possible CONFIGS to be scheduled.", configCount);
            int azioniCount = configsList.stream()
                    .flatMap(Configurazione::getSoglieAsStream)
                    .mapToInt(Soglia::getAzioniSize)
                    .sum();
            log.info("Detected {} possible AZIONI to be executed.", azioniCount);
            var props = PropsLoader.loadQuartzProperties();
            StdSchedulerFactory configFactory = new StdSchedulerFactory(props);
            msdScheduler = configFactory.getScheduler();
            msdScheduler.start();

            log.debug("Initialized Quartz schedulers.");
            log.info("Initialized Quartz schedulers.");
        } catch (SchedulerException | RuntimeException e) {
            log.error("Failed to start Quartz schedulers. {}", e.getMessage(), e.getCause());
            throw new MSDRuntimeException("Failed to start Quartz schedulers", e);
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            if (msdScheduler != null) {
                log.debug("Shutting down (Configurazione) scheduler..");
                msdScheduler.shutdown();
            }
        } catch (SchedulerException e) {
            log.error("Failed to shutdown Quartz schedulers. {}", e.getMessage(), e.getCause());
            throw new MSDRuntimeException("Failed to shutdown Quartz schedulers", e);
        }
    }

}
