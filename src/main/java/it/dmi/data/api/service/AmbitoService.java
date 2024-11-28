package it.dmi.data.api.service;

import it.dmi.data.api.repositories.impl.AmbitoRP;
import it.dmi.data.dto.AmbitoDTO;
import it.dmi.data.entities.Ambito;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
@Stateless
public class AmbitoService {

    @Inject
    private AmbitoRP repository;

    private boolean create(Ambito ambito) {
        if (ambito == null) return false;
        if (repository.findByID(ambito.getId()) != null) return false;
        return repository.save(ambito) != null;
    }

    public AmbitoDTO createOrFind(@NotNull AmbitoDTO dto) {
        if (create(dto.toEntity())) return dto;
        else return null;
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
