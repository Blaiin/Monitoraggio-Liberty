package it.dmi.caches;

import it.dmi.data.dto.OutputDTO;
import it.dmi.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Slf4j
public class JobDataCache {

    private static final Map<String, OutputDTO> outputCache = new ConcurrentHashMap<>();

    private static final Map<String, CountDownLatch> latches = new ConcurrentHashMap<>();


    public static void put(String key, OutputDTO outputDTO) {
        outputCache.put(key, outputDTO);
    }

    public static void countDown(String key) {
        CountDownLatch latch = getLatchesMap().get(key);
        final var readableID = Utils.Strings.toReadableID(key);
        if (latch != null) {
            latch.countDown();
            log.debug("CountDown called for Task ID: {}", readableID);
            removeLatch(key);
        } else {
            log.debug("No CountDown latch found for Task ID: {}", readableID);
        }
    }

    public static Map<String, CountDownLatch> getLatchesMap() {
        return latches;
    }

    public static void removeLatch(String key) {
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

    public static @NotNull OutputDTO getOutput(String key) {
        var out = outputCache.remove(key);
        if (out.getAzioneId() != null)
            log.debug("Output retrieved for Azione {}", out.getAzioneId());
        if (out.getConfigurazioneId() != null)
            log.debug("Output retrieved for Config {}", out.getConfigurazioneId());
        return out;
    }
}
