package it.dmi.rest.endpoint;

import it.dmi.caches.AzioneQueueCache;
import it.dmi.processors.ResultsProcessor;
import it.dmi.quartz.ejb.ManagerEJB;
import it.dmi.rest.endpoint.interfaces.QueryAPI;
import it.dmi.structure.io.QueryResponse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@RequestScoped
@Slf4j
public class QueryAPIResource implements QueryAPI {

    private static int maxMessages = 1;

    @Inject
    private ResultsProcessor resultsProcessor;

    public QueryAPIResource() {
        if (maxMessages == 1) {
            log.debug("QueryAPIResource initialized.");
            maxMessages++;
        }
    }

    @Inject
    private ManagerEJB managerEJB;

    @Override
    public Response activate() {
        List<Map<String, List<?>>> results;
        try {
            managerEJB.scheduleJobsAsync();
            results = resultsProcessor.processLatches();
            return !results.isEmpty() ?
                    Response.ok().entity(new QueryResponse("Success",
                            "Results populated", results)).build() :
                    Response.noContent().build();
        } catch (Exception e) {
            log.error("Could not process configurations: ", e);
            return Response.serverError().entity(new QueryResponse("Error",
                    "Internal server error")).build();
        } finally {
            log.info("Retrieving actions..");
            log.info("AzioneQueueCache size: {}", AzioneQueueCache.getCacheSize());
            AzioneQueueCache.getAll().forEach((k, v) -> {
                log.info("AzioneQueueCache: sogliaId: {}, azioni: {}", k, v.toArray());
                v.forEach(a -> log.debug("A. n. {}, action: {}", a.getSoglia().getId() , a.getDestinatario()));
            });
        }
    }
}
