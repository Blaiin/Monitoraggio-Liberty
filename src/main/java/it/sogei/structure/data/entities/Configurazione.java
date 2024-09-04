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
@Table(name = "\"MON_Configurazione\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Configurazione {

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

    @Column(name = "\"ControlloID\"")
    private Long controlloID;

    @Column(name = "\"TipoControlloID\"")
    private Long tipoControlloID;

    @Column(name = "\"AmbitoID\"")
    private Long ambitoID;

    @Column(name = "\"FonteDatiID\"")
    private Long fonteDatiID;

    @Column(name = "\"UtenteFonteDatiID\"")
    private Long utenteFonteDatiID;

}
