package it.dmi.quartz.ejb;


import it.dmi.caches.JobDataCache;
import it.dmi.data.api.service.ControlloService;
import it.dmi.data.entities.Configurazione;
import it.dmi.data.entities.Controllo;
import it.dmi.quartz.builders.JobInfoBuilder;
import it.dmi.structure.internal.JobInfo;
import it.dmi.utils.NullChecks;
import it.dmi.utils.jobs.QueryResolver;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.DependsOn;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
@Slf4j
@DependsOn("SchedulerEJB")
public class ManagerEJB {

    private static int maxMessages = 1;
    private static final boolean dev = true;

    @Inject
    private SchedulerEJB schedulerEJB;

    @Inject
    private ControlloService controlService;

    @Inject
    private JobInfoBuilder jobInfoBuilder;

    private List<Configurazione> configs;

    public void scheduleJobs() {
        try {
            if(configs.isEmpty()) {
                throw new NullPointerException("No configs found.");
            }
            for (Configurazione config : configs) {
                var id = config.getStringID();
                JobDataCache.createLatch(id, 1);
                JobInfo jobInfo = jobInfoBuilder.buildJobInfo(schedulerEJB.getScheduler(), config);
                if(NullChecks.requireNonNull(jobInfo)) {
                    schedulerEJB.getScheduler().scheduleJob(jobInfo.jobDetail(), jobInfo.trigger());
                    long waitTime = jobInfo.trigger().getStartTime().getTime() - System.currentTimeMillis() + (15 * 1000);
                    log.info("Timeout: {} s", waitTime / 1000);
                    JobDataCache.awaitData(id,
                            waitTime,
                            TimeUnit.MILLISECONDS);
                } else log.error("No operation scheduled for config n. {}, job info is null.", id);
            }
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    public void scheduleActions() {

    }

    @PostConstruct
    public void init() {
        log.debug("Post-construct phase initialized.");
        try {
            configs = new ArrayList<>();
            List<Controllo> controls = controlService.getAllOrdered();
            if(!controls.isEmpty()) {
                log.info("Controls fetched: {}", controls.size());
                log.debug("Controls fetched: {}", controls.size());
                for(Controllo controllo : controls) {
                    if (dev) {
                        configs.addAll(controllo.getConfigurazioniOrdered()
                                .stream()
                                .filter(QueryResolver::acceptSelectOrCount)
                                .toList());
                    } else {
                        configs.addAll(controllo.getConfigurazioniOrdered()
                                .stream()
                                .filter(QueryResolver::validateAndLog).toList());
                    }
                }
            }
            log.debug("ManagerEJB initialized.");
            schedulerEJB.getScheduler().start();
            log.debug("Scheduler (ManagerEJB) started.");
            log.info("Initialization complete.");
        } catch (NullPointerException e) {
            if(e.getMessage().contains("ConfigService")) {
                log.error("ConfigService is required.");
            } else if(e.getMessage().contains("SchedulerEJB")){
                log.error("SchedulerEJB is required.");
            } else {
                log.error("Failed to initialize ManagerEJB", e);
            }
        } catch (Exception e) {
            log.error("Failed to get configs for ManagerEJB", e);
        }
    }
    public ManagerEJB() {
        if (maxMessages == 1) {
            log.debug("ManagerEJB queued to be initialized.");
            maxMessages++;
        }
    }
}
