package it.dmi.caches;

import it.dmi.data.dto.OutputDTO;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import static it.dmi.utils.constants.NamingConstants.CONFIG;

@Slf4j
public class JobDataCache extends ASharedCache {

    private static final Map<String, OutputDTO> outputCache = new ConcurrentHashMap<>();

    public static void put(String key, OutputDTO outputDTO) {
        outputCache.put(key, outputDTO);
    }
    public static void countDown(String key) {
        CountDownLatch latch = getLatchesMap().get(key);
        if (latch != null) {
            latch.countDown();
            log.debug("CountDown called for Config ID: {}", key.replace(CONFIG, ""));
        } else {
            log.debug("Not CountDown latch found for Task ID: {}", key);
        }
    }

    public static OutputDTO getOutput(String key) {
        var out = outputCache.get(key);
        outputCache.remove(key);
        log.debug("Output retrieved for Config ID: {}", out.getConfigurazioneId());
        return out;
    }
}
