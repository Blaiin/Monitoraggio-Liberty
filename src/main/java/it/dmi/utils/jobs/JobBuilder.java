package it.dmi.utils.jobs;

import it.dmi.data_access.service.FonteDatiService;
import it.dmi.data_access.service.SicurezzaFonteDatiService;
import it.dmi.quartz.jobs.ClassJob;
import it.dmi.quartz.jobs.ProgramJob;
import it.dmi.quartz.jobs.SelectCountJob;
import it.dmi.quartz.jobs.SelectJob;
import it.dmi.structure.data.entities.Azione;
import it.dmi.structure.data.entities.Configurazione;
import it.dmi.structure.data.entities.FonteDati;
import it.dmi.structure.data.entities.SicurezzaFonteDati;
import it.dmi.structure.data.entities.task.QuartzTask;
import it.dmi.structure.internal.JobInfo;
import it.dmi.structure.internal.JobType;
import it.dmi.structure.internal.QueryType;
import jakarta.ejb.DependsOn;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.quartz.*;

import java.text.ParseException;
import java.util.Objects;

import static it.dmi.utils.constants.NamingConstants.*;
import static it.dmi.utils.jobs.QueryResolver.resolveQuery;

@Slf4j
@RequestScoped
@DependsOn({"FonteDatiService", "SicurezzaFonteDatiService"})
public class JobBuilder {

    @Inject
    private FonteDatiService fonteDatiService;

    @Inject
    private SicurezzaFonteDatiService sicurezzaFonteDatiService;


    private static final boolean devMode = false;

    private final static int defaultConfigDelay = 10;
    private final static int defaultAzioneDelay = 10;

    public JobInfo buildJobInfo(Scheduler scheduler, QuartzTask task) {
        String id = String.valueOf(task.getStringID());
        String jobName = JOB + id;
        String groupName = GROUP + id;
        try {
            JobDetail jobDetail = buildJobDetail(scheduler, task, new JobKey(jobName, groupName));

            //TODO fix cast to Configurazione, implement proper handling of QuartzTask
            Trigger trigger = buildConfigTrigger((Configurazione) task, new TriggerKey(jobName, groupName));

            Objects.requireNonNull(jobDetail, "JobDetail is required.");
            Objects.requireNonNull(trigger, "Trigger is required.");
            log.info("Job scheduled for Azione n. {}.", task.getStringID());
            return new JobInfo(jobDetail, trigger);
        } catch (IllegalArgumentException e) {
            log.error("Failed to build job: ", e);
            return new JobInfo(null, null);
        } catch (SchedulerException e) {
            log.error("Failed to build job info, defaulting to no-op. Cause: ", e);
            return new JobInfo(null, null);
        } catch (NullPointerException e) {
            if (e.getMessage().contains("JobDetail is required.")) {
                log.error("Could not create job, skipping.");
            } else {
                log.error("Failed to build job info, defaulting to no-op. Cause: ", e);
            }
            return new JobInfo(null, null);
        }
    }

    private JobDetail buildJobDetail (Scheduler scheduler, QuartzTask task, JobKey jobKey) throws SchedulerException {
        return null;
    }


    public JobInfo buildJobInfo(Scheduler scheduler, Configurazione config) {
        String id = String.valueOf(config.getId());
        String jobName = JOB + id;
        String groupName = GROUP + id;
        try {
            JobDetail jobDetail = buildJobDetail(scheduler, config, new JobKey(jobName, groupName));
            Trigger trigger = buildConfigTrigger(config, new TriggerKey(jobName, groupName));
            Objects.requireNonNull(jobDetail, "JobDetail is required.");
            Objects.requireNonNull(trigger, "Trigger is required.");
            log.info("Job scheduled for Configurazione n. {}.", config.getId());
            return new JobInfo(jobDetail, trigger);
        } catch (IllegalArgumentException e) {
            log.error("Failed to build job: ", e);
            return new JobInfo(null, null);
        } catch (SchedulerException e) {
            log.error("Failed to build job info, defaulting to no-op. Cause: ", e);
            return new JobInfo(null, null);
        } catch (NullPointerException e) {
            if (e.getMessage().contains("JobDetail is required.")) {
                log.error("Could not create job, skipping.");
            } else {
                log.error("Failed to build job info, defaulting to no-op. Cause: ", e);
            }
            return new JobInfo(null, null);
        } catch (ParseException | JSQLParserException e) {
            log.error("Failed to build job: ", e);
        }
        return null;
    }

    public JobDetail buildJobDetail(Scheduler scheduler, Configurazione config, JobKey jobKey)
            throws SchedulerException, ParseException, JSQLParserException {
        checkIfJobExists(scheduler, jobKey, config);

        JobDataMap jobDataMap = buildJobDataMap(config);
        JobType jobType = getJobType(jobDataMap, config);

        return createJobBuilder(jobType, jobKey, jobDataMap, config).build();
    }

    private void checkIfJobExists(Scheduler scheduler, JobKey jobKey, Configurazione config) throws SchedulerException {
        if (scheduler.checkExists(jobKey)) {
            log.warn("Job with key {}, Configurazione {} already exists, skipping creation.", jobKey, config.getId());
            throw new SchedulerException("Job already exists.");
        }
    }

    private JobType getJobType(JobDataMap jobDataMap, Configurazione config) {
        return JobType.valueOf(jobDataMap.getString(JOB_TYPE + config.getId()));
    }

    private org.quartz.JobBuilder createJobBuilder(JobType jobType, JobKey jobKey, JobDataMap jobDataMap, Configurazione config) throws JSQLParserException {
        return switch (jobType) {
            case SQL -> createSqlJobBuilder(config, jobKey, jobDataMap);
            case PROGRAM -> org.quartz.JobBuilder.newJob(ProgramJob.class)
                    .withIdentity(jobKey)
                    .usingJobData(jobDataMap);
            case CLASS -> org.quartz.JobBuilder.newJob(ClassJob.class)
                    .withIdentity(jobKey)
                    .usingJobData(jobDataMap);
            case AZIONE -> throw new IllegalArgumentException("JobType not supported.");
        };
    }

    private org.quartz.JobBuilder createSqlJobBuilder(Configurazione config, JobKey jobKey, JobDataMap jobDataMap) throws JSQLParserException {
        QueryType queryType = resolveQuery(config.getSqlScript());
        return switch (queryType) {
            case SELECT -> org.quartz.JobBuilder.newJob(SelectJob.class)
                    .withIdentity(jobKey)
                    .usingJobData(jobDataMap);
            case SELECT_COUNT -> org.quartz.JobBuilder.newJob(SelectCountJob.class)
                    .withIdentity(jobKey)
                    .usingJobData(jobDataMap);
            case INSERT, UPDATE, DELETE ->
                    throw new IllegalArgumentException(String.format("QueryType (%s) not supported.", queryType.getQueryType()));
            default -> throw new IllegalArgumentException("Unexpected QueryType: " + queryType);
        };
    }

    private Trigger buildTrigger (QuartzTask task, TriggerKey key) throws SchedulerException {
        if (task instanceof Azione) {
            return TriggerBuilder.newTrigger()
                    .withIdentity(key)
                    .startNow()
                    .build();
        } else if (task instanceof Configurazione) {
            return buildConfigTrigger((Configurazione) task, key);
        } else {
            log.error("Task type not supported.");
            throw new IllegalArgumentException("Task type not supported.");
        }
    }
    private Trigger buildConfigTrigger (Configurazione config, TriggerKey triggerKey)
            throws SchedulerException {
        if(config.getSchedulazione() != null) {
            return TriggerBuilder.newTrigger()
                    .withIdentity(triggerKey)
                    .withSchedule(CronScheduleBuilder
                            .cronSchedule(config.getSchedulazione()))
                    .build();
        }
        log.info("Scheduling trigger for config n. {} in {} seconds.", config.getId(), defaultConfigDelay);
        return TriggerBuilder.newTrigger()
                .withIdentity(triggerKey)
                .startAt(DateBuilder.futureDate(defaultConfigDelay, DateBuilder.IntervalUnit.SECOND))
                .build();

    }

    private JobDataMap buildJobDataMap(Configurazione config) throws IllegalArgumentException {
        String id = String.valueOf(config.getId());
        boolean sql = config.getSqlScript() != null;
        boolean program = config.getProgramma() != null;
        boolean clasz = config.getClasse() != null;
        if((sql && program) || (sql && clasz) || (program && clasz)) {
            log.error("Configurazione n. {} has multiple conflicting fields set, skipping creation.", config.getId());
            throw new IllegalArgumentException("Configurazione has multiple conflicting fields set, only one allowed.");
        }
        if(devMode)
            log.debug("Dev Mode is enabled, logging sensitive data.");
        if (sql) {
            FonteDati fonteDati = fonteDatiService.getByID(config.getFonteDati().getId());
            SicurezzaFonteDati sicurezzaFonteDati = sicurezzaFonteDatiService.getByID(config.getUtenteFonteDati().getId());
            JobDataMap map = new JobDataMap();
            if(devMode) {
                log.debug("JobType detected: {}", JobType.SQL.getJobType());
                log.debug("SQL Script: {}", config.getSqlScript());
                log.debug("Nome: {}", config.getNome());
                log.debug("Driver name: {}", fonteDati.getNomeDriver());
                log.debug("URL: {}", fonteDati.getUrl());
                log.debug("Username: {}", sicurezzaFonteDati.getUserID());
                log.debug("Password: {}", sicurezzaFonteDati.getPassword());
            }
            map.put(ID, id);
            map.put(CONFIG + id, config);
            map.put(JOB_TYPE + id, JobType.SQL.getJobType());
            map.put(SQL_SCRIPT + id, config.getSqlScript());
            map.put(NOME, config.getNome());
            map.put(DRIVER_NAME + id, fonteDati.getNomeDriver());
            map.put(URL + id, fonteDati.getUrl());
            map.put(USERNAME + id, sicurezzaFonteDati.getUserID());
            map.put(PASSWORD + id, sicurezzaFonteDati.getPassword());
            map.put(THRESHOLDS + id, config.getSoglie());
            return map;
        }
        if(program) {
            JobDataMap map = new JobDataMap();
            if(devMode) {
                log.debug("JobType detected: {}", JobType.PROGRAM.getJobType());
                log.debug("Programma: {}", config.getProgramma());
                log.debug("Nome: {}", config.getNome());
            }
            map.put(ID, id);
            map.put(CONFIG + id, config);
            map.put(JOB_TYPE + id, JobType.PROGRAM.getJobType());
            map.put(NOME, config.getNome());
            map.put(PROGRAMMA + id, config.getProgramma());
            map.put(THRESHOLDS + id, config.getSoglie());
            return map;
        }
        if(clasz) {
            JobDataMap map = new JobDataMap();
            if(devMode) {
                log.debug("JobType detected: {}", JobType.CLASS.getJobType());
                log.debug("Classe: {}", config.getClasse());
                log.debug("Nome: {}", config.getNome());
            }
            map.put(ID, id);
            map.put(CONFIG + id, config);
            map.put(JOB_TYPE + id, JobType.CLASS.getJobType());
            map.put(NOME, config.getNome());
            map.put(CLASS + id, config.getClasseSimpleName());
            map.put(THRESHOLDS + id, config.getSoglie());
            return map;
        }
        log.error("Configurazione n. {} has no valid fields set, skipping creation.", config.getId());
        throw new IllegalArgumentException("Configurazione has no valid necessary fields set, configure exactly one" +
                "between SQLScript, Programma or Classe.");
    }

}
