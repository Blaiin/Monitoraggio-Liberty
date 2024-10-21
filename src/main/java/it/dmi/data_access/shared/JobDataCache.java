package it.dmi.data_access.shared;

import java.util.concurrent.CountDownLatch;

public class JobDataCache extends ASharedCache {

    public static void countDown(String key) {
        CountDownLatch latch = getLatchesMap().get(key);
        if (latch != null) {
            latch.countDown();
        }
    }
}
