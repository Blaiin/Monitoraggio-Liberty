package it.dmi.data.dto;

import it.dmi.data.dto.idto.IDTO;
import it.dmi.data.entities.*;
import it.dmi.data.entities.task.Configurazione;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurazioneDTO implements IDTO<ConfigurazioneDTO, Configurazione> {

    private Long id;
    private String nome;
    private String sqlScript;
    private String programma;
    private String classe;
    private String schedulazione;
    private Controllo controllo;
    private TipoControllo tipoControllo;
    private Ambito ambito;
    private FonteDati fonteDati;
    private SicurezzaFonteDati utenteFonteDati;
    private int ordineConfigurazione;

    public ConfigurazioneDTO(String nome, String sqlScript, String programma, String classe,
                             String schedulazione, int ordineConfigurazione) {
        this.nome = nome;
        this.sqlScript = sqlScript;
        this.programma = programma;
        this.classe = classe;
        this.schedulazione = schedulazione;
        this.ordineConfigurazione = ordineConfigurazione;
    }

    @Override
    public ConfigurazioneDTO fromEntity(Configurazione c) {
        if (c != null) {
            this.id = c.getId();
            this.nome = c.getNome();
            this.sqlScript = c.getSqlScript();
            this.programma = c.getProgramma();
            this.classe = c.getClasse();
            this.schedulazione = c.getSchedulazione();
            this.controllo = c.getControllo();
            this.tipoControllo = c.getTipoControllo();
            this.ambito = c.getAmbito();
            this.fonteDati = c.getFonteDati();
            this.utenteFonteDati = c.getUtenteFonteDati();
            this.ordineConfigurazione = c.getOrdineConfigurazione();
        } return null;
    }

    @Override
    public Configurazione toEntity() {
        return new Configurazione(
                this.id,
                this.nome,
                this.sqlScript,
                this.programma,
                this.classe,
                this.schedulazione,
                this.controllo,
                this.tipoControllo,
                this.ambito,
                this.fonteDati,
                this.utenteFonteDati,
                this.ordineConfigurazione
        );
    }
}
