package it.dmi.structure.data.dto;

import it.dmi.structure.data.dto.idto.IDTO;
import it.dmi.structure.data.entities.*;
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

    @Override
    public <T> IDTO fromEntity (T entity) {
        return entity instanceof Configurazione ? new ConfigurazioneDTO(
                ((Configurazione) entity).getId(),
                ((Configurazione) entity).getNome(),
                ((Configurazione) entity).getSqlScript(),
                ((Configurazione) entity).getProgramma(),
                ((Configurazione) entity).getClasse(),
                ((Configurazione) entity).getSchedulazione(),
                ((Configurazione) entity).getControllo(),
                ((Configurazione) entity).getTipoControllo(),
                ((Configurazione) entity).getAmbito(),
                ((Configurazione) entity).getFonteDati(),
                ((Configurazione) entity).getUtenteFonteDati()
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
                this.utenteFonteDati
        );
    }
}
