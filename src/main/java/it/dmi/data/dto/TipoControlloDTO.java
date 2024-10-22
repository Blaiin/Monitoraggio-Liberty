package it.dmi.data.dto;

import it.dmi.data.dto.idto.IDTO;
import it.dmi.data.entities.TipoControllo;
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
        return entity instanceof TipoControllo ? new TipoAzioneDTO(
                ((TipoControllo) entity).getId(),
                ((TipoControllo) entity).getDescrizione()
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
