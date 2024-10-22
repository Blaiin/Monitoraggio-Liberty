package it.dmi.data.api.service;

import it.dmi.data.api.repositories.impl.SicurezzaFonteDatiRP;
import it.dmi.data.entities.SicurezzaFonteDati;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class SicurezzaFonteDatiService {

    @Inject
    private SicurezzaFonteDatiRP repository;

    public void create (SicurezzaFonteDati sfd) {
        repository.save(sfd);
    }

    public SicurezzaFonteDati getByID (Long id) {
        return repository.findByID(id);
    }

    public void update (SicurezzaFonteDati sfd) {
        repository.update(sfd);
    }

    public void delete (Long id) {
        repository.delete(id);
    }

    public List<SicurezzaFonteDati> getAll () {
        return repository.findAll();
    }
}
