package it.dmi.data.api.service;

import it.dmi.data.api.repositories.impl.AzioneRP;
import it.dmi.data.entities.task.Azione;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.Comparator;
import java.util.List;

@Stateless
public class AzioneService {

    @Inject
    private AzioneRP repository;

    public void create(Azione azione) {
        repository.save(azione);
    }

    public Azione getByID(Long id) {
        return repository.findByID(id);
    }

    public void update(Azione azione) {
        repository.update(azione);
    }

    public void delete(Long id) {
        repository.delete(id);
    }

    public List<Azione> getAll() {
        return repository.findAll();
    }

    public List<Azione> getAllOrdered() {
        return repository.findAll().stream().sorted(Comparator.comparingInt(Azione::getOrdineAzione)).toList();
    }
}
