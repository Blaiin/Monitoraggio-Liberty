package it.dmi.data.api.repositories.impl;

import it.dmi.data.api.repositories.ARepository;
import it.dmi.data.entities.task.Configurazione;
import jakarta.ejb.Stateless;

@Stateless
public class ConfigurazioneRP extends ARepository<Configurazione> {

    public ConfigurazioneRP() {
        super(Configurazione.class);
    }
}
