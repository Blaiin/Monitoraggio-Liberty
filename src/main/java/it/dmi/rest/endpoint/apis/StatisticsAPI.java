package it.dmi.rest.endpoint.apis;

import it.dmi.rest.io.RamUsageResponse;
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
    @Operation(summary = "Ram usage of system",
            description = "Measure ram statistics")
    @APIResponse(responseCode = "200",
            description = "Succesfully returned statistics",
            content = @Content(schema = @Schema(implementation = RamUsageResponse.class)))
    @APIResponse(responseCode = "500",
            description = "Internal server error")
    RamUsageResponse ramUsage();

}
