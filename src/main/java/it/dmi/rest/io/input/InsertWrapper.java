package it.dmi.rest.io.input;

import it.dmi.data.dto.*;
import org.eclipse.microprofile.openapi.annotations.ExternalDocumentation;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Schema(hidden = true)
public class InsertWrapper {

    @Schema(name = "TipoControllo")
    public record TipoControlloInsert (
            @Schema(example = "Tipo controllo sistemistico interno")
            String descrizione) implements IRequest<TipoControlloDTO> {

        @Contract(value = " -> new", pure = true)
        @Override
        public @NotNull TipoControlloDTO toDTO() {
            return new TipoControlloDTO(this.descrizione);
        }
    }

    @Schema(name = "Ambito")
    public record AmbitoInsert(

            @Schema(example = "Statistica")
            String nome,

            @Schema(example = "Ambito statistico sistema interno")
            String destinazione) implements IRequest<AmbitoDTO> {

        @Contract(value = " -> new", pure = true)
        @Override
        public @NotNull AmbitoDTO toDTO() {
            return new AmbitoDTO(this.nome, this.destinazione);
        }
    }

    @Schema(name = "Controllo")
    public record ControlloInsert(
            @Schema(example = "Controllo sistemistico interno ala statistica")
            String descrizione,

            @Schema(name = "TipoControlloIdentificativo", example = "9")
            Long tipoControlloID,

            @Schema(name = "AmbitoIdentificativo", example = "11")
            Long ambitoID,

            @Schema(name = "ordine", description = "Order of Controllo which they will be executed by", example = "3")
            int ordineControllo) implements IRequest<ControlloDTO> {

        @Contract(value = " -> new", pure = true)
        @Override
        public @NotNull ControlloDTO toDTO() {
            return new ControlloDTO(this.descrizione, this.tipoControlloID, this.ambitoID, this.ordineControllo);
        }
    }

    @Schema(name = "FonteDati")
    public record FonteDatiInsert(

            @Schema(example = "Coordinates to access target Postgres Database")
            String descrizione,
            @Schema(name = "NomeDriver", example = "org.postgresql.Driver")
            String nomeDriver,
            @Schema(name = "NomeClasse")
            String nomeClasse,
            @Schema(name = "DatabaseURL", example = "jdbc:postgresql://{host}:{port}/{database}")
            String url,
            @Schema(name = "JNDIName", example = "java:/jdbc/{datasourceName}",
                    externalDocs = @ExternalDocumentation(
                        description = "Tomcat JNDI Configuration Guide",
                        url = "https://tomcat.apache.org/tomcat-9.0-doc/jndi-resources-howto.html"
            ))
            String jndiName) implements IRequest<FonteDatiDTO> {

        @Contract(value = " -> new", pure = true)
        @Override
        public @NotNull FonteDatiDTO toDTO() {
            return new FonteDatiDTO(this.descrizione, this.nomeDriver, this.nomeClasse, this.url, this.jndiName);
        }
    }

    @Schema(name = "UtenteFonteDati")
    public record UtenteFonteDatiInsert(

            @Schema(example = "Fonte Dati to access specific database for internal logs")
            String descrizione,

            @Schema(name = "Username", example = "oracleUser")
            String username,

            @Schema(name = "Password", example = "oraclePass")
            String password) implements IRequest<SicurezzaFonteDatiDTO> {

        @Contract(value = " -> new", pure = true)
        @Override
        public @NotNull SicurezzaFonteDatiDTO toDTO() {
            return new SicurezzaFonteDatiDTO(this.descrizione, this.username, this.password);
        }
    }

    @Schema(name = "Configurazione")
    public record ConfigurazioneInsert(

            @Schema(description = "Name of Configurazione", example = "Count faulty data")
            String nome,

            @Schema(name = "ConfigurazioneScript", description = "Script to run on target database",
                    example = "SELECT user.name, user.email FROM users")
            String sqlScript,

            @Schema(name = "ConfigurazioneProgramma", description = "Programma to run")
            String programma,

            @Schema(name = "ConfigurazioneClasse", description = "Classe to run")
            String classe,

            @Schema(description = "Scheduled time at which Configurazione will be executed",
            example = "0/30 * * * * ? -> every 30 seconds of every day")
            String schedulazione,

            @Schema(name = "ordine", description = "Order by which Configurazioni will be executed by", example = "1")
            int ordineConfigurazione) implements IRequest<ConfigurazioneDTO> {

        @Contract(value = " -> new", pure = true)
        @Override
        public @NotNull ConfigurazioneDTO toDTO() {
            return new ConfigurazioneDTO(this.nome, this.sqlScript, this.programma, this.classe,
                    this.schedulazione, this.ordineConfigurazione);
        }
    }

    @Schema(name = "Soglia")
    public record SogliaInsert(

            @Schema(name = "SogliaInferiore", description = "Lowest threshold to compare", example = "0.5")
            double sogliaInferiore,

            @Schema(name = "SogliaSuperiore", description = "Upper threshold to compare", example = "11.7")
            double sogliaSuperiore,

            @Schema(description = "Single value string to directly compare with Configurazione output", example = "Mario")
            String valore,

            @Schema(description = "To be used in conjunction with Valore, applies to single value compare operations", example = "<>")
            String operatore) implements IRequest<SogliaDTO> {

        @Contract(" -> new")
        @Override
        public @NotNull SogliaDTO toDTO() {
            return new SogliaDTO(this.sogliaInferiore, this.sogliaSuperiore, this.valore, this.operatore);
        }
    }
}
