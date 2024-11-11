package it.dmi.quartz.threadPool;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import static it.dmi.utils.constants.NamingConstants.WORKER;

public final class MSDThreadFactory implements ThreadFactory {

    private final String threadName;
    private final AtomicInteger THREAD_USAGE_COUNT = new AtomicInteger(0);

    public MSDThreadFactory(boolean quartzJobsUsage) {
        if (quartzJobsUsage) {
            this.threadName = "MSD_" + WORKER;
            return;
        }
        this.threadName = "Manager_" + WORKER;
    }

    @Override
    public Thread newThread(@NotNull final Runnable r) {
        return Thread.ofVirtual()
                .name(threadName + THREAD_USAGE_COUNT.incrementAndGet())
                .unstarted(r);
    }
}
