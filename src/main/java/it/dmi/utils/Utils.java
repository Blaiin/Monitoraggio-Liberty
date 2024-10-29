package it.dmi.utils;

import it.dmi.caches.AzioneQueueCache;
import it.dmi.data.entities.FonteDati;
import it.dmi.data.entities.SicurezzaFonteDati;
import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import it.dmi.structure.exceptions.impl.quartz.JobBuildingException;
import it.dmi.structure.internal.JobType;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;

import java.util.ArrayList;
import java.util.List;

import static it.dmi.utils.constants.NamingConstants.*;

@Slf4j
public class Utils {

    public static class StringHelper {

        public static String capitalize(String s) {
            if (s == null) throw new IllegalArgumentException("String to capitalize cannot be null");
            return s.substring(0, 1).toUpperCase() + s.substring(1);
        }
    }

    public static class JobHelper {

        public static JobType resolveJobType(QuartzTask task) throws JobBuildingException {
            boolean sql = task.getSqlScript() != null;
            boolean program = task.getProgramma() != null;
            boolean classe = task.getClasse() != null;
            if((sql && program) || (sql && classe) || (program && classe)) {
                log.error("Configurazione n. {} has multiple conflicting fields set, skipping creation.", task.getId());
                throw new JobBuildingException("Configurazione has multiple conflicting fields set, only one allowed.");
            }
            if(sql) return JobType.SQL;
            if(program) return JobType.PROGRAM;
            if(classe) return JobType.CLASS;
            return JobType.OTHER;
        }

        public static void createCLASSEJobDataMap(Configurazione config, JobDataMap map, String id) {
            map.put(ID, id);
            map.put(CONFIG + id, config);
            map.put(JOB_TYPE + id, JobType.CLASS.getJobType());
            map.put(NOME, config.getNome());
            map.put(CLASS + id, config.getClasse());
            map.put(THRESHOLDS + id, config.getSoglie());
        }

        public static void createCLASSEJobDataMap(Azione azione, JobDataMap map, String id) {
            map.put(ID, id);
            map.put(AZIONE + id, azione);
            map.put(JOB_TYPE + id, JobType.CLASS.getJobType());
            map.put(CLASS + id, azione.getClasse());
        }

        public static void createPROGRAMJobDataMap(Configurazione config, JobDataMap map, String id) {
            map.put(ID, id);
            map.put(CONFIG + id, config);
            map.put(JOB_TYPE + id, JobType.PROGRAM.getJobType());
            map.put(NOME, config.getNome());
            map.put(PROGRAMMA + id, config.getProgramma());
            map.put(THRESHOLDS + id, config.getSoglie());
        }

        public static void createPROGRAMJobDataMap(Azione azione, JobDataMap map, String id) {
            map.put(ID, id);
            map.put(CONFIG + id, azione);
            map.put(JOB_TYPE + id, JobType.PROGRAM.getJobType());
            map.put(PROGRAMMA + id, azione.getProgramma());
        }

        public static void createSQLJobDataMap(Configurazione config, JobDataMap map, String id, JobType jobType, FonteDati fd, SicurezzaFonteDati sfd) {
            map.put(ID, id);
            map.put(CONFIG + id, config);
            map.put(JOB_TYPE + id, jobType.getJobType());
            map.put(SQL_SCRIPT + id, config.getSqlScript());
            map.put(NOME, config.getNome());
            map.put(DRIVER_NAME + id, fd.getNomeDriver());
            map.put(URL + id, fd.getUrl());
            map.put(USERNAME + id, sfd.getUserID());
            map.put(PASSWORD + id, sfd.getPassword());
            map.put(THRESHOLDS + id, config.getSoglie());
        }

        public static void createSQLJobDataMap(Azione azione, JobDataMap map, String id, JobType jobType, FonteDati fd, SicurezzaFonteDati sfd) {
            map.put(ID, id);
            map.put(CONFIG + id, azione);
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
                debugID(azione.getStringID());
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

    public static <V> List<V> typeCheckAndReturn(Object toSanitize, Class<V> transformTo) {
        var sanitized = new ArrayList<V>();
        try {
            if(toSanitize instanceof List<?> l)
                for (Object o : l) {
                    if(transformTo.isInstance(o)) {
                        sanitized.add(transformTo.cast(o));
                    }
                }
        } catch (ClassCastException e) {
            log.error("Could not process type checking process, skipping. {}", e.getMessage(), e.getCause());
        }
        return sanitized;
    }

    //TODO check validity and usability of method or delete
    @SuppressWarnings("unused")
    public static void retrieveAzioni() {
        var acs = AzioneQueueCache.getCacheSize();
        if (acs > 0) {
            log.info("Retrieving actions..");
            AzioneQueueCache.getAll().forEach((k, v) -> {
                log.info("AzioneQueueCache: sogliaId: {}, azioni: {}", k, v.toArray());
                v.forEach(a -> log.debug("A. n. {}, action: {}", a.getSoglia().getId() , a.getDestinatario()));
            });
        }
    }

}
