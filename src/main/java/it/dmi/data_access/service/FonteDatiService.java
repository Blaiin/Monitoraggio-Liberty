package it.dmi.data_access.service;

import it.dmi.data_access.repositories.impl.FonteDatiRP;
import it.dmi.structure.data.entities.FonteDati;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

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
