package it.dmi.quartz.builders;

import it.dmi.data.entities.FonteDati;
import it.dmi.data.entities.SicurezzaFonteDati;
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
        var id = azione.getStringID();
        JobType jobType = Utils.JobHelper.resolveJobType(azione);
        FonteDati fd = azione.getFonteDati();
        SicurezzaFonteDati sfd = azione.getUtenteFonteDati();
        if(devMode)
            log.debug("Dev Mode is enabled, logging sensitive data.");
        switch (jobType) {
            case SQL -> {
                JobDataMap map = new JobDataMap();
                Utils.DebugLogger.debug(devMode, azione, jobType, fd);
                Utils.JobHelper.createSQLJobDataMap(azione, map, id, jobType, fd, sfd);
                return map;
            }
            case PROGRAM -> {
                JobDataMap map = new JobDataMap();
                Utils.DebugLogger.debug(devMode, azione, jobType);
                Utils.JobHelper.createPROGRAMJobDataMap(azione, map, id);
                return map;
            }
            case CLASS -> {
                JobDataMap map = new JobDataMap();
                Utils.DebugLogger.debug(devMode, azione, jobType);
                Utils.JobHelper.createCLASSEJobDataMap(azione, map, id);
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
        var id = config.getStringID();
        JobType jobType = Utils.JobHelper.resolveJobType(config);
        FonteDati fd = config.getFonteDati();
        SicurezzaFonteDati sfd = config.getUtenteFonteDati();
        if(devMode)
            log.debug("Dev Mode is enabled, logging sensitive data.");
        switch (jobType) {
            case SQL -> {
                JobDataMap map = new JobDataMap();
                Utils.DebugLogger.debug(devMode, config, jobType, fd);
                Utils.JobHelper.createSQLJobDataMap(config, map, id, jobType, fd, sfd);
                return map;
            }
            case PROGRAM -> {
                JobDataMap map = new JobDataMap();
                Utils.DebugLogger.debug(devMode, config, jobType);
                Utils.JobHelper.createPROGRAMJobDataMap(config, map, id);
                return map;
            }
            case CLASS -> {
                JobDataMap map = new JobDataMap();
                Utils.DebugLogger.debug(devMode, config, jobType);
                Utils.JobHelper.createCLASSEJobDataMap(config, map, id);
                return map;
            }
            default -> {
                log.error("Configurazione n. {} has no valid fields set, skipping creation.", config.getId());
                throw new IllegalArgumentException("Configurazione has no valid necessary fields set, configure exactly one" +
                        "between SQLScript, Programma or Classe.");
            }
        }
    }
}
