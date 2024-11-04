package it.dmi.data.api.repositories.impl;

import it.dmi.data.api.repositories.ARepository;
import it.dmi.data.entities.FonteDati;
import jakarta.ejb.Stateless;

@Stateless
public class FonteDatiRP extends ARepository<FonteDati> {

    public FonteDatiRP() {
        super(FonteDati.class);
    }


}
