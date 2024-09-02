package it.sogei.structure.data.dto;

import it.sogei.structure.data.dto.idto.IDTO;
import it.sogei.structure.data.entities.FonteDati;
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
        return entity instanceof FonteDati fonteDati ? new FonteDatiDTO(
                fonteDati.getId(),
                fonteDati.getDescrizione(),
                fonteDati.getNomeDriver(),
                fonteDati.getNomeClasse(),
                fonteDati.getUrl(),
                fonteDati.getJndiName()
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
