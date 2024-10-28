package it.dmi.data.entities;


import it.dmi.utils.jpa_converters.JSONConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
    private Map<String, List<Object>> contenuto;

    @Column(name = "\"ConfigurazioneID\"")
    private Long configurazioneId;

    @Column(name = "\"AzioneID\"")
    private Long azioneId;

    @Column(name = "\"TipoAzioneID\"")
    private Long tipoAzioneId;

    @Column(name = "\"Inizio\"")
    private LocalDateTime inizio;

    @Column(name = "\"Fine\"")
    private LocalDateTime fine;

    @Column(name = "\"Durata\"")
    private Long durata;

    public Output(Character esito, Map<String, List<Object>> contenuto, Long configurazioneId, Long azioneId,
                  Long tipoAzioneId, LocalDateTime inizio, LocalDateTime fine, Long durata) {
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


