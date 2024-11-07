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
public class AzioneDTO implements IDTO<AzioneDTO, Azione> {

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
    public AzioneDTO fromEntity(Azione a) {
        if (a != null) {
            this.id = a.getId();
            this.destinatario = a.getDestinatario();
            this.sqlScript = a.getSqlScript();
            this.programma = a.getProgramma();
            this.classe = a.getClasse();
            this.tipoAzione = a.getTipoAzione();
            this.soglia = a.getSoglia();
            this.controllo = a.getControllo();
            this.tipoControllo = a.getTipoControllo();
            this.ambito = a.getAmbito();
            this.fonteDati = a.getFonteDati();
            this.utenteFonteDati = a.getUtenteFonteDati();
            this.ordineAzione = a.getOrdineAzione();
        } return null;
    }

    @Override
    public Azione toEntity() {
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
