package it.dmi.quartz.ejb;

import it.dmi.data_access.service.ConfigurazioneService;
import it.dmi.utils.file.ConfigLoader;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import java.util.Properties;

import static it.dmi.utils.constants.FileConstants.QUARTZ_THREAD_COUNT;

@Getter
@Singleton
@Startup
@Slf4j
public class SchedulerEJB {

    private Scheduler scheduler;

    @Inject
    private ConfigurazioneService configurazioneService;

    @PostConstruct
    public void init() {
        try {
            int configCount = configurazioneService.count();
            log.info("Detected {} jobs to be scheduled", configCount);
            int threadCount = Math.max(10, (int) Math.ceil(configCount * 1.5));
            log.info("Setting Quartz thread pool size to {}", threadCount);

            Properties properties = ConfigLoader.loadQuartzConfig();
            properties.setProperty(QUARTZ_THREAD_COUNT, String.valueOf(threadCount));
            StdSchedulerFactory factory = new StdSchedulerFactory();
            factory.initialize(properties);
            scheduler = factory.getScheduler();
            scheduler.start();

            log.info("Quartz scheduler started with {} threads", threadCount);
            log.debug("Initialized Quartz scheduler");
            log.info("Initialized Quartz scheduler");
        } catch (SchedulerException | RuntimeException e) {
            log.error("Failed to start Quartz scheduler", e);
            throw new RuntimeException("Failed to start Quartz scheduler", e);
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            if (scheduler != null) {
                log.debug("Shutting down scheduler...");
                scheduler.shutdown();
            }
        } catch (SchedulerException e) {
            log.error("Failed to shutdown remote Quartz scheduler", e);
            throw new RuntimeException("Failed to shutdown remote Quartz scheduler", e);
        }
    }

}
