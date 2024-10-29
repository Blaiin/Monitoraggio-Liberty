package it.dmi.quartz.ejb;


import it.dmi.caches.AzioneQueueCache;
import it.dmi.caches.JobDataCache;
import it.dmi.data.api.service.ControlloService;
import it.dmi.data.api.service.OutputService;
import it.dmi.data.entities.Controllo;
import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import it.dmi.processors.jobs.QueryResolver;
import it.dmi.quartz.builders.JobInfoBuilder;
import it.dmi.quartz.listeners.AzioneJobListener;
import it.dmi.quartz.listeners.ConfigurazioneJobListener;
import it.dmi.quartz.scheduler.MSDScheduler;
import it.dmi.structure.exceptions.impl.internal.DependencyInjectionException;
import it.dmi.structure.internal.info.JobInfo;
import it.dmi.utils.NullChecks;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.DependsOn;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
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
@DependsOn("MSDScheduler")
public class ManagerEJB {

    private static int maxMessages = 1;

    private static final boolean dev = true;
    private static final boolean countOnly = true;

    @Inject
    private MSDScheduler msdScheduler;

    @Inject
    private ControlloService controlService;

    @Inject
    private OutputService outputService;

    @Inject
    private JobInfoBuilder jobInfoBuilder;

    private List<Configurazione> configs;

    private final ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();

    public void scheduleConfigs() {
        if(configs.isEmpty()) {
            throw new NullPointerException("No configs found.");
        }
        log.info("Trying to start reading configs..");
        var configScheduler = msdScheduler.getConfigScheduler();
        configs.forEach(c -> {
            var id = c.getStringID();
            log.debug("Reading confing {}", id);
            JobDataCache.createLatch(id, 1);
            JobInfo jobInfo = jobInfoBuilder.buildJobInfo(configScheduler, c);
            if(!NullChecks.requireNonNull(jobInfo)) {
                log.error("Could not construct job info");
            } else {
                CompletableFuture.runAsync(() -> {
                try {
                    addJobListener(configScheduler, c, jobInfo);
                    configScheduler.scheduleJob(jobInfo.jobDetail(), jobInfo.trigger());
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

    //TODO check for usages of Config ID
    public void scheduleActions(/*String cID,*/ List<String> soglieIDs) {
        var azioneSched = msdScheduler.getAzioneScheduler();
        soglieIDs.forEach(sID ->
                CompletableFuture.runAsync(() ->
                    AzioneQueueCache.get(sID).forEach(a -> {
                        var jobInfo = jobInfoBuilder.buildJobInfo(azioneSched, a);
                        try {
                            azioneSched.getListenerManager().addJobListener(
                                    new AzioneJobListener(a, this),
                                    KeyMatcher.keyEquals(jobInfo.jobDetail().getKey())
                            );
                            azioneSched.scheduleJob(jobInfo.jobDetail(), jobInfo.trigger());
                        } catch (SchedulerException e) {
                            throw new RuntimeException(e);
                        }
                    })
                )
        );
    }

    public synchronized void onConfigJobCompletion(String cID, List<String> soglieIDs) {
        try {
            log.debug("Job completion verification {}", cID);
            var configOutput = JobDataCache.getOutput(OUTPUT + cID);
            outputService.create(configOutput.toEntity());
            log.info("Output from Config {} created.", cID);
            scheduleActions(/*cID,*/ soglieIDs);
        } catch (Exception e) {
            log.error("Silently failing: {}", e.getMessage(), e.getCause());
        }
    }

    //TODO implement logic for config job failure
    public synchronized void onConfigJobFail(String cID, Throwable e) {
        log.warn("Job {} encountered an error during execution. {}", cID, e.getMessage(), e.getCause());
    }

    //TODO finalize completion logic
    @SuppressWarnings("unused")
    public synchronized void onAzioneJobCompletion(String aID) {
    }

    //TODO finalize failure logic
    @SuppressWarnings("unused")
    public synchronized void onAzioneJobFail(String aID) {
    }

    private void addJobListener(Scheduler scheduler, QuartzTask task, JobInfo jobInfo) throws SchedulerException {
        switch (task) {
            case Azione a ->
                    scheduler.getListenerManager().addJobListener(
                    new AzioneJobListener(a, this),
                    KeyMatcher.keyEquals(jobInfo.jobDetail().getKey())
            );
            case Configurazione c ->
                    scheduler.getListenerManager().addJobListener(
                    new ConfigurazioneJobListener(c, this),
                    KeyMatcher.keyEquals(jobInfo.jobDetail().getKey())
            );
        }
    }

    @PostConstruct
    public void init() {
        log.debug("Post-construct phase initialized.");
        if(outputService == null)
            throw new DependencyInjectionException("Output Service was null");
        if(controlService == null)
            throw new DependencyInjectionException("Control Service was null");
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
                                .filter(QueryResolver::validateAndLog)
                                .toList());
                    }
                }
            }
            log.debug("ManagerEJB initialized.");
            log.info("Initialization complete.");
        } catch (DependencyInjectionException e) {
            log.error(e.getMessage(), e.getCause());
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
