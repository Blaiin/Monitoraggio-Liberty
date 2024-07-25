package it.sogei.quartz.ejb;


import it.sogei.data_access.service.ConfigService;
import it.sogei.data_access.shared.SharedDataCache;
import it.sogei.structure.apimodels.QueryRequest;
import it.sogei.structure.data.Config;
import it.sogei.utils.NullChecks;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.DependsOn;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static it.sogei.utils.jobs.JobBuilder.buildJobInfo;


@ApplicationScoped
@Slf4j
@DependsOn({"SchedulerEJB", "ConfigService"})
public class ManagerEJB {


    @Inject
    private SchedulerEJB schedulerEJB;

    @Inject
    private ConfigService service;

    private List<Config> configs;

    public void scheduleQueryJob(QueryRequest request) {
        try {
            schedulerEJB.getScheduler().start();
            log.info("Scheduler started");
            String id = String.valueOf(request.hashCode());
            SharedDataCache.createLatch(id, 1);
            JobInfo jobInfo = buildJobInfo(schedulerEJB.getScheduler(), id, request);
            if (NullChecks.requireNonNull(jobInfo)) {
                schedulerEJB.getScheduler().scheduleJob(jobInfo.jobDetail(), jobInfo.trigger());
                SharedDataCache.awaitData(id,
                    jobInfo.trigger.getStartTime().getTime() - System.currentTimeMillis() + 15000,
                    TimeUnit.MILLISECONDS);
            } else log.error("No operation scheduled for request n. {}, job info is null.", request.hashCode());
        } catch (SchedulerException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public void scheduleJobs() {
        try {
            schedulerEJB.getScheduler().start();
            log.info("Scheduler started");
            for(Config config : configs) {

                String id = String.valueOf(config.getId());
                SharedDataCache.createLatch(id, 1);
                JobInfo jobInfo = buildJobInfo(schedulerEJB.getScheduler(), config);

                if(NullChecks.requireNonNull(jobInfo)) {

                    schedulerEJB.getScheduler().scheduleJob(jobInfo.jobDetail(), jobInfo.trigger());
                    SharedDataCache.awaitData(id,
                        jobInfo.trigger.getStartTime().getTime() - System.currentTimeMillis() + 15000,
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


    @PostConstruct
    public void init() {
        try {
            configs = service.getAllConfigs();
            log.info("ManagerEJB initialized.");
        } catch (NullPointerException e) {
            if(e.getMessage().contains("ConfigService")) {
                log.error("ConfigService is required.");
            } else if(e.getMessage().contains("SchedulerEJB")){
                log.error("SchedulerEJB is required.");
            } else {
                log.error("Failed to initialize ManagerEJB", e);
            }
        } catch (Exception e) {
            log.error("Failed to get configs", e);
        }
    }
    public ManagerEJB() {
        log.info("ManagerEJB initialized.");
    }
    public record JobInfo(JobDetail jobDetail, Trigger trigger) {
    }
}
