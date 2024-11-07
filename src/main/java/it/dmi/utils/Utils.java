package it.dmi.utils;

import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import it.dmi.structure.internal.info.JobInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static it.dmi.utils.constants.NamingConstants.AZIONE;
import static it.dmi.utils.constants.NamingConstants.CONFIG;

@Slf4j
public class Utils {

    public static class Strings {

        @Contract("null -> fail")
        public static @NotNull String capitalize(String s) {
            if (s == null) throw new IllegalArgumentException("String to capitalize cannot be null");
            return s.substring(0, 1).toUpperCase() + s.substring(1);
        }

        public static @Nullable String toReadableID(@NotNull String key) {
            if (key.contains(CONFIG))
                return capitalize(CONFIG) + " " + key.replace(CONFIG, "");
            if (key.contains(AZIONE))
                return capitalize(AZIONE) + " " +key.replace(AZIONE, "");
            return null;
        }
    }

    public static long calculateWaitTime(@NotNull QuartzTask task, JobInfo info) {
        final var maxDelay = TimeUnit.SECONDS.toMillis(3600);
        switch (task) {
            case Configurazione c -> {
                if (c.getSchedulazione() == null)
                    return info.trigger().getStartTime().getTime() - System.currentTimeMillis() + (10 * 1000);
                else return maxDelay;
            }
            case Azione ignored -> {
                return maxDelay;
            }
        }
    }

    public static <V> @NotNull List<V> transformAndReturn(Object toSanitize, Class<V> transformTo) {
        Objects.requireNonNull(toSanitize, "Sanitization failed.");
        log.debug("Sanitizing output");
        var sanitized = new ArrayList<V>();
        try {
            if(toSanitize instanceof List<?> l)
                if (!l.isEmpty()) {
                    for (Object o : l) {
                        if(transformTo.isInstance(o)) {
                            sanitized.add(transformTo.cast(o));
                        }
                    }
                } else log.debug("Could not sanitize objects from List, 99% not a bug, ignore");
        } catch (ClassCastException e) {
            log.error("Could not process type checking process, skipping. {}", e.getMessage(), e.getCause());
        }
        return sanitized;
    }
}
