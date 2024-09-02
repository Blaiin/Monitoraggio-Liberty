package it.sogei.structure.data.dto;

import it.sogei.structure.data.dto.idto.IDTO;
import it.sogei.structure.data.entities.TipoAzione;
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
        return entity instanceof TipoAzione tipoAzione ? new TipoAzioneDTO(
                tipoAzione.getId(),
                tipoAzione.getDescrizione()
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
