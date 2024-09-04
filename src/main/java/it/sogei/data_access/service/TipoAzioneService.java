package it.sogei.data_access.service;

import it.sogei.data_access.repositories.impl.TipoAzioneRP;
import it.sogei.structure.data.entities.TipoAzione;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class TipoAzioneService {

    @Inject
    private TipoAzioneRP repository;

    public void create (TipoAzione tipoAzione) {
        repository.save(tipoAzione);
    }

    public TipoAzione getByID (Long id) {
        return repository.findByID(id);
    }

    public void update (TipoAzione tipoAzione) {
        repository.update(tipoAzione);
    }

    public void delete (Long id) {
        repository.delete(id);
    }

    public List<TipoAzione> getAll () {
        return repository.findAll();
    }
}
