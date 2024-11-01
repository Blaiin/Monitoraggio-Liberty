package it.dmi.quartz.ejb;


import it.dmi.caches.AzioneQueueCache;
import it.dmi.caches.JobDataCache;
import it.dmi.data.api.service.ControlloService;
import it.dmi.data.api.service.OutputService;
import it.dmi.data.entities.Controllo;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.processors.jobs.QueryResolver;
import it.dmi.quartz.builders.JobInfoBuilder;
import it.dmi.quartz.scheduler.MSDScheduler;
import it.dmi.structure.exceptions.MSDRuntimeException;
import it.dmi.structure.exceptions.impl.internal.DependencyInjectionException;
import it.dmi.structure.exceptions.impl.internal.InvalidStateException;
import it.dmi.structure.internal.info.JobInfo;
import it.dmi.utils.Utils;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.DependsOn;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

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

    //TODO enable full SELECT functionality
    public void scheduleConfigs() {
        if(configs.isEmpty())
            throw new InvalidStateException("No configs available to be scheduled.");

        log.info("Scheduling configs..");
        configs.forEach(c -> {
            final var id = c.getStringID();
            JobDataCache.createLatch(id + CONFIG, 1);
            final var jobInfo = JobInfoBuilder.buildJobInfo(scheduler, c);
            if(!jobInfo.isValid()) {
                log.error("Could not construct job info for Config {}", id);
                return;
            }
            log.debug("Processing Config {}", id);
            Utils.Jobs.addJobListener(this, scheduler, c, jobInfo);
            CompletableFuture.runAsync(() -> scheduleJob(id, c, jobInfo), service);
        });
    }

    //TODO check for usages of Config ID
    private void scheduleActions(/*String cID,*/ List<String> soglieIDs) {
        if (soglieIDs.isEmpty()) {
            log.error("List of Azioni to be scheduled is empty");
            return;
        }
        soglieIDs.forEach(sID ->
            AzioneQueueCache.get(sID).forEach(a -> {
                final var aID = a.getStringID();
                log.debug("Reading azione {} from Soglia {}", aID, sID);
                JobDataCache.createLatch(aID + AZIONE, 1);
                final var jobInfo = JobInfoBuilder.buildJobInfo(scheduler, a);
                if(!jobInfo.isValid()) {
                    log.error("Could not construct job info for Azione {}", aID);
                    return;
                }
                Utils.Jobs.addJobListener(this, scheduler, a, jobInfo);
                CompletableFuture.runAsync(() -> {
                    try {
                        log.info("Scheduling Azione {}", aID);
                        scheduler.scheduleJob(jobInfo.jobDetail(), jobInfo.trigger());
                        int maxWaitTime = 3600;
                        JobDataCache.awaitData(aID + AZIONE,
                                maxWaitTime,
                                TimeUnit.SECONDS);
                    } catch (Exception e) {
                        log.error("Encountered an error while scheduling Azione {}", aID);
                        throw new RuntimeException(e);
                    }
                }, service);
            }));
    }


    private void scheduleJob(String id, Configurazione c, JobInfo jobInfo) {
        try {
            scheduler.scheduleJob(jobInfo.jobDetail(), jobInfo.trigger());
            long waitTime = Utils.calculateWaitTime(c, jobInfo);
            boolean dataAvailable = JobDataCache
                    .awaitData(id + CONFIG, waitTime, TimeUnit.MILLISECONDS);
            log.debug("Config {} scheduled. Data available: {}", id, dataAvailable);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error waiting for job to complete. {}", e.getMessage(), e);
        } catch (SchedulerException e) {
            log.error("Scheduler error for config {}: {}", id, e.getMessage(), e);
            throw new MSDRuntimeException(e);
        }
    }

    @Synchronized
    public void onConfigJobCompletion(String cID, List<String> soglieIDs) {
        try {
            log.debug("CONFIG Jobs completion verification {}", cID);
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
        log.warn("Jobs {} encountered an error during execution. {}", cID, e.getMessage(), e);
    }

    @Synchronized
    public void onAzioneJobCompletion(String aID) {
        try {
            log.debug("AZIONE Jobs completion verification {}", aID);
            var azioneOutput = JobDataCache.getOutput(OUTPUT + aID);
            outputService.create(azioneOutput);
            log.info("Output from Azione {} created.", aID);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
        }
    }

    @Synchronized
    public void onAzioneJobFail(String aID, Throwable e) {
        log.warn("Jobs {} encountered an error during execution. {}", aID, e.getMessage(), e);
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
