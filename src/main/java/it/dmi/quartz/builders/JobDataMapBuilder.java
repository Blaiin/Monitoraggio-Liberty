package it.dmi.quartz.builders;

import it.dmi.data.api.service.FonteDatiService;
import it.dmi.data.api.service.SicurezzaFonteDatiService;
import it.dmi.data.entities.FonteDati;
import it.dmi.data.entities.SicurezzaFonteDati;
import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import it.dmi.structure.exceptions.impl.quartz.JobBuildingException;
import it.dmi.structure.internal.JobType;
import jakarta.ejb.DependsOn;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;

import static it.dmi.utils.constants.NamingConstants.*;

@SuppressWarnings("LoggingSimilarMessage")
@Slf4j
@RequestScoped
@DependsOn({"FonteDatiService", "SicurezzaFonteDatiService"})
public class JobDataMapBuilder {

    @Inject
    private FonteDatiService fonteDatiService;

    @Inject
    private SicurezzaFonteDatiService sicurezzaFonteDatiService;

    private static final boolean devMode = false;

    public JobDataMap buildJobDataMap(QuartzTask task) throws JobBuildingException {
        return switch (task) {
            case Configurazione c -> buildJobDataMap(c);
            case Azione a -> /*buildJobDataMap(a);*/ null;
        };
    }

    public JobDataMap buildJobDataMap(Configurazione config) throws JobBuildingException {
        String id = String.valueOf(config.getId());
        boolean sql = config.getSqlScript() != null;
        boolean program = config.getProgramma() != null;
        boolean clasz = config.getClasse() != null;
        if((sql && program) || (sql && clasz) || (program && clasz)) {
            log.error("Configurazione n. {} has multiple conflicting fields set, skipping creation.", config.getId());
            throw new JobBuildingException("Configurazione has multiple conflicting fields set, only one allowed.");
        }
        if(devMode)
            log.debug("Dev Mode is enabled, logging sensitive data.");
        if (sql) {
            FonteDati fonteDati = fonteDatiService.getByID(config.getFonteDati().getId());
            SicurezzaFonteDati sicurezzaFonteDati = sicurezzaFonteDatiService.getByID(config.getUtenteFonteDati().getId());
            JobDataMap map = new JobDataMap();
            if(devMode) {
                log.debug("JobType detected: {}", JobType.SQL.getJobType());
                log.debug("SQL Script: {}", config.getSqlScript());
                log.debug("Nome: {}", config.getNome());
                log.debug("Driver name: {}", fonteDati.getNomeDriver());
                log.debug("URL: {}", fonteDati.getUrl());
                log.debug("Username: {}", sicurezzaFonteDati.getUserID());
                log.debug("Password: {}", sicurezzaFonteDati.getPassword());
            }
            map.put(ID, id);
            map.put(CONFIG + id, config);
            map.put(JOB_TYPE + id, JobType.SQL.getJobType());
            map.put(SQL_SCRIPT + id, config.getSqlScript());
            map.put(NOME, config.getNome());
            map.put(DRIVER_NAME + id, fonteDati.getNomeDriver());
            map.put(URL + id, fonteDati.getUrl());
            map.put(USERNAME + id, sicurezzaFonteDati.getUserID());
            map.put(PASSWORD + id, sicurezzaFonteDati.getPassword());
            map.put(THRESHOLDS + id, config.getSoglie());
            return map;
        }
        if(program) {
            JobDataMap map = new JobDataMap();
            if(devMode) {
                log.debug("JobType detected: {}", JobType.PROGRAM.getJobType());
                log.debug("Programma: {}", config.getProgramma());
                log.debug("Nome: {}", config.getNome());
            }
            map.put(ID, id);
            map.put(CONFIG + id, config);
            map.put(JOB_TYPE + id, JobType.PROGRAM.getJobType());
            map.put(NOME, config.getNome());
            map.put(PROGRAMMA + id, config.getProgramma());
            map.put(THRESHOLDS + id, config.getSoglie());
            return map;
        }
        if(clasz) {
            JobDataMap map = new JobDataMap();
            if(devMode) {
                log.debug("JobType detected: {}", JobType.CLASS.getJobType());
                log.debug("Classe: {}", config.getClasse());
                log.debug("Nome: {}", config.getNome());
            }
            map.put(ID, id);
            map.put(CONFIG + id, config);
            map.put(JOB_TYPE + id, JobType.CLASS.getJobType());
            map.put(NOME, config.getNome());
            map.put(CLASS + id, config.getClasseSimpleName());
            map.put(THRESHOLDS + id, config.getSoglie());
            return map;
        }
        log.error("Configurazione n. {} has no valid fields set, skipping creation.", config.getId());
        throw new IllegalArgumentException("Configurazione has no valid necessary fields set, configure exactly one" +
                "between SQLScript, Programma or Classe.");
    }

}
