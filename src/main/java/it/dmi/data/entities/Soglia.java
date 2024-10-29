package it.dmi.data.entities;

import it.dmi.data.entities.task.Azione;
import it.dmi.data.entities.task.Configurazione;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;

@Entity
@Table(name = "\"MON_Soglia\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Soglia {

    @Id
    @Column(name = "\"SogliaID\"")
    private Long id;

    @Column(name = "\"SogliaInferiore\"")
    private BigInteger sogliaInferiore;

    @Column(name = "\"SogliaSuperiore\"")
    private BigInteger sogliaSuperiore;

    @Column(name = "\"Valore\"")
    private String valore;

    @Column(name = "\"Operatore\"", length = 10)
    private String operatore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"ConfigurazioneID\"")
    private Configurazione configurazione;

    @OneToMany(mappedBy = "soglia", fetch = FetchType.LAZY)
    private List<Azione> azioni;

    public int getAzioniSize() {
        return azioni.size();
    }

    public List<Azione> getAzioniOrdered() {
        return azioni.stream()
                .sorted(Comparator.comparingInt(Azione::getOrdineAzione))
                .toList();
    }

    public Soglia (Long id, BigInteger sogliaInferiore, BigInteger sogliaSuperiore,
                   String valore, String operatore, Configurazione configurazione) {
        this.id = id;
        this.sogliaInferiore = sogliaInferiore;
        this.sogliaSuperiore = sogliaSuperiore;
        this.valore = valore;
        this.operatore = operatore;
        this.configurazione = configurazione;
    }

    public String getStringID() {
        return String.valueOf(this.id);
    }
}

