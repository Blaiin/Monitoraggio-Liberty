package it.sogei.utils.jobs;

import it.sogei.data_access.service.FonteDatiService;
import it.sogei.data_access.service.SicurezzaFonteDatiService;
import it.sogei.quartz.ejb.ManagerEJB;
import it.sogei.quartz.jobs.DynamicQueryJob;
import it.sogei.quartz.jobs.InternalQueryJob;
import it.sogei.quartz.jobs.RestQueryJob;
import it.sogei.structure.apimodels.QueryRequest;
import it.sogei.structure.data.Config;
import it.sogei.structure.data.entities.Configurazione;
import jakarta.ejb.DependsOn;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.Objects;

@Slf4j
@RequestScoped
@DependsOn({"FonteDatiService", "SicurezzaFonteDatiService"})
public class JobBuilder {

    @Inject
    private FonteDatiService fonteDatiService;

    @Inject
    private SicurezzaFonteDatiService sicurezzaFonteDatiService;

    private static final String url = "jdbc:postgresql://localhost:5432/";
    private static final String completeUrl = "jdbc:postgresql://localhost:5432/Monitoraggio";
    private static final String username = "postgres";
    private static final String password = "postgres";

    private static final boolean devMode = true;

    public ManagerEJB.JobInfo buildJobInfo(Scheduler scheduler, Configurazione config) {
        var id = String.valueOf(config.getId());
        var jobName = "JOB" + id;
        var groupName = "GROUP" + id;
        try {
            JobDetail jobDetail = buildJobDetail(scheduler, config, new JobKey(jobName, groupName));
            Trigger trigger = buildTrigger(config, new TriggerKey(jobName, groupName), 15);
            Objects.requireNonNull(jobDetail, "JobDetail is required.");
            Objects.requireNonNull(trigger, "Trigger is required.");
            log.info("Job scheduled with JobKey: {} and TriggerKey: {}", jobDetail.getKey(), trigger.getKey());
            return new ManagerEJB.JobInfo(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("Failed to build job info, defaulting to no-op", e);
            return new ManagerEJB.JobInfo(null, null);
        } catch (NullPointerException e) {
            if (e.getMessage().contains("JobDetail is required.")) {
                log.error("JobDetail is required, skipping creation.");
            } else {
                log.error("Failed to build job info, defaulting to no-op", e);
            }
            return new ManagerEJB.JobInfo(null, null);
        }
    }

    public JobDetail buildJobDetail(Scheduler scheduler, Configurazione config, JobKey jobKey)
            throws SchedulerException {
        if(scheduler.checkExists(jobKey)) {
            log.warn("Job with key {} already exists, skipping creation.", jobKey);
            return null;
        }
        JobDataMap map = buildJobDataMap(config);
        org.quartz.JobBuilder builder = org.quartz.JobBuilder.newJob(DynamicQueryJob.class)
                .withIdentity(jobKey)
                .usingJobData(map);
        return builder.build();
    }

    private Trigger buildTrigger(Configurazione config, TriggerKey triggerKey, int fromNow)
            throws SchedulerException {

        //Using Configurazione for scheduling time
        // TODO
        // Implement switching to cron expression from db when available and properly configured
        log.info("Scheduling trigger for config n. {} in {} seconds.", config.getId(), fromNow);
        return TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .startAt(DateBuilder.futureDate(fromNow, DateBuilder.IntervalUnit.SECOND))
                .build();

    }

    private JobDataMap buildJobDataMap(Configurazione config) {
        var id = String.valueOf(config.getId());
        var fonteDati = fonteDatiService.getByID(config.getFonteDatiID());
        var sicurezzaFonteDati = sicurezzaFonteDatiService.getByID(config.getUtenteFonteDatiID());
        JobDataMap map = new JobDataMap();
        if(devMode) {
            log.debug("Dev Mode is enabled, logging sensitive data.");
            log.debug("SQL Script: {}", config.getSqlScript());
            log.debug("Nome: {}", config.getNome());
            log.debug("Driver name: {}", fonteDati.getNomeDriver());
            log.debug("URL: {}", fonteDati.getUrl());
            log.debug("Username: {}", sicurezzaFonteDati.getUserID());
            log.debug("Password: {}", sicurezzaFonteDati.getPassword());
        }
        map.put("id", id);
        map.put("sqlScript" + id, config.getSqlScript());
        map.put("nome", config.getNome());
        map.put("driverName" + id, fonteDati.getNomeDriver());
        map.put("url" + id, fonteDati.getUrl());
        map.put("username" + id, sicurezzaFonteDati.getUserID());
        map.put("password" + id, sicurezzaFonteDati.getPassword());
        return map;
    }

    public ManagerEJB.JobInfo buildJobInfo(Scheduler scheduler, String id, QueryRequest request) {
        String jobName = RestQueryJob.class.getSimpleName() + id;
        String groupName = RestQueryJob.class.getSimpleName() + "Group" + id;
        try {
            JobDetail jobDetail = buildJobDetail(scheduler, request, new JobKey(jobName, groupName));
            Trigger trigger = buildTrigger(request, new TriggerKey(jobName, groupName));
            Objects.requireNonNull(jobDetail, "JobDetail is required.");
            Objects.requireNonNull(trigger, "Trigger is required.");
            log.info("Job scheduled with JobKey: {} and TriggerKey: {}", jobDetail.getKey(), trigger.getKey());
            return new ManagerEJB.JobInfo(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("Failed to build job info, defaulting to no-op", e);
            return new ManagerEJB.JobInfo(null, null);
        }
    }

    public ManagerEJB.JobInfo buildJobInfo(Scheduler scheduler, Config config) {
        String jobName = InternalQueryJob.class.getSimpleName() + config.getId();
        String groupName = InternalQueryJob.class.getSimpleName() + "Group" + config.getId();
        try {
            JobDetail jobDetail = buildJobDetail(scheduler, config, new JobKey(jobName, groupName));
            Trigger trigger = buildTrigger(config, new TriggerKey(jobName, groupName), 10);
            Objects.requireNonNull(jobDetail, "JobDetail is required.");
            Objects.requireNonNull(trigger, "Trigger is required.");
            log.info("Job scheduled with JobKey: {} and TriggerKey: {}", jobDetail.getKey(), trigger.getKey());
            return new ManagerEJB.JobInfo(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("Failed to build job info, defaulting to no-op", e);
            return new ManagerEJB.JobInfo(null, null);
        } catch (NullPointerException e) {
            if (e.getMessage().contains("JobDetail is required.")) {
                log.error("JobDetail is required, skipping creation.");
            } else {
                log.error("Failed to build job info, defaulting to no-op", e);
            }
            return new ManagerEJB.JobInfo(null, null);
        }
    }

    public JobDetail buildJobDetail(Scheduler scheduler, QueryRequest request, JobKey jobKey)
            throws SchedulerException {
        if(scheduler.checkExists(jobKey)) {
            log.warn("Job with key {} already exists, skipping creation.", jobKey);
            return null;
        }
        JobDataMap map = buildJobDataMap(request);

        org.quartz.JobBuilder builder = org.quartz.JobBuilder.newJob(RestQueryJob.class)
                .withIdentity(jobKey)
                .usingJobData(map);
        return builder.build();
    }

    public JobDetail buildJobDetail(Scheduler scheduler, Config config, JobKey jobKey)
            throws SchedulerException {
        if(scheduler.checkExists(jobKey)) {
            log.warn("Job with key {} already exists, skipping creation.", jobKey);
            return null;
        }
        JobDataMap map = buildJobDataMap(config);

        org.quartz.JobBuilder builder = org.quartz.JobBuilder.newJob(InternalQueryJob.class)
                .withIdentity(jobKey)
                .usingJobData(map);
        return builder.build();
    }

    private Trigger buildTrigger(QueryRequest request, TriggerKey triggerKey) {
        log.info("Scheduling trigger for request n. {} now.", request.hashCode());
        return TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .startNow()
                .build();
    }

    private Trigger buildTrigger(Config config, TriggerKey triggerKey, int fromNow)
            throws SchedulerException {
        //Use config for scheduling time
        log.info("Scheduling trigger for config n. {} in {} seconds.", config.getId(), fromNow);
        return TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .startAt(DateBuilder.futureDate(fromNow, DateBuilder.IntervalUnit.SECOND))
                .build();
    }

    private JobDataMap buildJobDataMap(QueryRequest request) {
        String id = String.valueOf(request.hashCode());
        JobDataMap map = new JobDataMap();
        if(devMode) {
            log.info("Dev Mode is enabled, logging sensitive data.");
            log.info("Query: {}", request.query());
        }
        map.put("id", id);
        map.put(id + "query", request.query());
        map.put("url", completeUrl);
        map.put("username", username);
        map.put("password", password);
        return map;
    }

    private JobDataMap buildJobDataMap(Config config) {
        String id = String.valueOf(config.getId());
        JobDataMap map = new JobDataMap();
        if(devMode) {
            log.info("Dev Mode is enabled, logging sensitive data.");
            log.info("Query: {}", config.getQuery());
            log.info("QueryType: {}", config.getQueryType());
            log.info("QueryDescription: {}", config.getQueryDescription());
            log.info("TargetTable: {}", config.getTargetTable());
            log.info("ExpectedResult: {}", config.getExpectedResult());
            log.info("Url: {}", url + config.getTargetDb());
            log.info("Username: {}", username);
            log.info("Password: {}", password);
        }
        map.put("id", id);
        map.put(id + "query", config.getQuery());
        map.put(id + "queryType", config.getQueryType());
        map.put(id + "queryDescription", config.getQueryDescription());
        map.put(id + "targetTable", config.getTargetTable());
        map.put(id + "expectedResult", config.getExpectedResult());
        map.put("url", url + config.getTargetDb());
        map.put("username", username);
        map.put("password", password);
        return map;
    }

}
