package it.sogei.structure.data.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Clob;
import java.time.LocalDate;

@Entity
@Table(name = "MON_Output")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Output {

    @Id
    @Column(name = "OutputID")
    private Long id;

    @Column(name = "Inizio")
    private LocalDate inizio;

    @Column(name = "Fine")
    private LocalDate fine;

    @Column(name = "Durata")
    private LocalDate durata;

    @Column(name = "Esito")
    private Character esito;

    @Lob
    @Column(name = "Contenuto")
    private Clob contenuto;

    @Column(name = "ConfigurazioneID")
    private Long configurazioneId;

    @Column(name = "AzioneID")
    private Long azioneId;

    @Column(name = "TipoAzioneID")
    private Long tipoAzioneId;
}


