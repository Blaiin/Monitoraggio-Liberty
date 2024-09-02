package it.sogei.structure.data.dto;

import it.sogei.structure.data.dto.idto.IDTO;
import it.sogei.structure.data.entities.Controllo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ControlloDTO implements IDTO {

    private Long id;
    private String descrizione;
    private Long tipoControlloID;
    private Long ambitoID;

    @Override
    public <T> IDTO fromEntity (T entity) {
        return entity instanceof Controllo controllo ? new ControlloDTO(
                controllo.getId(),
                controllo.getDescrizione(),
                controllo.getTipoControlloID(),
                controllo.getAmbitoID()
        ) : null;
    }

    @Override
    public Controllo toEntity () {
        return new Controllo(
                this.id,
                this.descrizione,
                this.tipoControlloID,
                this.ambitoID
        );
    }
}
