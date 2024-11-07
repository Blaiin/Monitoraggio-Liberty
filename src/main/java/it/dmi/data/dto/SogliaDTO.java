package it.dmi.data.dto;

import it.dmi.data.dto.idto.IDTO;
import it.dmi.data.entities.Soglia;
import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class SogliaDTO implements IDTO<SogliaDTO, Soglia> {

    private Long id;
    private BigInteger sogliaInferiore;
    private BigInteger sogliaSuperiore;
    private String valore;
    private String operatore;
    private Configurazione configurazione;
    private List<Azione> azioni;

    public int getAzioniSize() {
        return azioni.size();
    }

    public List<Azione> getAzioniOrdered() {
        return azioni.stream()
                .sorted(Comparator.comparingInt(Azione::getOrdineAzione))
                .toList();
    }

    public boolean compare(String toEvaluate) {
        if (toEvaluate instanceof String s) {
            return s.contains(this.valore);
        }
        return false;
    }

    public void queueActions(final String cID) {
        List<Azione> actions = this.getAzioniOrdered();
        log.debug("Queueing {} Azioni (Config {}, Soglia {})", actions.size(), cID, this.getStrID());
        actions.forEach(Azione::queue);
    }

    public boolean resultWithinRange(final int result, final String cID) {
        final boolean range = this.range(result, true);
        if (range) {
            return true;
        } else {
            log.warn("Config {} result outside range (Soglia {}).", cID, this.getStrID());
            return false;
        }
    }

    private boolean range(int value) {
        return (value > this.sogliaInferiore.intValue())
                && (value < this.sogliaSuperiore.intValue());
    }

    public boolean range(int value, boolean inclusive) {
        if (inclusive) {
            return (this.sogliaInferiore.intValue() <= value)
                    && (value <= this.sogliaSuperiore.intValue());
        }
        return range(value);
    }

    public boolean singleValue() {
        return !isMultiValue();
    }

    public boolean isMultiValue() {
        boolean singleValue = this.valore != null;
        boolean lowerAndUpper = this.sogliaInferiore != null && this.sogliaSuperiore != null;
        boolean noValues = this.valore == null && !lowerAndUpper;
        if (singleValue && lowerAndUpper) {
            throw new IllegalStateException("Both single threshold and multi threshold for comparison had values;" +
                    " cannot determine logic.");
        }
        if (noValues) {
            throw new IllegalStateException("Invalid configuration for Soglia: " + this.id +
                    ". At least one value must be present (Valore OR both SogliaInferiore and SogliaSuperiore).");
        }
        return !singleValue;
    }

    public String getStrID() {
        return String.valueOf(this.id);
    }

    @Override
    public SogliaDTO fromEntity(Soglia s) {
        if (s != null) {
            this.id = s.getId();
            this.sogliaInferiore = s.getSogliaInferiore();
            this.sogliaSuperiore = s.getSogliaSuperiore();
            this.valore = s.getValore();
            this.operatore = s.getOperatore();
            this.configurazione = s.getConfigurazione();
            this.azioni = s.getAzioni();
            return this;
        } return null;
    }

    @Override
    public Soglia toEntity() {
        return new Soglia(
                this.id,
                this.sogliaInferiore,
                this.sogliaSuperiore,
                this.valore,
                this.operatore,
                this.configurazione,
                this.azioni
        );
    }
}
