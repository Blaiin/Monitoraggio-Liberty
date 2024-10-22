package it.dmi.data.api.service;

import it.dmi.data.api.repositories.impl.ControlloRP;
import it.dmi.data.entities.Controllo;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.Comparator;
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

    public List<Controllo> getAllOrdered() {
        return getAll().stream().sorted(Comparator.comparingInt(Controllo::getOrdineControllo)).toList();
    }
}
