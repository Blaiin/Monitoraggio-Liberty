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
@Table(name = "MON_Azione")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Azione {

    @Id
    @Column(name = "AzioneID")
    private Long id;

    @Column(name = "Destinatario")
    private String destinatario;

    @Column(name = "SQLScript")
    private String sqlScript;

    @Column(name = "Programma")
    private String programma;

    @Column(name = "Classe")
    private String classe;

    @Column(name = "TipoAzioneID")
    private Long tipoAzioneID;

    @Column(name = "SogliaID")
    private Long sogliaID;

    @Column(name = "ControlloID")
    private Long controlloID;

    @Column(name = "TipoControlloID")
    private Long tipoControlloID;

    @Column(name = "AmbitoID")
    private Long ambitoID;

    @Column(name = "FonteDatiID")
    private Long fonteDatiID;

    @Column(name = "UtenteFonteDatiID")
    private Long utenteFonteDatiID;

}
