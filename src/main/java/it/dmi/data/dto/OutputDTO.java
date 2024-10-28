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
@Builder
@Slf4j
public class OutputDTO implements IDTO {

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

    public OutputDTO(Long id, Character esito,
                      Map<String, ?> contenuto,
                      Long configurazioneId, Long azioneId,
                      Long tipoAzioneId, LocalDateTime inizio,
                      LocalDateTime fine, Long durata) {
        this.id = id;
        this.esito = esito;
        this.contenuto = adaptContent(contenuto);
        this.configurazioneId = configurazioneId;
        this.azioneId = azioneId;
        this.tipoAzioneId = tipoAzioneId;
        this.inizio = inizio;
        this.fine = fine;
        this.durata = durata;
    }

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
    public <T> IDTO fromEntity (T entity) {
        return entity instanceof Output out ? new OutputDTO(
                out.getId(),
                out.getEsito(),
                out.getContenuto(),
                out.getConfigurazioneId(),
                out.getAzioneId(),
                out.getTipoAzioneId(),
                out.getInizio(),
                out.getFine(),
                out.getDurata()
        ) : null;
    }

    @Override
    public Output toEntity () {
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
