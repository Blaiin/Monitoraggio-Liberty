package it.dmi.data.dto;

import it.dmi.data.dto.idto.IDTO;
import it.dmi.data.entities.TipoAzione;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoAzioneDTO implements IDTO {

    private Long id;
    private String descrizione;

    @Override
    public <T> IDTO fromEntity (T entity) {
        return entity instanceof TipoAzione ta ? new TipoAzioneDTO(
                ta.getId(),
                ta.getDescrizione()
        ) : null;
    }

    @Override
    public TipoAzione toEntity () {
        return new TipoAzione(
                this.id,
                this.descrizione
        );
    }
}
