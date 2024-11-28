package it.dmi.rest.endpoint.apis;

import it.dmi.rest.io.QueryResponse;
import it.dmi.rest.io.input.ConfigurazioneInsertRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/app")
public interface QueryAPI {

    @Path("/config/insert")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Insert Configurazione",
            description = "This endpoint allows to insert a new Configurazione into the system")
    @RequestBody(
            description = "Mandatory data for Configurazione insert",
            required = true,
            content = @Content(schema = @Schema(implementation = ConfigurazioneInsertRequest.class)))
    @APIResponse(responseCode = "200", description = "Configurazione created successfully")
    @APIResponse(responseCode = "400", description = "Invalid input provided")
    Response insertConfig(ConfigurazioneInsertRequest request);

    @Path("/activate")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Activate the system",
            description = "This endpoint allows to submit a query to the system.")
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
