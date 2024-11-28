package it.dmi.rest.endpoint;

import it.dmi.data.api.service.*;
import it.dmi.quartz.ejb.Manager;
import it.dmi.rest.endpoint.apis.QueryAPI;
import it.dmi.rest.io.QueryResponse;
import it.dmi.rest.io.input.ConfigurazioneInsertRequest;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.StructuredTaskScope;

@RequestScoped
@Slf4j
public class QueryAPIResource implements QueryAPI {

    @Inject private Manager manager; @Inject private TipoControlloService tcService;
    @Inject private ControlloService cService; @Inject private AmbitoService aService;
    @Inject private FonteDatiService fdService; @Inject private SicurezzaFonteDatiService utenteFDService;
    @Inject private ConfigurazioneService configService; @Inject private SogliaService sService;

    //TODO finish logic and insert flow
    @Override
    public Response insertConfig(@NotNull ConfigurazioneInsertRequest request) {
        var tipoControlloRequest = request.getTipoControllo().toDTO();
        var ambitoRequest = request.getAmbito().toDTO();
        var controllo = request.getControllo();
        var fonteDati = request.getFonteDati();
        var utente = request.getUtenteFonteDati();
        var configurazione = request.getConfigurazione();
        var soglie = request.getSoglie();

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var tipoControlloFuture = scope.fork(() -> tcService.createOrFind(tipoControlloRequest));
            var ambitoFuture = scope.fork(() -> aService.createOrFind(ambitoRequest));

        }

        if(request.isRequestValid())
            if (configService.create(request.toEntity())) return Response.ok().build();
        return Response.serverError().build();
    }

    @Override
    public Response activate() {
        try {
            manager.scheduleConfigs();
            return Response.accepted().build();
        } catch (Exception e) {
            log.error("Could not process configurations: ", e);
            return Response.serverError().entity(new QueryResponse("Error",
                    "Internal server error")).build();
        }
    }

    private static int maxMessages = 1;

    public QueryAPIResource() {
        if (maxMessages == 1) {
            log.debug("QueryAPIResource initialized.");
            maxMessages++;
        }
    }
}
