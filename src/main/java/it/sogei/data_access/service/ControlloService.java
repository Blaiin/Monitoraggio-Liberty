package it.sogei.data_access.service;

import it.sogei.data_access.repositories.impl.ControlloRP;
import it.sogei.structure.data.entities.Controllo;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class ControlloService {

    @Inject
    private ControlloRP repository;

    public void create (Controllo controllo) {
        repository.save(controllo);
    }

    public Controllo getByID (Long id) {
        return repository.findByID(id);
    }

    public void update (Controllo controllo) {
        repository.update(controllo);
    }

    public void delete (Long id) {
        repository.delete(id);
    }

    public List<Controllo> getAll () {
        return repository.findAll();
    }
}
