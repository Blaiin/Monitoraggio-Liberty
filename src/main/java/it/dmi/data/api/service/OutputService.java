package it.dmi.data.api.service;

import it.dmi.data.api.repositories.impl.OutputRP;
import it.dmi.data.dto.OutputDTO;
import it.dmi.data.entities.Output;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@SuppressWarnings("unused")
@Stateless
@Slf4j
public class OutputService {

    @Inject
    private OutputRP repository;

    public void create(OutputDTO output) {
        if (output == null) {
            log.error("Could not save a null output to database.");
            return;
        }
        if(output.getConfigurazioneId() == null && output.getAzioneId() == null) {
            log.error("Could not save output to database, necessary fields were invalid.");
            return;
        }
        create(output.toEntity());
    }

    private void create(Output output) {
        repository.save(output);
    }

    public Output getByID(Long id) {
        return repository.findByID(id);
    }

    public void update(Output output) {
        repository.update(output);
    }

    public void delete(Long id) {
        repository.delete(id);
    }

    public List<Output> getAll() {
        return repository.findAll();
    }
}
