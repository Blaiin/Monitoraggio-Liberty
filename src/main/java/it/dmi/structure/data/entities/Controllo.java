package it.dmi.structure.data.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "\"MON_Controllo\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Controllo {

    @Id
    @Column(name = "\"ControlloID\"")
    private Long id;

    @Column(name = "\"Descrizione\"")
    private String descrizione;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"TipoControlloID\"")
    private TipoControllo tipoControllo;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "\"AmbitoID\"")
    private Ambito ambito;

    @OneToMany(mappedBy = "controllo", fetch = FetchType.LAZY)
    private List<Azione> azioni;

    @OneToMany(mappedBy = "controllo", fetch = FetchType.LAZY)
    private List<Configurazione> configurazioni;

}

