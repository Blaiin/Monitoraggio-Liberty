package it.dmi.structure.data.dto;

import it.dmi.structure.data.dto.idto.IDTO;
import it.dmi.structure.data.entities.Ambito;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
        return entity instanceof Ambito ? new AmbitoDTO(
                ((Ambito) entity).getId(),
                ((Ambito) entity).getNome(),
                ((Ambito) entity).getDestinazione()
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
