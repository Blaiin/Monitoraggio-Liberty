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
public class TipoAzioneDTO implements IDTO<TipoAzioneDTO, TipoAzione> {

    private Long id;
    private String descrizione;

    @Override
    public TipoAzioneDTO fromEntity(TipoAzione ta) {
        if (ta != null) {
            this.id = ta.getId();
            this.descrizione = ta.getDescrizione();
        } return null;
    }

    @Override
    public TipoAzione toEntity() {
        return new TipoAzione(
                this.id,
                this.descrizione
        );
    }
}
