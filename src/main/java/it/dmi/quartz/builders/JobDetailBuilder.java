package it.dmi.quartz.builders;

import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import it.dmi.quartz.jobs.ClassJob;
import it.dmi.quartz.jobs.ProgramJob;
import it.dmi.quartz.jobs.sql.SelectCountJob;
import it.dmi.quartz.jobs.sql.SelectJob;
import it.dmi.structure.exceptions.MSDException;
import it.dmi.structure.exceptions.impl.quartz.JobTypeException;
import it.dmi.structure.internal.JobType;
import it.dmi.structure.internal.QueryType;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import org.quartz.*;

import static it.dmi.processors.jobs.QueryResolver.resolveQuery;
import static it.dmi.utils.constants.NamingConstants.JOB_TYPE;

@Slf4j
@RequestScoped
public class JobDetailBuilder {

    @Inject
    private JobDataMapBuilder dataMapBuilder;

    public JobDetail buildJobDetail(Scheduler scheduler, QuartzTask task, JobKey jobKey)
            throws SchedulerException, JSQLParserException, JobTypeException {
        checkIfJobExists(scheduler, jobKey, task);
        JobDataMap jobDataMap;
        try {
            jobDataMap = dataMapBuilder.buildJobDataMap(task);
        } catch (MSDException e) {
            throw new SchedulerException(e);
        }
        JobType jobType = getJobType(jobDataMap, task);
        return createJobBuilder(jobType, jobKey, jobDataMap, task).build();
    }

    private JobBuilder createJobBuilder(JobType jobType, JobKey jobKey, JobDataMap jobDataMap, QuartzTask task)
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

    private JobBuilder createSqlJobBuilder(QuartzTask task, JobKey jobKey, JobDataMap jobDataMap)
            throws JSQLParserException {
        QueryType queryType = resolveQuery(task.getSqlScript());
        return switch (queryType) {
            case SELECT -> JobBuilder.newJob(SelectJob.class)
                    .withIdentity(jobKey)
                    .usingJobData(jobDataMap);
            case SELECT_COUNT -> JobBuilder.newJob(SelectCountJob.class)
                    .withIdentity(jobKey)
                    .usingJobData(jobDataMap);
            case INSERT, UPDATE, DELETE ->
                    throw new IllegalArgumentException(String.format("QueryType (%s) not supported.", queryType.getQueryType()));
            default -> throw new IllegalArgumentException("Unexpected QueryType: " + queryType);
        };
    }

    private void checkIfJobExists(Scheduler scheduler, JobKey jobKey, QuartzTask task)
            throws SchedulerException {
        if (scheduler.checkExists(jobKey)) {
            switch (task) {
                case Configurazione c -> {
                    log.warn("Job with key {}, Configurazione {} already exists, skipping creation.", jobKey, c.getId());
                    throw new SchedulerException(String.format("Job for Configurazione %d already exists.", c.getId()));
                }
                case Azione a -> {
                    log.warn("Job with key {}, Azione {} already exists, skipping creation.", jobKey, a.getId());
                    throw new SchedulerException(String.format("Job for Azione %d already exists.", a.getId()));
                }
                default -> throw new SchedulerException("Illegal task: " + task);
            }
        }
    }

    private JobType getJobType(JobDataMap jobDataMap, QuartzTask task) {
        return JobType.valueOf(jobDataMap.getString(JOB_TYPE + task.getStringID()));
    }

}
