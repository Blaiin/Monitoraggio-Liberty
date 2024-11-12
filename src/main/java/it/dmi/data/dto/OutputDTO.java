package it.dmi.data.dto;

import it.dmi.data.dto.idto.IDTO;
import it.dmi.data.entities.Output;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class OutputDTO implements IDTO<OutputDTO, Output> {

    private Long id;
    private Character esito;
    @Getter(AccessLevel.NONE)
    private Map<String, List<Object>> contenuto;
    private Long configurazioneId;
    private Long azioneId;
    private Long tipoAzioneId;
    private LocalDateTime inizio;
    private LocalDateTime fine;
    private Long durata;

    public void setContenuto(Map<String, ?> contenuto) {
        this.contenuto = adaptContent(contenuto);
    }

    private Map<String, List<Object>> adaptContent(Map<String, ?> content) {
        Map<String, List<Object>> adaptedContent = new HashMap<>();
        try {
            content.forEach((k, v) -> {
                if(v instanceof Integer) {
                    List<Integer> countList = Collections.singletonList((Integer) v);
                    adaptedContent.put(k, Collections.singletonList(countList));
                }
                if(v instanceof List<?>) {
                    List<Object> typeSafeList = new ArrayList<>((List<?>) v);
                    adaptedContent.put(k, typeSafeList);
                }
            });
        } catch (Exception e) {
            log.error("There was an error while trying to generate output for Config {}.",
                    this.configurazioneId, e);
        }
        return adaptedContent;
    }

    @Override
    public OutputDTO fromEntity(Output o) {
        if (o != null) {
            this.id = o.getId();
            this.esito = o.getEsito();
            this.contenuto = o.getContenuto();
            this.configurazioneId = o.getConfigurazioneId();
            this.azioneId = o.getAzioneId();
            this.tipoAzioneId = o.getTipoAzioneId();
            this.inizio = o.getInizio();
            this.fine = o.getFine();
            this.durata = o.getDurata();
        } return null;
    }

    @Override
    public Output toEntity() {
        return new Output(
                this.esito,
                this.contenuto,
                this.configurazioneId,
                this.azioneId,
                this.tipoAzioneId,
                this.inizio,
                this.fine,
                this.durata
        );
    }
}
