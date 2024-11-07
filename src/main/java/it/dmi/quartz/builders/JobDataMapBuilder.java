package it.dmi.quartz.builders;

import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import it.dmi.structure.exceptions.impl.quartz.JobBuildingException;
import it.dmi.structure.internal.JobType;
import it.dmi.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;

@SuppressWarnings("LoggingSimilarMessage")
@Slf4j
public class JobDataMapBuilder {

    private static final boolean devMode = false;

    public static JobDataMap buildJobDataMap(QuartzTask task) throws JobBuildingException {
        return switch (task) {
            case Configurazione c -> buildJobDataMap(c);
            case Azione a -> buildJobDataMap(a);
        };
    }

    private static JobDataMap buildJobDataMap(Azione azione) throws JobBuildingException {
        var id = azione.getStrID();
        JobType jobType = Utils.Jobs.resolveJobType(azione);
        if(devMode)
            log.debug("Dev Mode is enabled, logging sensitive data.");
        switch (jobType) {
            case SQL -> {
                JobDataMap map = new JobDataMap();
                Utils.DebugLogger.debug(devMode, azione, jobType, azione.getFonteDati());
                Utils.Jobs.createSQLJobDataMap(azione, map, id, jobType, azione.getFonteDati(), azione.getUtenteFonteDati());
                return map;
            }
            case PROGRAM -> {
                JobDataMap map = new JobDataMap();
                Utils.DebugLogger.debug(devMode, azione, jobType);
                Utils.Jobs.createPROGRAMJobDataMap(azione, map, id);
                return map;
            }
            case CLASS -> {
                JobDataMap map = new JobDataMap();
                Utils.DebugLogger.debug(devMode, azione, jobType);
                Utils.Jobs.createCLASSEJobDataMap(azione, map, id);
                return map;
            }
            default -> {
                log.error("Configurazione n. {} has no valid fields set, skipping creation.", azione.getId());
                throw new IllegalArgumentException("Configurazione has no valid necessary fields set, configure exactly one" +
                        "between SQLScript, Programma or Classe.");
            }
        }
    }

    private static JobDataMap buildJobDataMap(Configurazione config) throws JobBuildingException {
        var id = config.getStrID();
        JobType jobType = Utils.Jobs.resolveJobType(config);
        if(devMode)
            log.debug("Dev Mode is enabled, logging sensitive data.");
        switch (jobType) {
            case SQL -> {
                Utils.DebugLogger.debug(devMode, config, jobType, config.getFonteDati());
                return Utils.Jobs.populateSQLJobDataMap(config, new JobDataMap(), jobType,
                        config.getFonteDati(), config.getUtenteFonteDati());
            }
            case PROGRAM -> {
                Utils.DebugLogger.debug(devMode, config, jobType);
                return Utils.Jobs.createPROGRAMJobDataMap(config, new JobDataMap(), id);
            }
            case CLASS -> {
                Utils.DebugLogger.debug(devMode, config, jobType);
                return Utils.Jobs.createCLASSEJobDataMap(config, new JobDataMap(), id);
            }
            default -> {
                log.error("Configurazione n. {} has no valid fields set, skipping creation.", id);
                throw new IllegalArgumentException(String.format("Configurazione %s has no valid necessary fields set, " +
                        "configure exactly one" +
                        "between SQLScript, Programma or Classe.", id));
            }
        }
    }
}
