package it.dmi.data.api.service;

import it.dmi.data.api.repositories.impl.TipoControlloRP;
import it.dmi.data.dto.TipoControlloDTO;
import it.dmi.data.entities.TipoControllo;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@SuppressWarnings("unused")
@Stateless
public class TipoControlloService {

    @Inject
    private TipoControlloRP repository;

    private boolean create(TipoControllo tipoControllo) {
        if (tipoControllo == null) return false;
        if (repository.findByID(tipoControllo.getId()) != null) return false;
        return repository.save(tipoControllo) != null;
    }

    public TipoControlloDTO createOrFind(@NotNull TipoControlloDTO dto) {
        if(create(dto.toEntity())) return dto;
        else return null;
    }

    public TipoControllo getByID(Long id) {
        return repository.findByID(id);
    }

    public void update(TipoControllo tipoControllo) {
        repository.update(tipoControllo);
    }

    public void delete(Long id) {
        repository.delete(id);
    }

    public List<TipoControllo> getAll() {
        return repository.findAll();
    }
}
