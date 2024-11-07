package it.dmi.data.api.repositories.impl;

import it.dmi.data.api.repositories.ARepository;
import it.dmi.data.entities.TipoControllo;
import jakarta.ejb.Stateless;

@SuppressWarnings("unused")
@Stateless
public class TipoControlloRP extends ARepository<TipoControllo> {

    public TipoControlloRP() {
        super(TipoControllo.class);
    }
}
