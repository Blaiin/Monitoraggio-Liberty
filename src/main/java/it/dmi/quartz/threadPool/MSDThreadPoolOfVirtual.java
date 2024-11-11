package it.dmi.quartz.threadPool;

import it.dmi.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerConfigException;
import org.quartz.spi.ThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class MSDThreadPoolOfVirtual implements ThreadPool {

    private ExecutorService executorService;
    private final AtomicBoolean isShutdown = new AtomicBoolean(false);
    private String instanceName;

    @Override
    public int getPoolSize() {
        return 150_000;
    }

    @Override
    public void setInstanceId(String s) {
    }

    @Override
    public void setInstanceName(String s) {
        this.instanceName = s;
    }

    @Override
    public void initialize() throws SchedulerConfigException {
        if (executorService == null || executorService.isShutdown()) {
            executorService = Executors.newThreadPerTaskExecutor(new MSDThreadFactory(true));
            log.debug("{} Thread Pool initialized.", this.instanceName);
        } else {
            final String msg = String.format("Attempted to re-initialize an active Thread Pool, instanceName: %s.",
                    this.instanceName);
            log.error(msg);
            throw new SchedulerConfigException(msg);
        }
    }

    @Override
    public void shutdown(boolean waitForJobsToComplete) {
        if (isShutdown.compareAndSet(false, true)) {
            try {
                log.info("Shutting down MSD Thread Pool. Waiting for jobs to complete: {}", waitForJobsToComplete);
                if (waitForJobsToComplete) {
                    executorService.shutdown();
                    if (executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                        log.debug("Succesfully waited for jobs to finish.");
                    } else {
                        log.warn("Could not wait for jobs to finish.");
                    }
                } else {
                    var stoppedTasks = executorService.shutdownNow();
                    if (!stoppedTasks.isEmpty()) {
                        log.debug("{} tasks could not finish executing.", stoppedTasks.size());
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Thread pool shutdown interrupted.", e);
            } finally {
                if (!executorService.isTerminated()) {
                    log.warn("Some tasks did not terminate gracefully.");
                }
                log.info("{} Thread Pool shutdown complete.", Utils.Strings.capitalize(this.instanceName));
            }
        }
    }

    @Override
    public boolean runInThread(final Runnable runnable) {
        if (isShutdown.get()) {
            log.warn("Attempted to run a job after Thread Pool shutdown.");
            return false;
        }
        try {
            executorService.submit(runnable);
            return true;
        } catch (RejectedExecutionException e) {
            log.error("Task submission failed. Executor may be shutting down.", e);
            return false;
        } catch (Exception e) {
            log.error("Error while trying to run task. ", e);
            return false;
        }
    }

    @Override
    public int blockForAvailableThreads() {
        return 150_000;
    }
}
