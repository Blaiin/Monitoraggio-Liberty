package it.dmi.quartz.ejb;


import it.dmi.data_access.service.ControlloService;
import it.dmi.data_access.shared.JobDataCache;
import it.dmi.structure.data.entities.Configurazione;
import it.dmi.structure.data.entities.Controllo;
import it.dmi.structure.internal.JobInfo;
import it.dmi.utils.NullChecks;
import it.dmi.utils.jobs.JobBuilder;
import it.dmi.utils.jobs.QueryResolver;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.DependsOn;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;

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
    private JobBuilder jobBuilder;

    private List<Configurazione> configs;

    private List<Controllo> controls;

    public void scheduleJobs() {
        try {
            if(controls.isEmpty()) {
                throw new NullPointerException("No controls found.");
            }
            if(configs.isEmpty()) {
                throw new NullPointerException("No configs found.");
            }
            for (Configurazione config : configs) {
                String id = String.valueOf(config.getId());
                JobDataCache.createLatch(id, 1);
                JobInfo jobInfo = jobBuilder.buildJobInfo(schedulerEJB.getScheduler(), config);
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
            controls = controlService.getAll();
            int cSize = controls.size();
            if(!controls.isEmpty()) {
                log.info("Controls fetched: {}", cSize);
                log.debug("Controls fetched: {}", cSize);
                for(Controllo controllo : controls) {
                    if (dev) {
                        configs.addAll(controllo.getConfigurazioni()
                                .stream()
                                .filter(c -> {
                                    try {
                                        return QueryResolver.DEV_filterSELECT_OR_COUNT(c.getSqlScript());
                                    } catch (JSQLParserException e) {
                                        throw new RuntimeException(e);
                                    }
                                }).toList());
                    } else {
                        configs.addAll(controllo.getConfigurazioni()
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
