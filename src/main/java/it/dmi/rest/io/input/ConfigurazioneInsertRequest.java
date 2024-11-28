package it.dmi.rest.io.input;

import it.dmi.data.entities.task.Configurazione;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.util.List;

@Getter
@Schema(title = "Configurazione Insert Request Schema",
        name = "ConfigurazioneRequest",
        description = "Schema representing a request to the system to insert a new Configurazione to be executed by the latter")
public class ConfigurazioneInsertRequest {

    @NotNull(message = "Tipo Controllo cannot be null")
    @Schema(name = "Tipo Controllo", description = "Represents a group identifying different Configurazione")
    private InsertWrapper.TipoControlloInsert tipoControllo;

    @NotNull(message = "Controllo descrizione cannot be null")
    @Schema(name = "Controllo", description = "Represents a subgroup identifying different Configurazione")
    private InsertWrapper.ControlloInsert controllo;

    @NotNull(message = "Ambito nome cannot be null")
    @Schema(name = "Ambito", description = "Represents a domain identifying different Configurazione")
    private InsertWrapper.AmbitoInsert ambito;

    @NotNull(message = "Fonte Dati cannot be null")
    @Schema(name = "Fonte Dati", description = "Target Database configuration to execute Configurazione on")
    private InsertWrapper.FonteDatiInsert fonteDati;

    @NotNull(message = "Utente Fonte Dati cannot be null")
    @Schema(name = "Utente Fonte Dati", description = "Credentials to use with Fonte Dati")
    private InsertWrapper.UtenteFonteDatiInsert utenteFonteDati;

    @NotNull(message = "Configurazione cannot be null")
    @Schema(name = "Configurazione", description = "Core of application, contains mandatory information about execution of queries")
    private InsertWrapper.ConfigurazioneInsert configurazione;

    @Schema(name = "Soglie", description = "Soglie to be compared with Configurazione output")
    private List<InsertWrapper.SogliaInsert> soglie;

    @Schema(hidden = true)
    public boolean isRequestValid() {
        return false;
    }

    public Configurazione toEntity() {
        return null;
    }
}
