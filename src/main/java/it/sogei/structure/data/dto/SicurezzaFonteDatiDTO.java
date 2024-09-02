package it.sogei.structure.data.dto;

import it.sogei.structure.data.dto.idto.IDTO;
import it.sogei.structure.data.entities.SicurezzaFonteDati;
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
        return entity instanceof SicurezzaFonteDati sFonteDati ? new SicurezzaFonteDatiDTO(
                sFonteDati.getId(),
                sFonteDati.getDescrizione(),
                sFonteDati.getUserID(),
                sFonteDati.getPassword()
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
