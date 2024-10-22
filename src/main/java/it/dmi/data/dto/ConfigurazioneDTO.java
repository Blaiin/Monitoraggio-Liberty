package it.dmi.data.dto;

import it.dmi.data.dto.idto.IDTO;
import it.dmi.data.entities.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfigurazioneDTO implements IDTO {

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

    @Override
    public <T> IDTO fromEntity (T entity) {
        return entity instanceof Configurazione c ? new ConfigurazioneDTO(
                c.getId(),
                c.getNome(),
                c.getSqlScript(),
                c.getProgramma(),
                c.getClasse(),
                c.getSchedulazione(),
                c.getControllo(),
                c.getTipoControllo(),
                c.getAmbito(),
                c.getFonteDati(),
                c.getUtenteFonteDati(),
                c.getOrdineConfigurazione()
        ) : null;
    }

    @Override
    public Configurazione toEntity () {
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
