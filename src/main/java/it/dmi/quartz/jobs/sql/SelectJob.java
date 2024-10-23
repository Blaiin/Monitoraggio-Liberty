package it.dmi.quartz.jobs.sql;

import it.dmi.caches.RestDataCache;
import it.dmi.data.entities.Configurazione;
import it.dmi.processors.ResultsProcessor;
import it.dmi.structure.internal.info.DBInfo;
import it.dmi.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static it.dmi.structure.internal.Esito.POSITIVE;
import static it.dmi.utils.constants.NamingConstants.*;

@Slf4j
public class SelectJob implements ISQLJob {

    private final ResultsProcessor resultsProcessor = new ResultsProcessor();

    @Override
    public void execute (JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String id = dataMap.getString(ID);
        Configurazione config = (Configurazione) dataMap.get(CONFIG + id);
        log.info("Trying to execute job for Configurazione id: {}, name: {}.",
                id, dataMap.getString(NOME));
        DBInfo dbInfo = buildDBInfo(dataMap);
        loadDriver(dbInfo);
        var inizio = TimeUtils.now();
        var output = initializeOutputDTO(id, inizio);
        try (ResultSet resultSet = queryDB(dbInfo)) {
            Map<String, List<Object>> results = resultsProcessor.processSelectResultSet(resultSet);
            if (!results.isEmpty()) {
                log.info("Data found.");
                var fine = TimeUtils.now();
                output.setEsito(POSITIVE.getValue());
                output.setContenuto(results);
                output.setFine(Timestamp.valueOf(fine));
                output.setDurata(TimeUtils.duration(inizio, fine));
                RestDataCache.put(id, results);
                RestDataCache.put(OUTPUT + id, Collections.singletonList(output));
            } else {
                log.error("No data found.");
            }
        } catch (Exception e) {
            resolveException(e);
        }
    }
}
