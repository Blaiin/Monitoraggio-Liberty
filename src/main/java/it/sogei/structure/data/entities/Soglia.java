package it.sogei.structure.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Entity
@Table(name = "MON_Soglia")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Soglia {

    @Id
    @Column(name = "SogliaID")
    private Long id;

    @Column(name = "SogliaInferiore")
    private BigInteger sogliaInferiore;

    @Column(name = "SogliaSuperiore")
    private BigInteger sogliaSuperiore;

    @Column(name = "Valore")
    private String valore;

    @Column(name = "Operatore", length = 10)
    private String operatore;

    @Column(name = "ConfigurazioneID")
    private Long configurazioneId;

}

