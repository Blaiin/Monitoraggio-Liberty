package it.dmi.quartz.jobs.sql.impl;

import it.dmi.caches.AzioneQueueCache;
import it.dmi.caches.JobDataCache;
import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import it.dmi.processors.ResultsProcessor;
import it.dmi.processors.thresholds.ThresHoldComparator;
import it.dmi.quartz.jobs.sql.BaseSQLJob;
import it.dmi.structure.internal.info.DBInfo;
import it.dmi.utils.jobs.OutputUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.ResultSet;
import java.util.List;

import static it.dmi.utils.constants.NamingConstants.*;

@Slf4j
public class SelectCountJob extends BaseSQLJob implements Job {

    @Override
    public void execute (JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String id = dataMap.getString(ID);
        QuartzTask task = (QuartzTask) dataMap.get(TASK + id);
        switch (task) {
            case Azione a -> executeAzioneCountQuery(id, a, dataMap);
            case Configurazione c -> executeConfigCountQuery(id, c, dataMap);
        }
    }

    private void executeAzioneCountQuery(String aID, Azione azione, JobDataMap dataMap) throws JobExecutionException {
        if(azione == null) {
            log.error("Azione cannot be null since job execution depends on it.");
            return;
        }
        log.info("Exec job -> Azione {}", aID);
        var dbInfo = DBInfo.build(dataMap);
        loadDriver(dbInfo);
        var output = OutputUtils.initializeOutputDTO(azione);
        try (ResultSet resultSet = queryDB(dbInfo)) {
            int result = ResultsProcessor.processCountResultSet(resultSet);
            if (result == 0) {
                log.error("(A) No data found.");
                return;
            }
            log.info("(A) Data found.");
            OutputUtils.finalizeOutputDTO(output, result);
            OutputUtils.cacheOutputDTO(aID, output);
            JobDataCache.countDown(aID + AZIONE);
        }  catch (Exception e) {
            resolveException(e);
        }
    }

    private void executeConfigCountQuery(String cID, Configurazione config, JobDataMap dataMap)
            throws JobExecutionException {
        if(config == null) {
            log.error("Config cannot be null since job execution depends on it.");
            return;
        }
        log.info("Exec job -> Config {}, name: {}.",
                cID, dataMap.getString(NOME));
        DBInfo dbInfo = DBInfo.build(dataMap);
        loadDriver(dbInfo);
        var output = OutputUtils.initializeOutputDTO(config);
        try (ResultSet resultSet = queryDB(dbInfo)) {
            int result = ResultsProcessor.processCountResultSet(resultSet);
            if (result == 0) {
                log.error("(C) No data found.");
                return;
            }
            log.info("(C) Data found.");
            OutputUtils.finalizeOutputDTO(output, result);
            OutputUtils.cacheOutputDTO(cID, output);
            if (config.getSoglie() == null || config.getSoglie().isEmpty())
                log.warn("No thresholds for Config {}.", config.getId());
            else {
                List<String> soglieIDs = ThresHoldComparator.compareCountTH(config, result);
                if (!soglieIDs.isEmpty()) {
                    log.info("NOT EMPTY SOGLIE IDS LIST (Config {}): {}", cID, soglieIDs);
                    AzioneQueueCache.put(SOGLIE + cID, soglieIDs);

                } else log.error("EMPTY SOGLIE IDS LIST (Config {})", cID);
            }
            JobDataCache.countDown(cID + CONFIG);
        }  catch (Exception e) {
            resolveException(e);
        }
    }
}
