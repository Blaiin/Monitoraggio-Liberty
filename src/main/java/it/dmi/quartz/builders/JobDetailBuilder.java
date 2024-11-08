package it.dmi.quartz.builders;

import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import it.dmi.quartz.jobs.ClassJob;
import it.dmi.quartz.jobs.ProgramJob;
import it.dmi.quartz.jobs.sql.impl.SelectCountJob;
import it.dmi.quartz.jobs.sql.impl.SelectJob;
import it.dmi.structure.exceptions.MSDException;
import it.dmi.structure.exceptions.impl.quartz.JobAlreadyDefinedException;
import it.dmi.structure.exceptions.impl.quartz.JobTypeException;
import it.dmi.structure.internal.JobType;
import it.dmi.structure.internal.QueryType;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quartz.*;

import java.util.Optional;

import static it.dmi.processors.jobs.QueryResolver.resolveQuery;
import static it.dmi.utils.constants.NamingConstants.JOB_TYPE;

@Slf4j
public class JobDetailBuilder {

    public static @Nullable JobDetail buildJobDetail(Scheduler scheduler, QuartzTask task, JobKey jobKey)
            throws SchedulerException, JSQLParserException, JobTypeException, JobAlreadyDefinedException {
        if (checkIfJobExists(scheduler, jobKey, task))
            throw new JobAlreadyDefinedException(String.format("Job with key %s already defined.", jobKey));
        JobDataMap jobDataMap;
        try {
            jobDataMap = JobDataMapBuilder.buildJobDataMap(task);
        } catch (MSDException e) {
            throw new SchedulerException(e);
        }
        Optional<JobType> jobType = getJobType(jobDataMap, task.getStrID());
        if (jobType.isPresent()) {
            return createJobDetail(jobType.get(), jobKey, jobDataMap, task);
        }
        log.error("Could not resolve Job Type, skip creation.");
        return null;
    }

    private static JobDetail createJobDetail(@NotNull JobType jobType, JobKey jobKey, JobDataMap jobDataMap, QuartzTask task)
            throws JSQLParserException, JobTypeException {
        return switch (jobType) {
            case SQL -> createSqlJobBuilder(task, jobKey, jobDataMap);
            case PROGRAM -> JobBuilder.newJob(ProgramJob.class)
                    .withIdentity(jobKey)
                    .usingJobData(jobDataMap)
                    .storeDurably(false)
                    .build();
            case CLASS -> JobBuilder.newJob(ClassJob.class)
                    .withIdentity(jobKey)
                    .usingJobData(jobDataMap)
                    .storeDurably(false)
                    .build();
            case OTHER -> throw new JobTypeException("JobType not supported.");
        };
    }

    private static JobDetail createSqlJobBuilder(@NotNull QuartzTask task, JobKey jobKey, JobDataMap jobDataMap)
            throws JSQLParserException {
        QueryType queryType = resolveQuery(task.getSqlScript());
        log.debug("Query type detected: {}", queryType.getQueryType());
        return switch (queryType) {
            case SELECT -> defineJobDurability(task, SelectJob.class, jobKey, jobDataMap);
            case SELECT_COUNT -> defineJobDurability(task, SelectCountJob.class, jobKey, jobDataMap);
            case INSERT, UPDATE, DELETE ->
                    throw new IllegalArgumentException(String.format("Query type (%s) not supported yet.",
                            queryType.getQueryType()));
            case NOT_SUPPORTED -> throw new IllegalArgumentException("Unexpected Query type: " + queryType);
        };
    }

    private static JobDetail defineJobDurability(QuartzTask task, Class<? extends Job> jobClass,
                                                 JobKey key, JobDataMap map) {
        return switch (task) {
            //In caso di Azione il job dev'essere eseguito una sola volta per design
            case Azione ignored -> JobBuilder
                    .newJob(jobClass)
                    .withIdentity(key)
                    .usingJobData(map)
                    .storeDurably(false)
                    .build();
            //in caso di configurazione si controlla che la schedulazione sia
            //      valorizzata -> il job deve rimanere in memoria così che il check in caso di nuova schedulazione
            //                      possa fare lo skip
            //      nulla -> il job viene trattato come singola esecuzione
            case Configurazione c -> {
                if (c.getSchedulazione() != null) {
                    yield JobBuilder
                            .newJob(jobClass)
                            .withIdentity(key)
                            .usingJobData(map)
                            .build();
                } else {
                    yield JobBuilder
                            .newJob(jobClass)
                            .withIdentity(key)
                            .usingJobData(map)
                            .build();
                }
            }
        };
    }

    private static boolean checkIfJobExists(@NotNull Scheduler scheduler, JobKey jobKey, QuartzTask task)
            throws SchedulerException {
        if (scheduler.checkExists(jobKey)) {
            switch (task) {
                case Configurazione c -> {
                    log.warn("Jobs with key {}, Configurazione {} already exists, skipping creation.",
                            jobKey, c.getId());
                    return true;
                }
                case Azione a -> {
                    log.warn("Jobs with key {}, Azione {} already exists, skipping creation.", jobKey, a.getId());
                    return true;
                }
            }
        } return false;
    }

    private static Optional<JobType> getJobType(@NotNull JobDataMap jobDataMap, String taskID) {
        final String jobTypeKey = JOB_TYPE + taskID;
        Object jobTypeObj = jobDataMap.get(jobTypeKey);
        switch (jobTypeObj) {
            case String jobTypeStr -> {
                try {
                    return Optional.of(JobType.valueOf(jobTypeStr));
                } catch (IllegalArgumentException e) {
                    log.warn("Invalid Job Type string '{}' for task ID: {}", jobTypeStr, taskID);
                }
            }
            case JobType jobType -> {
                return Optional.of(jobType);
            }
            case null, default ->
                    log.warn("Job Type for task ID '{}' is not a valid String or JobType instance", taskID);
        }
        return Optional.empty();
    }


}
