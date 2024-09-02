package it.sogei.structure.data.dto;

import it.sogei.structure.data.dto.idto.IDTO;
import it.sogei.structure.data.entities.TipoControllo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoControlloDTO implements IDTO {
    private Long id;
    private String descrizione;

    @Override
    public <T> IDTO fromEntity (T entity) {
        return entity instanceof TipoControllo tipoControllo ? new TipoAzioneDTO(
                tipoControllo.getId(),
                tipoControllo.getDescrizione()
        ) : null;
    }

    @Override
    public TipoControllo toEntity () {
        return new TipoControllo(
                this.id,
                this.descrizione
        );
    }
}
