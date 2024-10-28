package it.dmi.data.dto;

import it.dmi.data.dto.idto.IDTO;
import it.dmi.data.entities.*;
import it.dmi.data.entities.task.Azione;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AzioneDTO implements IDTO {

        private Long id;
        private String destinatario;
        private String sqlScript;
        private String programma;
        private String classe;
        private TipoAzione tipoAzione;
        private Soglia soglia;
        private Controllo controllo;
        private TipoControllo tipoControllo;
        private Ambito ambito;
        private FonteDati fonteDati;
        private SicurezzaFonteDati utenteFonteDati;
        private int ordineAzione;

    @Override
    public <T> IDTO fromEntity (T entity) {
        return entity instanceof Azione a ? new AzioneDTO(
                a.getId(),
                a.getDestinatario(),
                a.getSqlScript(),
                a.getProgramma(),
                a.getClasse(),
                a.getTipoAzione(),
                a.getSoglia(),
                a.getControllo(),
                a.getTipoControllo(),
                a.getAmbito(),
                a.getFonteDati(),
                a.getUtenteFonteDati(),
                a.getOrdineAzione()
        ) : null;
    }

    @Override
    public Azione toEntity () {
        return new Azione(this.id,
                this.destinatario,
                this.sqlScript,
                this.programma,
                this.classe,
                this.tipoAzione,
                this.soglia,
                this.controllo,
                this.tipoControllo,
                this.ambito,
                this.fonteDati,
                this.utenteFonteDati,
                this.ordineAzione
        );
    }
}
