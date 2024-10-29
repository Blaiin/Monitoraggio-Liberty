package it.dmi.caches;

import it.dmi.data.entities.task.Azione;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
public class AzioneQueueCache {

    private static final ConcurrentMap<String, List<Azione>> cache = new ConcurrentHashMap<>();

    public static void put(String sogliaId, Azione azione) {
        cache.computeIfAbsent(sogliaId, value -> new ArrayList<>()).add(azione);
    }

    public static List<Azione> get(String sogliaId) {
        return cache.remove(sogliaId);
    }

    public static Map<String, List<Azione>> getAll() {
        var entries = new HashMap<>(cache);
        cache.clear();
        return entries;
    }

    public static void remove(String sogliaId) {
        cache.remove(sogliaId);
    }

    //TODO properly utilize clearing logic
    @SuppressWarnings("unused")
    public static void clearAll() {
        log.debug("Clearing {} Azioni.", cache.size());
        cache.clear();
    }

    public static long getCacheSize() {
        return cache.size();
    }
}
