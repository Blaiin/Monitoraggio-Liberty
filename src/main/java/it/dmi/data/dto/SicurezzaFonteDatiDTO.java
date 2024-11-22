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
public class SicurezzaFonteDatiDTO implements IDTO<SicurezzaFonteDatiDTO, SicurezzaFonteDati> {

    private Long id;
    private String descrizione;
    private String userID;
    private String password;

    public SicurezzaFonteDatiDTO(String password, String descrizione, String userID) {
        this.password = password;
        this.descrizione = descrizione;
        this.userID = userID;
    }

    @Override
    public SicurezzaFonteDatiDTO fromEntity(SicurezzaFonteDati sfd) {
        if (sfd != null) {
            this.id = sfd.getId();
            this.descrizione = sfd.getDescrizione();
            this.userID = sfd.getUserID();
            this.password = sfd.getPassword();
        } return null;
    }

    @Override
    public SicurezzaFonteDati toEntity() {
        return new SicurezzaFonteDati(
                this.id,
                this.descrizione,
                this.userID,
                this.password
        );
    }
}
