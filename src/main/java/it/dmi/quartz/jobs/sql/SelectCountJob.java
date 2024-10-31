package it.dmi.quartz.jobs.sql;

import it.dmi.caches.JobDataCache;
import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.data.entities.task.QuartzTask;
import it.dmi.processors.ResultsProcessor;
import it.dmi.processors.thresholds.ThresHoldComparator;
import it.dmi.structure.internal.info.DBInfo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.ResultSet;

import static it.dmi.utils.constants.NamingConstants.*;

@Slf4j
public class SelectCountJob implements ISQLJob {

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

    private void executeAzioneCountQuery(String id, Azione azione, JobDataMap dataMap) throws JobExecutionException {
        if(azione == null) {
            log.error("Azione cannot be null since job execution depends on it.");
            return;
        }
        log.info("Exec job -> Azione {}", id);
        DBInfo dbInfo = buildDBInfo(dataMap);
        loadDriver(dbInfo);
        var output = initializeOutputDTO(azione);
        try (ResultSet resultSet = queryDB(dbInfo)) {
            int result = ResultsProcessor.processCountResultSet(resultSet);
            if (result == 0) {
                log.error("(A) No data found.");
                return;
            }
            log.info("(A) Data found.");
            output = finalizeOutputDTO(output, result);
            cacheOutputDTO(id, output);
        }  catch (Exception e) {
            resolveException(e);
        } finally {
            JobDataCache.countDown(id + AZIONE);
        }
    }

    private void executeConfigCountQuery(String id, Configurazione config, JobDataMap dataMap) throws JobExecutionException {
        if(config == null) {
            log.error("Config cannot be null since job execution depends on it.");
            return;
        }
        log.info("Exec job -> Config {}, name: {}.",
                id, dataMap.getString(NOME));
        DBInfo dbInfo = buildDBInfo(dataMap);
        loadDriver(dbInfo);
        var output = initializeOutputDTO(config);
        try (ResultSet resultSet = queryDB(dbInfo)) {
            int result = ResultsProcessor.processCountResultSet(resultSet);
            if (result == 0) {
                log.error("(C) No data found.");
                return;
            }
            log.info("(C) Data found.");
            output = finalizeOutputDTO(output, result);
            cacheOutputDTO(id, output);
            if (config.getSoglie().isEmpty()) {
                log.warn("No thresholds for Config {}.", config.getId());
            } else {
                dataMap.put(SOGLIE + id, ThresHoldComparator.compareCountThresholds(config, result));
            }
        }  catch (Exception e) {
            resolveException(e);
        } finally {
            JobDataCache.countDown(id + CONFIG);
        }
    }
}
