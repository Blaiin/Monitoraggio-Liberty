package it.sogei.structure.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "MON_Controllo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Controllo {

    @Id
    @Column(name = "ControlloID")
    private Long id;

    @Column(name = "Descrizione")
    private String descrizione;

    @Column(name = "TipoControlloID")
    private Long tipoControlloID;

    @Column(name = "AmbitoID")
    private Long ambitoID;

}

