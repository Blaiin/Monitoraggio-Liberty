package it.sogei.structure.data.dto;

import it.sogei.structure.data.dto.idto.IDTO;
import it.sogei.structure.data.entities.Soglia;
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
    private Long configurazioneId;

    @Override
    public <T> IDTO fromEntity (T entity) {
        return entity instanceof Soglia soglia ? new SogliaDTO(
                soglia.getId(),
                soglia.getSogliaInferiore(),
                soglia.getSogliaSuperiore(),
                soglia.getValore(),
                soglia.getOperatore(),
                soglia.getConfigurazioneId()
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
                this.configurazioneId
        );
    }
}
