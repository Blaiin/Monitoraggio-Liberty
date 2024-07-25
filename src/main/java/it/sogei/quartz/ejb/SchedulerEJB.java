package it.sogei.quartz.ejb;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

@Getter
@Singleton
@Startup
@Slf4j
public class SchedulerEJB {

    private Scheduler scheduler;

    @PostConstruct
    public void init() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
            log.info("Scheduler started");
        } catch (SchedulerException e) {
            log.error("Failed to start remote Quartz scheduler", e);
            throw new RuntimeException("Failed to start remote Quartz scheduler", e);
        }
    }
    @PreDestroy
    public void destroy() {
        try {
            if (scheduler != null) {
                log.info("Shutting down scheduler...");
                scheduler.shutdown();
            }
        } catch (SchedulerException e) {
            log.error("Failed to shutdown remote Quartz scheduler", e);
            throw new RuntimeException("Failed to shutdown remote Quartz scheduler", e);
        }
    }

}
