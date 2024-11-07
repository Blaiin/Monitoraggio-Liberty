package it.dmi.data.api.repositories.impl;

import it.dmi.data.api.repositories.ARepository;
import it.dmi.data.entities.SicurezzaFonteDati;
import jakarta.ejb.Stateless;

@SuppressWarnings("unused")
@Stateless
public class SicurezzaFonteDatiRP extends ARepository<SicurezzaFonteDati> {

    public SicurezzaFonteDatiRP() {
        super(SicurezzaFonteDati.class);
    }
}
