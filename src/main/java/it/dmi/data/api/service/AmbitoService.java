package it.dmi.data.api.service;

import it.dmi.data.api.repositories.impl.AmbitoRP;
import it.dmi.data.entities.Ambito;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@SuppressWarnings("unused")
@Stateless
public class AmbitoService {

    @Inject
    private AmbitoRP repository;

    public void create(Ambito entity) {
        repository.save(entity);
    }

    public Ambito getByID (Long id) {
        return repository.findByID(id);
    }

    public void update (Ambito entity) {
        repository.update(entity);
    }

    public void delete (Long id) {
        repository.delete(id);
    }

    public List<Ambito> getAll () {
        return repository.findAll();
    }
}
