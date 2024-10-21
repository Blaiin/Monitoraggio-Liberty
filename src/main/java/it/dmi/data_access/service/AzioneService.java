package it.dmi.data_access.service;

import it.dmi.data_access.repositories.impl.AzioneRP;
import it.dmi.structure.data.entities.Azione;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class AzioneService {

    @Inject
    private AzioneRP repository;

    public void create (Azione azione) {
        repository.save(azione);
    }

    public Azione getByID (Long id) {
        return repository.findByID(id);
    }

    public void update (Azione azione) {
        repository.update(azione);
    }

    public void delete (Long id) {
        repository.delete(id);
    }

    public List<Azione> getAll () {
        return repository.findAll();
    }
}
