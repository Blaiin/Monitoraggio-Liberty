package it.dmi.structure.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "MON_TipoAzione")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoAzione {

    @Id
    @Column(name = "TipoAzioneID")
    private Long id;

    @Column(name = "Descrizione")
    private String descrizione;

}
