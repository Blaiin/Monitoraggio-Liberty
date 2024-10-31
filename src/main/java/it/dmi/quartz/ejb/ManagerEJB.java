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
import it.dmi.structure.exceptions.MSDRuntimeException;
import it.dmi.structure.exceptions.impl.internal.DependencyInjectionException;
import it.dmi.structure.internal.info.JobInfo;
import it.dmi.utils.NullChecks;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.DependsOn;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Synchronized;
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

import static it.dmi.utils.constants.NamingConstants.*;

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

    private List<Configurazione> configs;

    private final ExecutorService service = Executors.newVirtualThreadPerTaskExecutor();

    private Scheduler scheduler;

    public void scheduleConfigs() {
        if(configs.isEmpty()) {
            throw new NullPointerException("No configs found.");
        }
        log.info("Trying to start reading configs..");
        configs.forEach(c -> {
            var id = c.getStringID();
            log.debug("Reading confing {}", id);
            JobDataCache.createLatch(id + CONFIG, 1);
            JobInfo jobInfo = JobInfoBuilder.buildJobInfo(scheduler, c);
            if(NullChecks.requireNonNull(jobInfo)) {
                log.error("Could not construct job info");
            } else {
                addJobListener(scheduler, c, jobInfo);
                CompletableFuture.runAsync(() -> {
                try {
                    scheduler.scheduleJob(jobInfo.jobDetail(), jobInfo.trigger());
                    if (c.getSchedulazione() == null) {
                        long waitTime = jobInfo.trigger().getStartTime().getTime()
                                - System.currentTimeMillis() + (15 * 1000);
                        log.debug("ASYNC - Timeout: {} s", waitTime / 1000);
                        if (JobDataCache.awaitData(id + CONFIG,
                                waitTime,
                                TimeUnit.MILLISECONDS)) {
                            log.debug("ASYNC - Undertaking Configurazione n. {}", id);
                        }
                    } else {
                        int maxWaitTime = 3600;
                        JobDataCache.awaitData(id + CONFIG,
                                maxWaitTime,
                                TimeUnit.SECONDS);
                    }
                } catch (InterruptedException e) {
                    log.error("ASYNC - Error while waiting for job to finish. {}", e.getMessage(), e);
                } catch (SchedulerException e) {
                    log.error("Error: {}", e.getMessage(), e);
                    throw new MSDRuntimeException(e);
                }}, service);
            }
        });
    }

    //TODO check for usages of Config ID
    public void scheduleActions(/*String cID,*/ List<String> soglieIDs) {
        if(soglieIDs.isEmpty()) log.error("List of Azioni to be scheduled was null");
        soglieIDs.forEach(sID ->
            AzioneQueueCache.get(sID).forEach(a -> {
                log.debug("Reading azione {} from Soglia {}", a.getStringID(), sID);
                JobDataCache.createLatch(a.getStringID() + AZIONE, 1);
                var jobInfo = JobInfoBuilder.buildJobInfo(scheduler, a);
                if(NullChecks.requireNonNull(jobInfo)) {
                    log.error("Could not construct job info for Azione {}", a.getStringID());
                } else {
                    addJobListener(scheduler, a, jobInfo);
                    CompletableFuture.runAsync(() -> {
                        try {
                            log.info("Scheduling Azione {}", a.getStringID());
                            scheduler.scheduleJob(jobInfo.jobDetail(), jobInfo.trigger());
                            int maxWaitTime = 3600;
                            JobDataCache.awaitData(a.getStringID() + AZIONE,
                                    maxWaitTime,
                                    TimeUnit.SECONDS);
                        } catch (Exception e) {
                            log.error("Encountered an error while scheduling Azione {}", a.getStringID());
                            throw new RuntimeException(e);
                        }
                    }, service);
                }
        }));
    }

    @Synchronized
    public void onConfigJobCompletion(String cID, List<String> soglieIDs) {
        try {
            log.debug("CONFIG Job completion verification {}", cID);
            var configOutput = JobDataCache.getOutput(OUTPUT + cID);
            outputService.create(configOutput);
            log.info("Output from Config {} created.", cID);
            scheduleActions(/*cID,*/ soglieIDs);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
        }
    }

    //TODO implement logic for config job failure
    @Synchronized
    public void onConfigJobFail(String cID, Throwable e) {
        log.warn("Job {} encountered an error during execution. {}", cID, e.getMessage(), e);
    }

    @Synchronized
    public void onAzioneJobCompletion(String aID) {
        try {
            log.debug("AZIONE Job completion verification {}", aID);
            var azioneOutput = JobDataCache.getOutput(OUTPUT + aID);
            outputService.create(azioneOutput);
            log.info("Output from Azione {} created.", aID);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
        }
    }

    @Synchronized
    public void onAzioneJobFail(String aID, Throwable e) {
        log.warn("Job {} encountered an error during execution. {}", aID, e.getMessage(), e);
    }

    private void addJobListener(Scheduler scheduler, QuartzTask task, JobInfo jobInfo) {
        try {
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
        } catch (SchedulerException e) {
            log.error("Error adding Job Listener for Task {}", task.getStringID());
            throw new MSDRuntimeException(e);
        }
    }

    @PostConstruct
    public void init() {
        log.debug("Post-construct phase initialized.");
        if(outputService == null)
            throw new DependencyInjectionException("Output Service was null");
        if(controlService == null)
            throw new DependencyInjectionException("Control Service was null");
        if(msdScheduler == null)
            throw new DependencyInjectionException("MSD Scheduler was null");
        try {
            scheduler = msdScheduler.getMsdScheduler();
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
            log.error("There was an internal D. Injection error. {}", e.getMessage(), e);
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
