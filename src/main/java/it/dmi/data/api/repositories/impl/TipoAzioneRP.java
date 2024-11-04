package it.dmi.data.api.repositories.impl;

import it.dmi.data.api.repositories.ARepository;
import it.dmi.data.entities.TipoAzione;
import jakarta.ejb.Stateless;

@Stateless
public class TipoAzioneRP extends ARepository<TipoAzione> {

    public TipoAzioneRP() {
        super(TipoAzione.class);
    }
}
