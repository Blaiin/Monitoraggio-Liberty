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
public class AmbitoDTO implements IDTO {

        private Long id;
        private String nome;
        private String destinazione;

    @Override
    public <T> IDTO fromEntity (T entity) {
        return entity instanceof Ambito a ? new AmbitoDTO(
                a.getId(),
                a.getNome(),
                a.getDestinazione()
        ) : null;
    }

    @Override
    public Ambito toEntity () {
        return new Ambito(
                this.getId(),
                this.getNome(),
                this.getDestinazione()
        );
    }
}
