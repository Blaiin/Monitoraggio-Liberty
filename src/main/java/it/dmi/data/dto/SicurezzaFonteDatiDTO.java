package it.dmi.data.dto;

import it.dmi.data.dto.idto.IDTO;
import it.dmi.data.entities.SicurezzaFonteDati;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SicurezzaFonteDatiDTO implements IDTO {

    private Long id;
    private String descrizione;
    private String userID;
    private String password;

    @Override
    public <T> IDTO fromEntity (T entity) {
        return entity instanceof SicurezzaFonteDati sfd ? new SicurezzaFonteDatiDTO(
                sfd.getId(),
                sfd.getDescrizione(),
                sfd.getUserID(),
                sfd.getPassword()
        ) : null;
    }

    @Override
    public SicurezzaFonteDati toEntity () {
        return new SicurezzaFonteDati(
                this.id,
                this.descrizione,
                this.userID,
                this.password
        );
    }
}
