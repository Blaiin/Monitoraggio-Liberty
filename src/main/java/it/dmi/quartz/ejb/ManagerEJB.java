package it.dmi.quartz.ejb;


import it.dmi.caches.JobDataCache;
import it.dmi.data.api.service.ControlloService;
import it.dmi.data.api.service.OutputService;
import it.dmi.data.entities.Controllo;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.processors.jobs.QueryResolver;
import it.dmi.quartz.builders.JobInfoBuilder;
import it.dmi.quartz.listeners.ConfigurazioneJobListener;
import it.dmi.structure.internal.info.JobInfo;
import it.dmi.utils.NullChecks;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.DependsOn;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.KeyMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static it.dmi.utils.constants.NamingConstants.OUTPUT;

@ApplicationScoped
@Slf4j
@DependsOn("ConfigSchedulerEJB")
public class ManagerEJB {

    private static int maxMessages = 1;

    private static final boolean dev = true;
    private static final boolean countOnly = true;

    @Inject
    private ConfigSchedulerEJB configScheduler;

    @Inject
    private ControlloService controlService;

    @Inject
    private OutputService outputService;

    @Inject
    private JobInfoBuilder jobInfoBuilder;

    private List<Configurazione> configs;

    private final ExecutorService service = Executors.newCachedThreadPool();

    public void scheduleJobsAsync() {
        if(configs.isEmpty()) {
            throw new NullPointerException("No configs found.");
        }
        log.info("Trying to start reading configs..");
        configs.forEach(c -> {
            var id = c.getStringID();
            log.debug("Reading confing {}", id);
            JobDataCache.createLatch(id, 1);
            JobInfo jobInfo = jobInfoBuilder.buildJobInfo(configScheduler.getScheduler(), c);
            if(!NullChecks.requireNonNull(jobInfo)) {
                log.error("Could not construct job info");
            } else {
                CompletableFuture.runAsync(() -> {
                try {
                    configScheduler.getScheduler().getListenerManager().addJobListener(
                            new ConfigurazioneJobListener(c, this),
                            KeyMatcher.keyEquals(jobInfo.jobDetail().getKey())
                    );
                    configScheduler.getScheduler().scheduleJob(jobInfo.jobDetail(), jobInfo.trigger());
                    if (c.getSchedulazione() == null) {
                        long waitTime = jobInfo.trigger().getStartTime().getTime()
                                - System.currentTimeMillis() + (15 * 1000);
                        log.debug("ASYNC - Timeout: {} s", waitTime / 1000);
                        if (JobDataCache.awaitData(id,
                                waitTime,
                                TimeUnit.MILLISECONDS)) {
                            log.debug("ASYNC - Undertaking Configurazione n. {}", id);
                        }
                    } else {
                        int maxWaitTime = 3600;
                        JobDataCache.awaitData(id,
                                maxWaitTime,
                                TimeUnit.SECONDS);
                    }
                } catch (InterruptedException e) {
                    log.error("ASYNC - Error while waiting for job to finish. {}", e.getMessage());
                } catch (SchedulerException e) {
                    throw new RuntimeException(e);
                }}, service);
            }
        });
    }

    public void scheduleJobs() {
        if(configs.isEmpty()) {
            throw new NullPointerException("No configs found.");
        }
        for (Configurazione config : configs) {
            var id = config.getStringID();
            JobDataCache.createLatch(id, 1);
            JobInfo jobInfo = jobInfoBuilder.buildJobInfo(configScheduler.getScheduler(), config);
            try {
                if (NullChecks.requireNonNull(jobInfo)) {
                    configScheduler.getScheduler().scheduleJob(jobInfo.jobDetail(), jobInfo.trigger());
                    long waitTime = jobInfo.trigger().getStartTime().getTime() - System.currentTimeMillis() + (15 * 1000);
                    log.info("Timeout: {} s", waitTime / 1000);
                    if (config.getSchedulazione() == null) {
                        if (JobDataCache.awaitData(id,
                                waitTime,
                                TimeUnit.MILLISECONDS)) {
                            log.debug("Undertaking Configurazione n. {}", id);
                        }
                    } else {
                        int maxWaitTime = 3600;
                        JobDataCache.awaitData(id,
                                maxWaitTime,
                                TimeUnit.SECONDS);
                    }
                }
            } catch (InterruptedException e) {
                log.error("Error while waiting for job to finish. {}", e.getMessage());
            } catch (SchedulerException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void scheduleActions() {

    }

    public synchronized void onConfigJobCompletion (String cID) {
        try {
            log.info("Job completion verification {}", cID);
            var configOutput = JobDataCache.getOutput(OUTPUT + cID);
            outputService.create(configOutput.toEntity());
            log.info("Output from Config {} created.", cID);
        } catch (Exception e) {
            log.error("Silently failing: {}", e.getMessage(), e.getCause());
        }
    }

    //TODO implement logic for config job failure
    public void onConfigJobFail (String cID) {
        log.warn("Job from Config {} failed.", cID);
    }

    @PostConstruct
    public void init() {
        log.debug("Post-construct phase initialized.");
        if(outputService == null) log.error("Output Service was null");
        try {
            configs = new ArrayList<>();
            List<Controllo> controls = controlService.getAllOrdered();
            if(!controls.isEmpty()) {
                log.info("Controls fetched: {}", controls.size());
                log.debug("Controls fetched: {}", controls.size());
                for(Controllo controllo : controls) {
                    if (dev) {
                        if (!countOnly) {
                            configs.addAll(controllo.getConfigurazioniOrdered()
                                    .stream()
                                    .filter(QueryResolver::acceptSelectOrCount)
                                    .toList());
                        } else {
                            configs.addAll(controllo.getConfigurazioniOrdered()
                                    .stream()
                                    .filter(QueryResolver::acceptCount)
                                    .toList());
                        }
                    } else {
                        configs.addAll(controllo.getConfigurazioniOrdered()
                                .stream()
                                .filter(QueryResolver::validateAndLog).toList());
                    }
                }
            }
            log.debug("ManagerEJB initialized.");
            configScheduler.getScheduler().start();
            log.debug("Scheduler (ManagerEJB) started.");
            log.info("Initialization complete.");
        } catch (NullPointerException e) {
            if(e.getMessage().contains("ConfigService")) {
                log.error("ConfigService is required.");
            } else if(e.getMessage().contains("ConfigSchedulerEJB")){
                log.error("ConfigSchedulerEJB is required.");
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
