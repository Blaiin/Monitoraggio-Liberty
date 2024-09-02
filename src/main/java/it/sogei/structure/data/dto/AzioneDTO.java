package it.sogei.structure.data.dto;

import it.sogei.structure.data.dto.idto.IDTO;
import it.sogei.structure.data.entities.Azione;
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
        private Long tipoAzioneID;
        private Long sogliaID;
        private Long controlloID;
        private Long tipoControlloID;
        private Long ambitoID;
        private Long fonteDatiID;
        private Long utenteFonteDatiID;

    @Override
    public <T> IDTO fromEntity (T entity) {
        return entity instanceof Azione azione ? new AzioneDTO(
                azione.getId(),
                azione.getDestinatario(),
                azione.getSqlScript(),
                azione.getProgramma(),
                azione.getClasse(),
                azione.getTipoAzioneID(),
                azione.getSogliaID(),
                azione.getControlloID(),
                azione.getTipoControlloID(),
                azione.getAmbitoID(),
                azione.getFonteDatiID(),
                azione.getUtenteFonteDatiID()
        ) : null;
    }

    @Override
    public Azione toEntity () {
        return new Azione(this.id,
                this.destinatario,
                this.sqlScript,
                this.programma,
                this.classe,
                this.tipoAzioneID,
                this.sogliaID,
                this.controlloID,
                this.tipoControlloID,
                this.ambitoID,
                this.fonteDatiID,
                this.utenteFonteDatiID
        );
    }
}
