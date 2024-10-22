package it.dmi.data.dto;

import it.dmi.data.dto.idto.IDTO;
import it.dmi.data.entities.*;
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
    private int ordineControllo;

    @Override
    public <T> IDTO fromEntity (T entity) {
        return entity instanceof Controllo c ? new ControlloDTO(
                c.getId(),
                c.getDescrizione(),
                c.getTipoControllo(),
                c.getAmbito(),
                c.getAzioni(),
                c.getConfigurazioni(),
                c.getOrdineControllo()
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
                this.configurazioni,
                this.ordineControllo
        );
    }
}
