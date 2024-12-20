package it.dmi.quartz.ejb;

import it.dmi.caches.AzioneQueueCache;
import it.dmi.caches.JobDataCache;
import it.dmi.data.api.service.ControlloService;
import it.dmi.data.api.service.OutputService;
import it.dmi.data.entities.Controllo;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import it.dmi.processors.jobs.QueryResolver;
import it.dmi.quartz.builders.JobInfoBuilder;
import it.dmi.quartz.scheduler.MSDScheduler;
import it.dmi.quartz.threadPool.MSDThreadFactory;
import it.dmi.structure.exceptions.MSDRuntimeException;
import it.dmi.structure.exceptions.impl.internal.DependencyInjectionException;
import it.dmi.structure.exceptions.impl.internal.InvalidStateException;
import it.dmi.structure.internal.info.JobInfo;
import it.dmi.utils.Utils;
import it.dmi.utils.jobs.JobUtils;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateful;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Getter;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static it.dmi.utils.constants.NamingConstants.OUTPUT;

@Stateful
@ApplicationScoped
@Slf4j
public class Manager {

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

    @Getter
    private volatile Scheduler scheduler;

    private final ExecutorService defaultMSDService;

    public void scheduleConfigs() {
        if(configs.isEmpty())
            throw new InvalidStateException("No configs available to be scheduled.");

        log.info("Scheduling configs..");
        configs.forEach(c -> {
            final var id = c.getStrID();
            final var latchID = c.getLatchID();
            JobDataCache.createLatch(latchID, 1);
            final var jobInfo = JobInfoBuilder.buildJobInfo(c);
            if(!jobInfo.isValid() && jobInfo.alreadyDefined()) {
                log.error("Could not construct job info for Config {}", id);
                return;
            }
            log.debug("Processing Config {}", id);
            JobUtils.addJobListener(this, scheduler, c, jobInfo);
            CompletableFuture.runAsync(() -> scheduleJob(c, jobInfo), defaultMSDService);
        });
    }

    private void scheduleActions(@NotNull List<String> soglieIDs) {
        if (soglieIDs.isEmpty()) {
            log.error("List of Azioni to be scheduled is empty");
            return;
        }
        soglieIDs.forEach(sID ->
            AzioneQueueCache.getAzioni(sID).ifPresentOrElse(l -> l.forEach(a -> {
                final var aID = a.getStrID();
                final var latchID = a.getLatchID();
                log.debug("Reading azione {} from Soglia {}", aID, sID);
                JobDataCache.createLatch(latchID, 1);
                var jobInfo = JobInfoBuilder.buildJobInfo(a);
                if (!jobInfo.isValid()) {
                    log.error("Could not construct job info for Azione {}", aID);
                    return;
                }
                JobUtils.addJobListener(this, scheduler, a, jobInfo);
                CompletableFuture.runAsync(() -> scheduleJob(a, jobInfo), defaultMSDService);
            }), () -> log.debug("Could not find any Job (Azione) to be scheduled, " +
                    "probably no Soglia [id: {}] for Config were present", sID)));
    }

    private void scheduleJob(@NotNull QuartzTask task, @NotNull JobInfo jobInfo) {
        final var id = task.getStrID();
        final var latchID = task.getLatchID();
        try {
            if (JobUtils.checkIfJobExists(scheduler, jobInfo.jobDetail().getKey(), task))
                JobUtils.eraseJobData(this, jobInfo.jobDetail().getKey());
            scheduler.scheduleJob(jobInfo.jobDetail(), jobInfo.trigger());
            long waitTime = Utils.calculateWaitTime(task, jobInfo);
            boolean dataAvailable = JobDataCache
                    .awaitData(latchID, waitTime, TimeUnit.MILLISECONDS);
            log.debug("Task {} scheduled. Output data available: {}", id, dataAvailable);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error waiting for job to complete. {}", e.getMessage(), e);
        } catch (SchedulerException e) {
            log.error("Scheduler error for Task {}: {}", id, e.getMessage(), e);
            throw new MSDRuntimeException(e);
        }
    }

    @Synchronized
    public void onConfigJobCompletion(String cID, List<String> soglieIDs) {
        try {
            log.debug("CONFIG Job completion verification {}", cID);
            var configOutput = JobDataCache.getOutput(OUTPUT + cID);
            outputService.create(configOutput);
            log.info("Output from Config {} created.", cID);
            scheduleActions(soglieIDs);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage(), e);
        }
    }

    @Synchronized
    public void onConfigJobFail(String cID, Throwable e) {
        log.warn("Jobs {} encountered an error during execution. {}", cID, e.getMessage(), e);
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
        log.warn("Jobs {} encountered an error during execution. {}", aID, e.getMessage(), e);
    }

    //TODO enable full SELECT functionality
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
                log.debug("Controls fetched: {}", controls.size());
                for(Controllo controllo : controls) {
                    if (dev) {
                        if (countOnly) {
                            //Filter only select COUNT jobs
                            configs.addAll(controllo.getConfigurazioniOrdered()
                                    .stream()
                                    .filter(QueryResolver::acceptCount)
                                    .toList());
                            return;
                        }
                        //Filter only SELECT && select COUNT jobs
                        configs.addAll(controllo.getConfigurazioniOrdered()
                                .stream()
                                .filter(QueryResolver::acceptSelectOrCount)
                                .toList());
                        return;
                    }
                    //NO FILTER
                    configs.addAll(controllo.getConfigurazioniOrdered()
                            .stream()
                            .filter(QueryResolver::validateAndLog)
                            .toList());
                }
            }
            log.debug("Manager initialized.");
            log.info("Initialization complete.");
        } catch (DependencyInjectionException e) {
            log.error("There was an internal D. Injection error. {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Failed to get configs for Manager", e);
        }
    }

    @SuppressWarnings("unused")
    public Manager() {
        if (maxMessages == 1) {
            log.debug("Manager queued to be initialized.");
            maxMessages++;
        }
        this.defaultMSDService = Executors.newThreadPerTaskExecutor(new MSDThreadFactory(false));
    }
}
