package it.dmi.caches;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public abstract class ASharedCache {

    private static final Map<String, Collection<Object>> cache = new ConcurrentHashMap<>();

    private static final Map<String, CountDownLatch> latches = new ConcurrentHashMap<>();

    public static synchronized void put(String key, List<?> values) {
        cache.computeIfAbsent(key, value -> new ArrayList<>()).addAll(values);
        if (latches.containsKey(key)) {
            latches.get(key).countDown();
        }
    }

    public static synchronized void put(String key, Map<?, ?> values) {
        cache.computeIfAbsent(key, value -> new ArrayList<>()).add(values);
        if (latches.containsKey(key)) {
            latches.get(key).countDown();
        }
    }

    public static Collection<?> get(String key) {
        return cache.get(key);
    }

    public static Map<String, CountDownLatch> getLatchesMap() {
        return latches;
    }

    public static void removeLatch(String key) {
        cache.remove(key);
        latches.remove(key);
    }

    public static void createLatch(String key, int count) {
        log.debug("Creating latch with key {}", key);
        latches.put(key, new CountDownLatch(count));
    }

    public static boolean awaitData(String key, long timeout, TimeUnit unit) throws InterruptedException {
        CountDownLatch latch = latches.get(key);
        if (latch != null) {
            return latch.await(timeout, unit);
        }
        return false;
    }

    public static List<String> getLatches() {
        return new ArrayList<>(latches.keySet());
    }
}
