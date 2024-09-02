package it.sogei.structure.data.dto;

import it.sogei.structure.data.dto.idto.IDTO;
import it.sogei.structure.data.entities.Configurazione;
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
    private Long controlloID;
    private Long tipoControlloID;
    private Long ambitoID;
    private Long fonteDatiID;
    private Long utenteFonteDatiID;

    @Override
    public <T> IDTO fromEntity (T entity) {
        return entity instanceof Configurazione config ? new ConfigurazioneDTO(
                config.getId(),
                config.getNome(),
                config.getSqlScript(),
                config.getProgramma(),
                config.getClasse(),
                config.getSchedulazione(),
                config.getControlloID(),
                config.getTipoControlloID(),
                config.getAmbitoID(),
                config.getFonteDatiID(),
                config.getUtenteFonteDatiID()
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
                this.controlloID,
                this.tipoControlloID,
                this.ambitoID,
                this.fonteDatiID,
                this.utenteFonteDatiID
        );
    }
}
