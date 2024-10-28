package it.dmi.rest.endpoint;

import it.dmi.caches.AzioneQueueCache;
import it.dmi.quartz.ejb.ManagerEJB;
import it.dmi.rest.endpoint.interfaces.QueryAPI;
import it.dmi.structure.io.QueryResponse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@RequestScoped
@Slf4j
public class QueryAPIResource implements QueryAPI {

    private static int maxMessages = 1;

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
        try {
            managerEJB.scheduleJobsAsync();
            return Response.accepted().build();
        } catch (Exception e) {
            log.error("Could not process configurations: ", e);
            return Response.serverError().entity(new QueryResponse("Error",
                    "Internal server error")).build();
        } finally {
            var acs = AzioneQueueCache.getCacheSize();
            if (acs > 0) {
                log.info("Retrieving actions..");
                AzioneQueueCache.getAll().forEach((k, v) -> {
                    log.info("AzioneQueueCache: sogliaId: {}, azioni: {}", k, v.toArray());
                    v.forEach(a -> log.debug("A. n. {}, action: {}", a.getSoglia().getId() , a.getDestinatario()));
                });
            }
        }
    }
}
