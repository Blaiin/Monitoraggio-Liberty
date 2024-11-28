package it.dmi.data.dto;

import it.dmi.data.dto.idto.IDTO;
import it.dmi.data.entities.Ambito;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@SuppressWarnings("unused")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AmbitoDTO implements IDTO<AmbitoDTO, Ambito> {

    private Long id;
    private String nome;
    private String destinazione;

    public AmbitoDTO(String nome, String destinazione) {
        this.nome = nome;
        this.destinazione = destinazione;
    }

    @Override
    public AmbitoDTO fromEntity(Ambito a) {
        if (a != null) {
            this.id = a.getId();
            this.nome = a.getNome();
            this.destinazione = a.getDestinazione();
            return this;
        } return null;
    }

    @Override
    public Ambito toEntity() {
        return new Ambito(
                this.getId(),
                this.getNome(),
                this.getDestinazione()
        );
    }
}
