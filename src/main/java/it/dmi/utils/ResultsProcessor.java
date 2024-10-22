package it.dmi.utils;

import it.dmi.caches.RestDataCache;
import it.dmi.data.api.service.ConfigurazioneService;
import it.dmi.data.api.service.OutputService;
import it.dmi.data.dto.OutputDTO;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.NoContentException;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@RequestScoped
public class ResultsProcessor {

    @Inject
    private OutputService outputService;

    @Inject
    private ConfigurazioneService configurazioneService;

    public Map<String, List<Object>> processSelectResultSet (ResultSet resultSet)
            throws SQLException, NullPointerException {

        Map<String, List<Object>> results = new HashMap<>();
        Objects.requireNonNull(resultSet, "ResultSet for select query was null.");
        log.info("Reading select result set...");
        int columnCount = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            for (int i = 1; i <= columnCount; i++) {
                String columnName = resultSet.getMetaData().getColumnName(i);
                Object columnValue = resultSet.getObject(i);
                if (results.containsKey(columnName)) {
                    results.get(columnName).add(columnValue);
                } else {
                    List<Object> list = new ArrayList<>();
                    list.add(columnValue);
                    results.put(columnName, list);
                }
            }
        }
        return results;
    }

    public Map<String, Integer> processCountResultSet(ResultSet set) {
        Map<String, Integer> results = new HashMap<>();
        Objects.requireNonNull(set, "ResultSet for count query was null.");
        log.info("Reading count result set..");
        try {
            if (set.next()) {
                int count = set.getInt(1);
                results.put("count", count);
            }
        } catch (SQLException e) {
            log.error("Failed to process count result set.", e);
        }
        return results;
    }

    public List<Map<String, List<?>>> processLatches() throws NoContentException {
        List<Map<String, List<?>>> results = new ArrayList<>();
        log.info("Retrieving latches from cache...");
        List<String> latches = RestDataCache.getLatches();
        if (latches.isEmpty()) {
            log.error("No latches found.");
            return null;
        }
        for (String id : latches) {
            log.debug("Retrieving data from latch {}...", id);
            Object fromDataCache = RestDataCache.get(id);
            Objects.requireNonNull(fromDataCache, "Output data cache is null.");
            if (fromDataCache instanceof List<?> list) {
                if (list.isEmpty()) {
                    log.error("Result from Configurazione with id: {} (Configurazione name: {}) were null.",
                            id,
                            configurazioneService.getByID(Long.valueOf(id)).getNome());
                    throw new NoContentException(String
                            .format("No content found for Configurazione id: %d.", Long.valueOf(id)));
                }
                log.debug("Data retrieved successfully.");
                results.add(Collections.singletonMap(id, list));
                log.debug("Creating output...");
                Collection<?> outputList = RestDataCache.get("output" + id);
                if (outputList instanceof List<?> outList && !outList.isEmpty()) {
                    if (outList.getFirst() instanceof OutputDTO outputDTO) {
                        outputService.create(outputDTO.toEntity());
                        log.info("Output from C. {} created.", outputDTO.getConfigurazioneId());
                    }
                } else {
                    log.error("Output not created.");
                }
            } else {
                log.error("Data type not readable.");
                results.add(Map.of(id, Collections.singletonList("Data type not readable.")));
            }
        }
        return results;
    }

}
