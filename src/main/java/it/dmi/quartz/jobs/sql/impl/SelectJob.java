package it.dmi.quartz.jobs.sql.impl;

import it.dmi.caches.JobDataCache;
import it.dmi.data.entities.task.Configurazione;
import it.dmi.processors.ResultsProcessor;
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
import java.util.Map;

import static it.dmi.utils.constants.NamingConstants.*;

@Slf4j
public class SelectJob extends BaseSQLJob implements Job {

    @Override
    public void execute (JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String id = dataMap.getString(ID);
        Configurazione config = (Configurazione) dataMap.get(CONFIG + id);
        log.info("Trying to execute job for Configurazione id: {}, name: {}.",
                id, dataMap.getString(NOME));
        DBInfo dbInfo = DBInfo.build(dataMap);
        loadDriver(dbInfo);
        var output = OutputUtils.initializeOutputDTO(config);
        try (ResultSet resultSet = queryDB(dbInfo)) {
            Map<String, List<Object>> results = ResultsProcessor.processSelectResultSet(resultSet);
            if (!results.isEmpty()) {
                log.info("Data found.");
                OutputUtils.finalizeOutputDTO(output, results);
                OutputUtils.cacheOutputDTO(id, output);
            } else {
                log.error("No data found.");
            }
        } catch (Exception e) {
            resolveException(e);
        } finally {
            JobDataCache.countDown(id);
        }
    }
}
