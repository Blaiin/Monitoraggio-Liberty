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
@Table(name = "MON_Ambito")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ambito {

    @Id
    @Column(name = "AmbitoID")
    private Long id;

    @Column(name = "Nome")
    private String nome;

    @Column(name = "Destinazione")
    private String destinazione;

}