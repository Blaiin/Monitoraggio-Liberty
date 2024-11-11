package it.dmi.caches;

import it.dmi.data.entities.task.Azione;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SuppressWarnings("unused")
@Slf4j
public class AzioneQueueCache {

    private static final ConcurrentMap<String, List<Azione>> queue = new ConcurrentHashMap<>();

    private static final ConcurrentMap<String, List<String>> soglieIDs = new ConcurrentHashMap<>();

    public static void queue(String sogliaId, Azione azione) {
        queue.computeIfAbsent(sogliaId, value -> new ArrayList<>()).add(azione);
    }

    public static void put(String identifier, List<String> ids) {
        soglieIDs.computeIfAbsent(identifier, v -> new ArrayList<>()).addAll(ids);
    }

    public static Optional<List<Azione>> getAzioni(String sogliaId) {
        return Optional.ofNullable(queue.remove(sogliaId));
    }

    public static Optional<List<String>> getSoglieIDs(String soglieID) {
        log.info("SOGLIE IDS: {}", soglieID);
        return Optional.ofNullable(soglieIDs.remove(soglieID));
    }

    public static @NotNull Map<String, List<Azione>> getAll() {
        var entries = new HashMap<>(queue);
        queue.clear();
        return entries;
    }

    public static void remove(String sogliaId) {
        queue.remove(sogliaId);
    }

    public static long getCacheSize() {
        return queue.size();
    }
}
