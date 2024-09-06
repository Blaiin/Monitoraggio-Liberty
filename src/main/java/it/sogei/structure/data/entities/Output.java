package it.sogei.structure.data.entities;


import it.sogei.utils.jpa_converters.JSONConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Table(name = "\"MON_Output\"")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Output {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "\"OutputID\"")
    private Long id;

    @Column(name = "\"Esito\"")
    private Character esito;

    @Convert(converter = JSONConverter.class)
    @Column(name = "\"Contenuto\"")
    private List<String> contenuto;

    @Column(name = "\"ConfigurazioneID\"")
    private Long configurazioneId;

    @Column(name = "\"AzioneID\"")
    private Long azioneId;

    @Column(name = "\"TipoAzioneID\"")
    private Long tipoAzioneId;

    @Column(name = "\"Inizio\"")
    private Timestamp inizio;

    @Column(name = "\"Fine\"")
    private Timestamp fine;
    @Column(name = "\"Durata\"")
    private Long durata;

    public Output(Character esito, List<String> contenuto, Long configurazioneId, Long azioneId,
                  Long tipoAzioneId, Timestamp inizio, Timestamp fine, Long durata) {
        this.esito = esito;
        this.contenuto = contenuto;
        this.configurazioneId = configurazioneId;
        this.azioneId = azioneId;
        this.tipoAzioneId = tipoAzioneId;
        this.inizio = inizio;
        this.fine = fine;
        this.durata = durata;
    }
}


