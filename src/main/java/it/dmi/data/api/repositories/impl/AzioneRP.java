package it.dmi.data.api.repositories.impl;

import it.dmi.data.api.repositories.ARepository;
import it.dmi.data.entities.task.Azione;
import jakarta.ejb.Stateless;

@Stateless
public class AzioneRP extends ARepository<Azione> {

    public AzioneRP() {
        super(Azione.class);
    }
}
