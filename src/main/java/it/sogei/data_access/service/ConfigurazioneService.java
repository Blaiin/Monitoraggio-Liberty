package it.sogei.data_access.service;

import it.sogei.data_access.repositories.impl.ConfigurazioneRP;
import it.sogei.structure.data.entities.Configurazione;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class ConfigurazioneService {

    @Inject
    private ConfigurazioneRP repository;

    public void create (Configurazione config) {
        repository.save(config);
    }

    public Configurazione getByID (Long id) {
        return repository.findByID(id);
    }

    public void update (Configurazione config) {
        repository.update(config);
    }

    public void delete (Long id) {
        repository.delete(id);
    }

    public List<Configurazione> getAll () {
        return repository.findAll();
    }
}
