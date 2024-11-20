package it.dmi.data.dto;

import it.dmi.data.dto.idto.IDTO;
import it.dmi.data.entities.FonteDati;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FonteDatiDTO implements IDTO<FonteDatiDTO, FonteDati> {

    private Long id;
    private String descrizione;
    private String nomeDriver;
    private String nomeClasse;
    private String url;
    private String jndiName;

    public FonteDatiDTO(String jndiName, String url, String nomeClasse, String descrizione, String nomeDriver) {
        this.jndiName = jndiName;
        this.url = url;
        this.nomeClasse = nomeClasse;
        this.descrizione = descrizione;
        this.nomeDriver = nomeDriver;
    }

    @Override
    public FonteDatiDTO fromEntity(FonteDati fd) {
        if (fd != null) {
            this.id = fd.getId();
            this.descrizione = fd.getDescrizione();
            this.nomeDriver = fd.getNomeDriver();
            this.nomeClasse = fd.getNomeClasse();
            this.url = fd.getUrl();
            this.jndiName = fd.getJndiName();
            return this;
        } return null;
    }

    @Override
    public FonteDati toEntity() {
        return new FonteDati(
                this.id,
                this.descrizione,
                this.nomeDriver,
                this.nomeClasse,
                this.url,
                this.jndiName
        );
    }
}
