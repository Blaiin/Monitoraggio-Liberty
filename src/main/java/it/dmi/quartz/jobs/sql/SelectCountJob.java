package it.dmi.quartz.jobs.sql;

import it.dmi.caches.JobDataCache;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.processors.ResultsProcessor;
import it.dmi.processors.thresholds.ThresHoldComparator;
import it.dmi.structure.internal.info.DBInfo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.ResultSet;
import java.util.Map;

import static it.dmi.utils.constants.NamingConstants.*;

@Slf4j
public class SelectCountJob implements ISQLJob {

    private final ThresHoldComparator comparator = new ThresHoldComparator();

    @Override
    public void execute (JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String id = dataMap.getString(ID); Configurazione config = (Configurazione) dataMap.get(CONFIG + id);
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
            Map<String, Integer> results = ResultsProcessor.processCountResultSet(resultSet);
            if (results.isEmpty()) {
                log.error("No data found.");
                return;
            }
            log.info("Data found.");
            output = finalizeOutputDTO(output, results);
            cacheOutputDTO(id, output);
            if (config.getSoglie().isEmpty()) {
                log.warn("No thresholds for Config {}.", config.getId());
            } else {
                dataMap.put(SOGLIE + id, comparator.compareCountThresholds(config, results));
            }
        }  catch (Exception e) {
            resolveException(e);
        } finally {
            JobDataCache.countDown(id);
        }
    }
}
