package it.dmi.data.dto;

import it.dmi.data.dto.idto.IDTO;
import it.dmi.data.entities.Ambito;
import it.dmi.data.entities.Controllo;
import it.dmi.data.entities.TipoControllo;
import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ControlloDTO implements IDTO<ControlloDTO, Controllo> {

    private Long id;
    private String descrizione;
    private TipoControllo tipoControllo;
    private Ambito ambito;
    private List<Azione> azioni;
    private List<Configurazione> configurazioni;
    private int ordineControllo;

    //TODO implement retrieval of entities associated
    public ControlloDTO(String descrizione, Long tipoControlloID, Long ambitoID, int ordineControllo) {
        this.descrizione = descrizione;
    }

    @Override
    public ControlloDTO fromEntity(Controllo c) {
        if (c != null) {
            this.id = c.getId();
            this.descrizione = c.getDescrizione();
            this.tipoControllo = c.getTipoControllo();
            this.ambito = c.getAmbito();
            this.azioni = c.getAzioni();
            this.configurazioni = c.getConfigurazioni();
            this.ordineControllo = c.getOrdineControllo();
        } return null;
    }

    @Override
    public Controllo toEntity() {
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
