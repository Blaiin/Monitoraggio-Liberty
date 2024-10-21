package it.dmi.structure.data.dto;

import it.dmi.structure.data.dto.idto.IDTO;
import it.dmi.structure.data.entities.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ControlloDTO implements IDTO {

    private Long id;
    private String descrizione;
    private TipoControllo tipoControllo;
    private Ambito ambito;
    private List<Azione> azioni;
    private List<Configurazione> configurazioni;

    @Override
    public <T> IDTO fromEntity (T entity) {
        return entity instanceof Controllo ? new ControlloDTO(
                ((Controllo) entity).getId(),
                ((Controllo) entity).getDescrizione(),
                ((Controllo) entity).getTipoControllo(),
                ((Controllo) entity).getAmbito(),
                ((Controllo) entity).getAzioni(),
                ((Controllo) entity).getConfigurazioni()
        ) : null;
    }

    @Override
    public Controllo toEntity () {
        return new Controllo(
                this.id,
                this.descrizione,
                this.tipoControllo,
                this.ambito,
                this.azioni,
                this.configurazioni
        );
    }
}
