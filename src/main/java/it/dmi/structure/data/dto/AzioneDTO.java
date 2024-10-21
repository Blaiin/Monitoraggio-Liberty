package it.dmi.structure.data.dto;

import it.dmi.structure.data.dto.idto.IDTO;
import it.dmi.structure.data.entities.*;
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

    @Override
    public <T> IDTO fromEntity (T entity) {
        return entity instanceof Azione ? new AzioneDTO(
                ((Azione) entity).getId(),
                ((Azione) entity).getDestinatario(),
                ((Azione) entity).getSqlScript(),
                ((Azione) entity).getProgramma(),
                ((Azione) entity).getClasse(),
                ((Azione) entity).getTipoAzione(),
                ((Azione) entity).getSoglia(),
                ((Azione) entity).getControllo(),
                ((Azione) entity).getTipoControllo(),
                ((Azione) entity).getAmbito(),
                ((Azione) entity).getFonteDati(),
                ((Azione) entity).getUtenteFonteDati()
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
                this.utenteFonteDati
        );
    }
}
