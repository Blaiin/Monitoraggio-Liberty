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
public class TipoControlloDTO implements IDTO<TipoControlloDTO, TipoControllo> {

    private Long id;
    private String descrizione;

    @Override
    public TipoControlloDTO fromEntity(TipoControllo tc) {
        if (tc != null) {
            this.id = tc.getId();
            this.descrizione = tc.getDescrizione();
        } return null;
    }

    @Override
    public TipoControllo toEntity() {
        return new TipoControllo(
                this.id,
                this.descrizione
        );
    }
}
