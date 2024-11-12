package it.dmi.data.api.service;

import it.dmi.data.api.repositories.impl.FonteDatiRP;
import it.dmi.data.entities.FonteDati;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@SuppressWarnings("unused")
@Stateless
public class FonteDatiService {

    @Inject
    private FonteDatiRP repository;

    public void create (FonteDati fonteDati) {
        repository.save(fonteDati);
    }

    public FonteDati getByID (Long id) {
        return repository.findByID(id);
    }

    public void update (FonteDati fonteDati) {
        repository.update(fonteDati);
    }

    public void delete (Long id) {
        repository.delete(id);
    }

    public List<FonteDati> getAll () {
        return repository.findAll();
    }
}
