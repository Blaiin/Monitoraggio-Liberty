package it.dmi.rest.endpoint.interfaces;

import it.dmi.structure.io.RamUsageResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/stats")
public interface StatisticsAPI {

    @Path("/ram-usage")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Activate the system",
            description = "This endpoint allows you to submit a query to the system.")
    @APIResponse(responseCode = "200",
            description = "Succesfully returned statistics",
            content = @Content(schema = @Schema(implementation = RamUsageResponse.class)))
    @APIResponse(responseCode = "204",
            description = "No content")
    @APIResponse(responseCode = "400",
            description = "Bad request")
    @APIResponse(responseCode = "500",
            description = "Internal server error")
    RamUsageResponse ramUsage();

}
