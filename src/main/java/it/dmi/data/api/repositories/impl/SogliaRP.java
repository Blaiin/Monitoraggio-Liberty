package it.dmi.data.api.repositories.impl;

import it.dmi.data.api.repositories.ARepository;
import it.dmi.data.entities.Soglia;
import jakarta.ejb.Stateless;

@SuppressWarnings("unused")
@Stateless
public class SogliaRP extends ARepository<Soglia> {

    public SogliaRP() {
        super(Soglia.class);
    }
}
