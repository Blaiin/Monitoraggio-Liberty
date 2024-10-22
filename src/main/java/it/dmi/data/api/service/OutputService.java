package it.dmi.data.api.service;

import it.dmi.data.api.repositories.impl.OutputRP;
import it.dmi.data.entities.Output;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;

@Stateless
public class OutputService {

    @Inject
    private OutputRP repository;

    public void create (Output output) {
        repository.save(output);
    }

    public Output getByID (Long id) {
        return repository.findByID(id);
    }

    public void update (Output output) {
        repository.update(output);
    }

    public void delete (Long id) {
        repository.delete(id);
    }

    public List<Output> getAll () {
        return repository.findAll();
    }
}
