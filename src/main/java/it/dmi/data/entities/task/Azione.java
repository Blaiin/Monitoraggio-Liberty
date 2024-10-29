package it.dmi.data.entities.task;

import it.dmi.caches.AzioneQueueCache;
import it.dmi.data.entities.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "\"MON_Azione\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public non-sealed class Azione implements QuartzTask {

    @Id
    @Column(name = "\"AzioneID\"")
    private Long id;

    @Column(name = "\"Destinatario\"")
    private String destinatario;

    @Column(name = "\"SQLScript\"")
    private String sqlScript;

    @Column(name = "\"Programma\"")
    private String programma;

    @Column(name = "\"Classe\"")
    private String classe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"TipoAzioneID\"")
    private TipoAzione tipoAzione;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"SogliaID\"")
    private Soglia soglia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"ControlloID\"")
    private Controllo controllo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"TipoControlloID\"")
    private TipoControllo tipoControllo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"AmbitoID\"")
    private Ambito ambito;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"FonteDatiID\"")
    private FonteDati fonteDati;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"UtenteFonteDatiID\"")
    private SicurezzaFonteDati utenteFonteDati;

    @Column(name = "\"ORDINEAZIONE\"")
    private int ordineAzione;

    public void queue() {
        var cID = soglia.getConfigurazione().getStringID(); var sID = String.valueOf(soglia.getId());
        log.info("Queueing action for S: {}, C: {}", sID, cID);
        AzioneQueueCache.put(sID, this);
    }

    @Override
    public String toString() {
        String sql = "SQL Script", prog = "Programma", cl = "Classe", dest = "Destinatario";
        if(sqlScript != null) return "Azione: " + sql;
        else if(programma != null) return "Azione: " + prog;
        else if(classe != null) return "Azione: " + cl;
        else if(destinatario != null) return "Azione: " + dest;
        else return "Azione: not valid.";
    }

    @Override
    public String getStringID() {
        return String.valueOf(this.id);
    }
}
