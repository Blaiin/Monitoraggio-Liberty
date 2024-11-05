package it.dmi.quartz.builders;

import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import it.dmi.quartz.jobs.ClassJob;
import it.dmi.quartz.jobs.ProgramJob;
import it.dmi.quartz.jobs.sql.impl.SelectCountJob;
import it.dmi.quartz.jobs.sql.impl.SelectJob;
import it.dmi.structure.exceptions.MSDException;
import it.dmi.structure.exceptions.impl.quartz.JobTypeException;
import it.dmi.structure.internal.JobType;
import it.dmi.structure.internal.QueryType;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.quartz.*;

import static it.dmi.processors.jobs.QueryResolver.resolveQuery;
import static it.dmi.utils.constants.NamingConstants.JOB_TYPE;

@Slf4j
public class JobDetailBuilder {

    public static JobDetail buildJobDetail(Scheduler scheduler, QuartzTask task, JobKey jobKey)
            throws SchedulerException, JSQLParserException, JobTypeException {
        checkIfJobExists(scheduler, jobKey, task);
        JobDataMap jobDataMap;
        try {
            jobDataMap = JobDataMapBuilder.buildJobDataMap(task);
        } catch (MSDException e) {
            throw new SchedulerException(e);
        }
        JobType jobType = getJobType(jobDataMap, task);
        return createJobBuilder(jobType, jobKey, jobDataMap, task).build();
    }

    private static JobBuilder createJobBuilder(JobType jobType, JobKey jobKey, JobDataMap jobDataMap, QuartzTask task)
            throws JSQLParserException, JobTypeException {
        return switch (jobType) {
            case SQL -> createSqlJobBuilder(task, jobKey, jobDataMap);
            case PROGRAM -> JobBuilder.newJob(ProgramJob.class)
                    .withIdentity(jobKey)
                    .usingJobData(jobDataMap);
            case CLASS -> JobBuilder.newJob(ClassJob.class)
                    .withIdentity(jobKey)
                    .usingJobData(jobDataMap);
            case OTHER -> throw new JobTypeException("JobType not supported.");
        };
    }

    private static JobBuilder createSqlJobBuilder(QuartzTask task, JobKey jobKey, JobDataMap jobDataMap)
            throws JSQLParserException {
        QueryType queryType = resolveQuery(task.getSqlScript());
        log.debug("Query type detected: {}", queryType.getQueryType());
        return switch (queryType) {
            case SELECT -> JobBuilder.newJob(SelectJob.class)
                    .withIdentity(jobKey)
                    .usingJobData(jobDataMap);
            case SELECT_COUNT -> JobBuilder.newJob(SelectCountJob.class)
                    .withIdentity(jobKey)
                    .usingJobData(jobDataMap);
            case INSERT, UPDATE, DELETE ->
                    throw new IllegalArgumentException(String.format("Query type (%s) not supported yet.",
                            queryType.getQueryType()));
            case NOT_SUPPORTED -> throw new IllegalArgumentException("Unexpected Query type: " + queryType);
        };
    }

    private static void checkIfJobExists(Scheduler scheduler, JobKey jobKey, QuartzTask task)
            throws SchedulerException {
        if (scheduler.checkExists(jobKey)) {
            switch (task) {
                case Configurazione c -> {
                    log.warn("Jobs with key {}, Configurazione {} already exists, skipping creation.", jobKey, c.getId());
                    throw new SchedulerException(String.format("Jobs for Configurazione %d already exists.", c.getId()));
                }
                case Azione a -> {
                    log.warn("Jobs with key {}, Azione {} already exists, skipping creation.", jobKey, a.getId());
                    throw new SchedulerException(String.format("Jobs for Azione %d already exists.", a.getId()));
                }
            }
        }
    }

    private static JobType getJobType(JobDataMap jobDataMap, QuartzTask task) {
        return JobType.valueOf(jobDataMap.getString(JOB_TYPE + task.getStringID()));
    }

}
