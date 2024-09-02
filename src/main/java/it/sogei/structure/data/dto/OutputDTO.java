package it.sogei.structure.data.dto;

import it.sogei.structure.data.dto.idto.IDTO;
import it.sogei.structure.data.entities.Output;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Clob;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OutputDTO implements IDTO {
    private Long id;
    private LocalDate inizio;
    private LocalDate fine;
    private LocalDate durata;
    private Character esito;
    private Clob contenuto;
    private Long configurazioneId;
    private Long azioneId;
    private Long tipoAzioneId;

    @Override
    public <T> IDTO fromEntity (T entity) {
        return entity instanceof Output output ? new OutputDTO(
                output.getId(),
                output.getInizio(),
                output.getFine(),
                output.getDurata(),
                output.getEsito(),
                output.getContenuto(),
                output.getConfigurazioneId(),
                output.getAzioneId(),
                output.getTipoAzioneId()
        ) : null;
    }

    @Override
    public Output toEntity () {
        return new Output(
                this.id,
                this.inizio,
                this.fine,
                this.durata,
                this.esito,
                this.contenuto,
                this.configurazioneId,
                this.azioneId,
                this.tipoAzioneId
        );
    }
}
