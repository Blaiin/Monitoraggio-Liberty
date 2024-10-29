package it.dmi.quartz.threadPool;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.quartz.SchedulerConfigException;
import org.quartz.spi.ThreadPool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static it.dmi.utils.constants.NamingConstants.*;

@Slf4j
public class MSDVirtualThreadPool implements ThreadPool {

    private ExecutorService executorService;
    private final AtomicBoolean isShutdown = new AtomicBoolean(false);
    private String schedulerInstanceName;

    @Override
    public int getPoolSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void setInstanceId(String s) {
    }

    @Override
    public void setInstanceName (String s) {
        this.schedulerInstanceName = s;
    }

    @Override
    public void initialize() throws SchedulerConfigException {
        if (executorService == null || executorService.isShutdown()) {
            executorService = Executors.newThreadPerTaskExecutor(new MSDVirtualThreadFactory());
            log.debug("Initialized MSD Thread Pool with virtual threads.");
        } else {
            final String msg = "Attempted to re-initialize an active VirtualThreadPool.";
            log.error(msg);
            throw new SchedulerConfigException(msg);
        }
    }

    @Override
    public void shutdown(boolean waitForJobsToComplete) {
        if (isShutdown.compareAndSet(false, true)) {
            try {
                log.info("Shutting down VirtualThreadPool. Waiting for jobs to complete: {}", waitForJobsToComplete);
                if (waitForJobsToComplete) {
                    executorService.shutdown();
                    if (executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                        log.debug("Succesfully waited for jobs to finish.");
                    } else {
                        log.warn("Could not wait for jobs to finish.");
                    }
                } else {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Thread pool shutdown interrupted.", e);
            } finally {
                if (!executorService.isTerminated()) {
                    log.warn("Some tasks did not terminate gracefully.");
                }
                log.info("VirtualThreadPool shutdown complete.");
            }
        }
    }

    @Override
    public boolean runInThread(Runnable runnable) {
        if (isShutdown.get()) {
            log.warn("Attempted to run a job after VirtualThreadPool shutdown.");
            return false;
        }
        try {
            executorService.submit(runnable);
            return true;
        } catch (RejectedExecutionException e) {
            log.error("Task submission failed. Executor may be shutting down.", e);
            return false;
        }
    }

    @Override
    public int blockForAvailableThreads() {
        return Integer.MAX_VALUE;
    }

    final class MSDVirtualThreadFactory implements ThreadFactory {

        private final String baseThreadName;
        private final AtomicInteger threadUsageCount = new AtomicInteger(0);

        MSDVirtualThreadFactory() {
            if(schedulerInstanceName.contains(AZIONE.toUpperCase())) this.baseThreadName = AZIONE + WORKER;
            else if (schedulerInstanceName.contains(CONFIGURAZIONE.toUpperCase())) this.baseThreadName = CONFIGURAZIONE + WORKER;
            else this.baseThreadName = WORKER;
        }

        @Override
        public Thread newThread(@NotNull Runnable r) {
            return Thread.ofVirtual().name(baseThreadName + threadUsageCount.incrementAndGet()).unstarted(r);
        }
    }
}
