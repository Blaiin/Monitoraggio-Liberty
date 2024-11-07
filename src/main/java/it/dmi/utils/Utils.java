package it.dmi.utils;

import it.dmi.data.entities.FonteDati;
import it.dmi.data.entities.SicurezzaFonteDati;
import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import it.dmi.quartz.ejb.Manager;
import it.dmi.quartz.listeners.AzioneJobListener;
import it.dmi.quartz.listeners.ConfigurazioneJobListener;
import it.dmi.structure.exceptions.MSDRuntimeException;
import it.dmi.structure.exceptions.impl.quartz.JobBuildingException;
import it.dmi.structure.internal.JobType;
import it.dmi.structure.internal.info.JobInfo;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.quartz.JobDataMap;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.matchers.KeyMatcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static it.dmi.utils.constants.NamingConstants.*;

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

    public static class Jobs {

        public static void addJobListener(Manager manager, Scheduler scheduler,
                                          @NotNull QuartzTask task, JobInfo jobInfo) {
            try {
                switch (task) {
                    case Azione a ->
                            scheduler.getListenerManager().addJobListener(
                                    new AzioneJobListener(a, manager),
                                    KeyMatcher.keyEquals(jobInfo.jobDetail().getKey())
                            );
                    case Configurazione c ->
                            scheduler.getListenerManager().addJobListener(
                                    new ConfigurazioneJobListener(c, manager),
                                    KeyMatcher.keyEquals(jobInfo.jobDetail().getKey())
                            );
                }
            } catch (SchedulerException e) {
                log.error("Error adding Jobs Listener for Task {}", task.getStrID());
                throw new MSDRuntimeException(e);
            }
        }

        public static JobType resolveJobType(@NotNull QuartzTask task) throws JobBuildingException {
            boolean sql = task.getSqlScript() != null;
            boolean program = task.getProgramma() != null;
            boolean classe = task.getClasse() != null;
            if((sql && program) || (sql && classe) || (program && classe)) {
                String logMsg = "%s {} has multiple conflicting fields set, skipping creation."
                    , excMsg = "%s has multiple conflicting fields set, only one allowed.";
                switch (task) {
                    case Azione a -> {
                        final var simpleName = a.getClass().getSimpleName();
                        logMsg = String.format(logMsg, simpleName);
                        excMsg = String.format(excMsg, simpleName);
                    }
                    case Configurazione c -> {
                        final var simpleName = c.getClass().getSimpleName();
                        logMsg = String.format(logMsg, simpleName);
                        excMsg = String.format(excMsg, simpleName);
                    }
                }
                log.error(logMsg, task.getId());
                throw new JobBuildingException(excMsg);
            }
            if(sql) return JobType.SQL;
            if(program) return JobType.PROGRAM;
            if(classe) return JobType.CLASS;
            return JobType.OTHER;
        }

        public static JobDataMap createCLASSEJobDataMap(Configurazione config, @NotNull JobDataMap map, String id) {
            map.put(ID, id);
            map.put(CONFIG + id, config);
            map.put(JOB_TYPE + id, JobType.CLASS.getJobType());
            map.put(NOME, config.getNome());
            map.put(CLASS + id, config.getClasse());
            map.put(THRESHOLDS + id, config.getSoglie());
            return map;
        }

        public static void createCLASSEJobDataMap(Azione azione, @NotNull JobDataMap map, String id) {
            map.put(ID, id);
            map.put(AZIONE + id, azione);
            map.put(JOB_TYPE + id, JobType.CLASS.getJobType());
            map.put(CLASS + id, azione.getClasse());
        }

        public static JobDataMap createPROGRAMJobDataMap(Configurazione config, @NotNull JobDataMap map, String id) {
            map.put(ID, id);
            map.put(CONFIG + id, config);
            map.put(JOB_TYPE + id, JobType.PROGRAM.getJobType());
            map.put(NOME, config.getNome());
            map.put(PROGRAMMA + id, config.getProgramma());
            map.put(THRESHOLDS + id, config.getSoglie());
            return map;
        }

        public static void createPROGRAMJobDataMap(Azione azione, @NotNull JobDataMap map, String id) {
            map.put(ID, id);
            map.put(CONFIG + id, azione);
            map.put(JOB_TYPE + id, JobType.PROGRAM.getJobType());
            map.put(PROGRAMMA + id, azione.getProgramma());
        }

        public static JobDataMap populateSQLJobDataMap(Configurazione config, @NotNull JobDataMap map,
                                                 @NotNull JobType jobType, @NotNull FonteDati fd, @NotNull SicurezzaFonteDati sfd) {
            var cID = config.getStrID();
            map.put(ID, cID);
            map.put(TASK + cID, config);
            map.put(JOB_TYPE + cID, jobType.getJobType());
            map.put(SQL_SCRIPT + cID, config.getSqlScript());
            map.put(NOME, config.getNome());
            map.put(DRIVER_NAME + cID, fd.getNomeDriver());
            map.put(URL + cID, fd.getUrl());
            map.put(USERNAME + cID, sfd.getUserID());
            map.put(PASSWORD + cID, sfd.getPassword());
            map.put(THRESHOLDS + cID, config.getSoglie());
            return map;
        }

        public static void createSQLJobDataMap(Azione azione, @NotNull JobDataMap map, String id,
                                               @NotNull JobType jobType, @NotNull FonteDati fd, @NotNull SicurezzaFonteDati sfd) {
            map.put(ID, id);
            map.put(TASK + id, azione);
            map.put(JOB_TYPE + id, jobType.getJobType());
            map.put(SQL_SCRIPT + id, azione.getSqlScript());
            map.put(DRIVER_NAME + id, fd.getNomeDriver());
            map.put(URL + id, fd.getUrl());
            map.put(USERNAME + id, sfd.getUserID());
            map.put(PASSWORD + id, sfd.getPassword());
        }
    }

    public static class DebugLogger {

        private static void debugJobTypeDetection(String jobType) {
            log.debug("JobType detected: {}", jobType);
        }
        private static void debugName(String name) {
            log.debug("Nome: {}", name);
        }
        private static void debugID(String id) {
            log.debug("Job ID: {}", id);
        }
        private static void debugScript(String script) {
            log.debug("SQL Script: {}", script);
        }
        private static void debugClasse(String classe) {
            log.debug("Classe: {}", classe);
        }
        private static void debugProgramma(String programma) {
            log.debug("Programma: {}", programma);
        }
        private static void debugDriverName(String driverName) {
            log.debug("Driver name: {}", driverName);
        }
        private static void debugURL(String url) {
            log.debug("URL: {}", url);
        }
        public static void debug(boolean devMode, Configurazione config, JobType jobType, FonteDati fd) {
            if(devMode) {
                debugJobTypeDetection(jobType.getJobType());
                debugScript(config.getSqlScript());
                debugName(config.getNome());
                debugDriverName(fd.getNomeDriver());
                debugURL(fd.getUrl());
            }
        }

        public static void debug(boolean devMode, Configurazione config, JobType jobType) {
            if(devMode) {
                debugJobTypeDetection(jobType.getJobType());
                debugID(config.getStrID());
                if (jobType == JobType.CLASS) {
                    debugClasse(config.getClasse());
                } else if (jobType == JobType.PROGRAM) {
                    debugProgramma(config.getProgramma());
                }
                debugName(config.getNome());
            }
        }

        public static void debug(boolean devMode, Azione azione, JobType jobType, FonteDati fd) {
            if(devMode) {
                debugJobTypeDetection(jobType.getJobType());
                debugID(azione.getStrID());
                debugScript(azione.getSqlScript());
                debugDriverName(fd.getNomeDriver());
                debugURL(fd.getUrl());
            }
        }

        public static void debug(boolean devMode, Azione azione, JobType jobType) {
            if(devMode) {
                debugJobTypeDetection(jobType.getJobType());
                if (jobType == JobType.CLASS) {
                    debugClasse(azione.getClasse());
                } else if (jobType == JobType.PROGRAM) {
                    debugProgramma(azione.getProgramma());
                }
            }
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

    public static <V> List<V> transformAndReturn(Object toSanitize, Class<V> transformTo) {
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
