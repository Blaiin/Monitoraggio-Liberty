package it.dmi.structure.data.dto;

import it.dmi.structure.data.dto.idto.IDTO;
import it.dmi.structure.data.entities.Output;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Timestamp;
import java.util.*;

@Getter
@NoArgsConstructor
@Slf4j
public class OutputDTO implements IDTO {

    private Long id;
    @Setter
    private Character esito;

    private Map<String, List<Object>> contenuto;
    @Setter
    private Long configurazioneId;
    @Setter
    private Long azioneId;
    @Setter
    private Long tipoAzioneId;
    @Setter
    private Timestamp inizio;
    @Setter
    private Timestamp fine;
    @Setter
    private Long durata;

    public OutputDTO (Long id, Character esito,
                      Map<String, ?> contenuto,
                      Long configurazioneId, Long azioneId,
                      Long tipoAzioneId, Timestamp inizio,
                      Timestamp fine, Long durata) {
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
            log.error("There was an error while trying to generate output from Configurazione n. {}.",
                    this.configurazioneId, e);
        }
        return adaptedContent;
    }

    @Override
    public <T> IDTO fromEntity (T entity) {
        return entity instanceof Output ? new OutputDTO(
                ((Output) entity).getId(),
                ((Output) entity).getEsito(),
                ((Output) entity).getContenuto(),
                ((Output) entity).getConfigurazioneId(),
                ((Output) entity).getAzioneId(),
                ((Output) entity).getTipoAzioneId(),
                ((Output) entity).getInizio(),
                ((Output) entity).getFine(),
                ((Output) entity).getDurata()
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
