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
@Table(name = "MON_TipoAzione")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TipoControllo {

    @Id
    @Column(name = "TipoControlloID")
    private Long id;

    @Column(name = "Descrizione")
    private String descrizione;

}
