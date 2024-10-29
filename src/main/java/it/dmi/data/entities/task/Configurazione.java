package it.dmi.data.entities.task;

import it.dmi.data.entities.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Stream;

@Entity
@Table(name = "\"MON_Configurazione\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public non-sealed class Configurazione implements QuartzTask {

    @Id
    @Column(name = "\"ConfigurazioneID\"")
    private Long id;

    @Column(name = "\"Nome\"")
    private String nome;

    @Column(name = "\"SQLScript\"")
    private String sqlScript;

    @Column(name = "\"Programma\"")
    private String programma;

    @Column(name = "\"Classe\"")
    private String classe;

    @Column(name = "\"Schedulazione\"")
    private String schedulazione;

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

    @Column(name = "\"ORDINECONFIGURAZIONE\"")
    private int ordineConfigurazione;

    @OneToMany(mappedBy = "configurazione", fetch = FetchType.LAZY)
    private List<Soglia> soglie;

    public Configurazione (Long id, String nome, String sqlScript,
                           String programma, String classe, String schedulazione,
                           Controllo controllo, TipoControllo tipoControllo, Ambito ambito,
                           FonteDati fonteDati, SicurezzaFonteDati utenteFonteDati, int ordineConfigurazione) {
        this.id = id;
        this.nome = nome;
        this.sqlScript = sqlScript;
        this.programma = programma;
        this.classe = classe;
        this.schedulazione = schedulazione;
        this.controllo = controllo;
        this.tipoControllo = tipoControllo;
        this.ambito = ambito;
        this.fonteDati = fonteDati;
        this.utenteFonteDati = utenteFonteDati;
        this.ordineConfigurazione = ordineConfigurazione;
    }

    public String getStringID() {
        return String.valueOf(this.id);
    }

    public Stream<Soglia> getSoglieAsStream() {
        return this.soglie.stream();
    }
}
