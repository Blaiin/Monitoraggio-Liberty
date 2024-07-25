package it.sogei.quartz.ejb;

import it.sogei.data_access.shared.RestDataCache;
import it.sogei.quartz.jobs.IQueryJob;
import it.sogei.quartz.jobs.QueryJob;
import jakarta.annotation.Resource;
import jakarta.ejb.DependsOn;
import jakarta.ejb.EJB;
import jakarta.ejb.Singleton;
import jakarta.ejb.Startup;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Singleton
@Startup
@DependsOn("SchedulerEJB")
@Slf4j
public class JobManagerEJB {

    List<Class<? extends IQueryJob>> queryJobs = List.of(QueryJob.class
                                                        //QueryJob2.class
                                                        );

    public JobManagerEJB() {
        log.info("JobManagerEJB initialized.");
    }
    private final String query = "SELECT * FROM test";

    @EJB
    private SchedulerEJB schedulerEJB;

    @Resource
    ManagedExecutorService executorService;


    //@PostConstruct
    public void init() {
        executorService.submit(this::scheduleJobs);
    }
    public void scheduleJobs() {
        try {
            schedulerEJB.getScheduler().start();
            log.info("Scheduler started");
            RestDataCache.createLatch("names", 1);
            log.info("Latch \"names\" created");

            for(Class<? extends IQueryJob> queryJob : queryJobs) {
                JobInfo jobInfo = buildJobInfo(queryJob);
                schedulerEJB.getScheduler().scheduleJob(jobInfo.jobDetail(), jobInfo.trigger());
            }
            RestDataCache.awaitData("names", 15, TimeUnit.SECONDS);
            log.info("Awaiting data from job...");
        } catch (SchedulerException e) {
            log.error("Failed to schedule job", e);
            throw new RuntimeException("Failed to schedule job", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            log.info("Retrieving from cache...");
            Object names =  RestDataCache.get("names");
            if (names instanceof List) {
                ((List<?>) names).forEach(System.out::println);
            }
        }
    }

    public JobInfo buildJobInfo(Class<? extends IQueryJob> jobClass) {
        String jobName = jobClass.getSimpleName();
        String groupName = jobClass.getPackageName();
        try {
            JobDetail jobDetail = getJobDetail(new JobKey(jobName, groupName), jobClass);
            Trigger trigger = getTrigger(jobName, groupName, jobDetail, 10);
            log.info("Job scheduled for table test with trigger 10 seconds from now.");
            return new JobInfo(jobDetail, trigger);
        } catch (SchedulerException e) {
            log.error("Failed to build job info", e);
            throw new RuntimeException(e);
        }
    }

    private JobDetail getJobDetail(JobKey jobKey, Class<? extends IQueryJob> jobClass) throws SchedulerException {
        if (schedulerEJB.getScheduler().checkExists(jobKey)) {
            log.info("Job already exists: {}", jobKey);
            return schedulerEJB.getScheduler().getJobDetail(jobKey);
        }

        JobBuilder jobBuilder = JobBuilder.newJob(jobClass)
                .withIdentity(jobKey);

        if (QueryJob.class.isAssignableFrom(jobClass)) {
            jobBuilder.usingJobData("url", "jdbc:postgresql://localhost:5432/Monitoraggio")
                    .usingJobData("username", "postgres")
                    .usingJobData("password", "postgres")
                    .usingJobData("query", query);
        }
        return jobBuilder.build();
    }

    private Trigger getTrigger(String name, String group, JobDetail job, int fromNow) {
        return TriggerBuilder.newTrigger()
                .withIdentity(name, group)
                .startAt(DateBuilder.futureDate(fromNow, DateBuilder.IntervalUnit.SECOND))
                .forJob(job)
                .build();
    }

    public record JobInfo(JobDetail jobDetail, Trigger trigger) {
    }
}
