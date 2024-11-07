package it.dmi.data.api.service;

import it.dmi.data.api.repositories.impl.SogliaRP;
import it.dmi.data.entities.Soglia;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@SuppressWarnings("unused")
@Stateless
public class SogliaService {

    @Inject
    private SogliaRP repository;

    public void create (Soglia soglia) {
        repository.save(soglia);
    }

    public Soglia getByID (Long id) {
        return repository.findByID(id);
    }

    public void update (Soglia soglia) {
        repository.update(soglia);
    }

    public void delete (Long id) {
        repository.delete(id);
    }

    public List<Soglia> getAll () {
        return repository.findAll();
    }
}
