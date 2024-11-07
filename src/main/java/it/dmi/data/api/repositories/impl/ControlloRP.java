package it.dmi.data.api.repositories.impl;

import it.dmi.data.api.repositories.ARepository;
import it.dmi.data.entities.Controllo;
import jakarta.ejb.Stateless;

@SuppressWarnings("unused")
@Stateless
public class ControlloRP extends ARepository<Controllo> {

    public ControlloRP() {
        super(Controllo.class);
    }
}
