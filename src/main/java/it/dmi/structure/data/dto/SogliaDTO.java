package it.dmi.structure.data.dto;

import it.dmi.structure.data.dto.idto.IDTO;
import it.dmi.structure.data.entities.Configurazione;
import it.dmi.structure.data.entities.Soglia;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SogliaDTO implements IDTO {

    private Long id;
    private BigInteger sogliaInferiore;
    private BigInteger sogliaSuperiore;
    private String valore;
    private String operatore;
    private Configurazione configurazione;

    @Override
    public <T> IDTO fromEntity (T entity) {
        return entity instanceof Soglia ? new SogliaDTO(
                ((Soglia) entity).getId(),
                ((Soglia) entity).getSogliaInferiore(),
                ((Soglia) entity).getSogliaSuperiore(),
                ((Soglia) entity).getValore(),
                ((Soglia) entity).getOperatore(),
                ((Soglia) entity).getConfigurazione()
        ) : null;
    }

    @Override
    public Soglia toEntity () {
        return new Soglia(
                this.id,
                this.sogliaInferiore,
                this.sogliaSuperiore,
                this.valore,
                this.operatore,
                this.configurazione
        );
    }
}
