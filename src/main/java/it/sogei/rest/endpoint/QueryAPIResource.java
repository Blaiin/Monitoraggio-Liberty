package it.sogei.rest.endpoint;

import it.sogei.data_access.shared.RestDataCache;
import it.sogei.quartz.ejb.JobManagerEJB;
import it.sogei.quartz.ejb.ManagerEJB;
import it.sogei.rest.endpoint.interfaces.QueryAPI;
import it.sogei.structure.apimodels.QueryRequest;
import it.sogei.structure.apimodels.QueryResponse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RequestScoped
@Slf4j
public class QueryAPIResource implements QueryAPI {

    public QueryAPIResource() {
        log.info("QueryAPIResource initialized.");
    }
    @Inject
    private JobManagerEJB jobManagerEJB;
    @Inject
    private ManagerEJB managerEJB;

    @Override
    public Response submitQuery(QueryRequest queryRequest) {
        if (queryRequest.query() != null) {
            List<List<?>> results = new ArrayList<>();
            try {
                managerEJB.scheduleQueryJob(queryRequest);
                log.info("Retrieving latches from cache...");
                List<String> latches = RestDataCache.getLatches();
                for (String id : latches) {
                    log.info("Retrieving data from latch {}...", id);
                    Collection<?> fromDataCache = RestDataCache.get(id);
                    switch (fromDataCache) {
                        case List<?> list when !list.isEmpty() -> {
                            log.info("Data retrieved successfully.");
                            results.add(list);
                        }
                        case List<?> _ -> {
                            log.error("No data found.");
                            results.add(Collections.singletonList("No data found."));
                        }
                        case null -> {
                            log.error("No data found.");
                            results.add(Collections.singletonList("No data found."));
                        }
                        default -> {
                            log.error("Data type not readable.");
                            results.add(Collections.singletonList("Data type not readable."));
                        }
                    }
                }
                return Response.ok().entity(new QueryResponse("200", "Success", results)).build();
            } catch (Exception e) {
                log.error("Failed to submit query", e);
                return Response.serverError().entity(new QueryResponse("500", "Internal server error", results)).build();
            }
        }
        return Response.serverError().build();
    }

    @Override
    public Response activate() {
            List<List<?>> results = new ArrayList<>();
            try {
                managerEJB.scheduleJobs();
                log.info("Retrieving latches from cache...");
                List<String> latches = RestDataCache.getLatches();
                for (String id : latches) {
                    log.info("Retrieving data from latch {}...", id);
                    Object fromDataCache = RestDataCache.get(id);
                    switch (fromDataCache) {
                        case List<?> list when !list.isEmpty() -> {
                            log.info("Data retrieved successfully.");
                            results.add(list);
                        }
                        case List<?> _ -> {
                            log.error("No data found.");
                            results.add(Collections.singletonList("No data found."));
                        }
                        case null -> {
                            log.error("No data found.");
                            results.add(Collections.singletonList("No data found."));
                        }
                        default -> {
                            log.error("Data type not readable.");
                            results.add(Collections.singletonList("Data type not readable."));
                        }
                    }
                }
                return !results.isEmpty() ?
                        Response.ok().entity(new QueryResponse("200", "Results populated", results)).build() :
                        Response.noContent().build();
            } catch (Exception e) {
                log.error("Failed to submit query", e);
                return Response.serverError().entity(new QueryResponse("500",
                        "Internal server error",
                        results)).build();
            }
    }

    @Override
    public Response submitQueryTest(QueryRequest queryRequest) {
        if (queryRequest.query() != null) {
            List<List<?>> results = new ArrayList<>();
            try {
                jobManagerEJB.scheduleJobs();

                return Response.ok().entity(new QueryResponse("200", "Success", results)).build();
            } catch (Exception e) {
                log.error("Failed to submit query", e);
                return Response.serverError().entity(new QueryResponse("500", "Internal server error", results)).build();
            }
        }
        return Response.serverError().build();
    }
}
