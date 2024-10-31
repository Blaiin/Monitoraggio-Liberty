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
public class FonteDatiDTO implements IDTO {

    private Long id;
    private String descrizione;
    private String nomeDriver;
    private String nomeClasse;
    private String url;
    private String jndiName;

    @Override
    public <T> IDTO fromEntity (T entity) {
        return entity instanceof FonteDati fd ? new FonteDatiDTO(
                fd.getId(),
                fd.getDescrizione(),
                fd.getNomeDriver(),
                fd.getNomeClasse(),
                fd.getUrl(),
                fd.getJndiName()
        ) : null;
    }

    @Override
    public FonteDati toEntity () {
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
