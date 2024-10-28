package it.dmi.rest.endpoint.interfaces;

import it.dmi.structure.io.QueryResponse;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/query")
public interface QueryAPI {

    @Path("/activate")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Activate the system",
            description = "This endpoint allows you to submit a query to the system.")
    @APIResponse(responseCode = "200",
            description = "Query submitted successfully",
            content = @Content(schema = @Schema(implementation = QueryResponse.class)))
    @APIResponse(responseCode = "202", description = "System took charge of request")
    @APIResponse(responseCode = "204",
            description = "No content",
            content = @Content(schema = @Schema(implementation = QueryResponse.class)))
    @APIResponse(responseCode = "400",
            description = "Bad request",
            content = @Content(schema = @Schema(implementation = QueryResponse.class)))
    @APIResponse(responseCode = "500",
            description = "Internal server error",
            content = @Content(schema = @Schema(implementation = QueryResponse.class)))
    Response activate();
}
