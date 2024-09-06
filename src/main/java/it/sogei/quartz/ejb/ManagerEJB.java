package it.sogei.quartz.ejb;


import it.sogei.data_access.service.ConfigServiceOld;
import it.sogei.data_access.service.ConfigurazioneService;
import it.sogei.data_access.shared.JobDataCache;
import it.sogei.data_access.shared.RestDataCache;
import it.sogei.structure.apimodels.QueryRequest;
import it.sogei.structure.data.Config;
import it.sogei.structure.data.entities.Configurazione;
import it.sogei.utils.NullChecks;
import it.sogei.utils.jobs.JobBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.DependsOn;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import java.util.List;
import java.util.concurrent.TimeUnit;


@ApplicationScoped
@Slf4j
@DependsOn("SchedulerEJB")
public class ManagerEJB {

    @Inject
    private SchedulerEJB schedulerEJB;

    @Inject
    private ConfigServiceOld service;

    @Inject
    private ConfigurazioneService configService;

    @Inject
    private JobBuilder jobBuilder;

    private List<Config> oldConfigs;

    private List<Configurazione> configs;

    public void scheduleJobs() {
        try {
            for (Configurazione config : configs) {
                var id = String.valueOf(config.getId());
                JobDataCache.createLatch(id, 1);
                JobInfo jobInfo = jobBuilder.buildJobInfo(schedulerEJB.getScheduler(), config);
                if(NullChecks.requireNonNull(jobInfo)) {
                    schedulerEJB.getScheduler().scheduleJob(jobInfo.jobDetail(), jobInfo.trigger());
                    JobDataCache.awaitData(id,
                            jobInfo.trigger.getStartTime().getTime() - System.currentTimeMillis() + (15 * 1000),
                            TimeUnit.MILLISECONDS);
                }
                else log.error("No operation scheduled for config n. {}, job info is null.", id);
            }
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    public void scheduleOldJobs() {
        try {
            for(Config config : oldConfigs) {
                String id = String.valueOf(config.getId());
                JobDataCache.createLatch(id, 1);
                JobInfo jobInfo = jobBuilder.buildJobInfo(schedulerEJB.getScheduler(), config);
                if(NullChecks.requireNonNull(jobInfo)) {

                    schedulerEJB.getScheduler().scheduleJob(jobInfo.jobDetail(), jobInfo.trigger());
                    JobDataCache.awaitData(id,
                        jobInfo.trigger.getStartTime().getTime() - System.currentTimeMillis() + (15 * 1000),
                        TimeUnit.MILLISECONDS);
                }
                else log.error("No operation scheduled for config n. {}, job info is null.", config.getId());
                }
        } catch (NullPointerException e) {
            if (e.getMessage().contains("this.configs"))
                log.error("Could not retrieve configs. Skipping scheduling jobs.", e);
            else log.error("Failed to schedule jobs.", e);
        } catch (Exception e) {
            log.error("Failed to schedule jobs.", e);
        }
    }

    public void scheduleQueryJob(QueryRequest request) {
        try {
            String id = String.valueOf(request.hashCode());
            RestDataCache.createLatch(id, 1);
            JobInfo jobInfo = jobBuilder.buildJobInfo(schedulerEJB.getScheduler(), id, request);
            if (NullChecks.requireNonNull(jobInfo)) {
                schedulerEJB.getScheduler().scheduleJob(jobInfo.jobDetail(), jobInfo.trigger());
                RestDataCache.awaitData(id,
                        10, TimeUnit.SECONDS);
            } else log.error("No operation scheduled for request n. {}, job info is null.", request.hashCode());
        } catch (SchedulerException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    public void init() {
        try {
            //oldConfigs = service.getAllConfigs();
            configs = configService.getAll();
            log.info("ManagerEJB initialized.");
            schedulerEJB.getScheduler().start();
            log.info("Scheduler (ManagerEJB) started.");
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
        log.info("ManagerEJB queued to be initialized.");
    }
    public record JobInfo(JobDetail jobDetail, Trigger trigger) {
    }
}
