package it.sogei.structure.data.dto;

import it.sogei.structure.data.dto.idto.IDTO;
import it.sogei.structure.data.entities.Output;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutputDTO implements IDTO {

    private Long id;
    private Character esito;
    private List<String> contenuto;
    private Long configurazioneId;
    private Long azioneId;
    private Long tipoAzioneId;
    private Timestamp inizio;
    private Timestamp fine;
    private Long durata;

    @Override
    public <T> IDTO fromEntity (T entity) {
        return entity instanceof Output output ? new OutputDTO(
                output.getId(),
                output.getEsito(),
                output.getContenuto(),
                output.getConfigurazioneId(),
                output.getAzioneId(),
                output.getTipoAzioneId(),
                output.getInizio(),
                output.getFine(),
                output.getDurata()
        ) : null;
    }

    @Override
    public Output toEntity () {
        return new Output(
                this.esito,
                this.contenuto,
                this.configurazioneId,
                this.azioneId,
                this.tipoAzioneId,
                this.inizio,
                this.fine,
                this.durata
        );
    }
}
