package it.dmi.data.api.repositories.impl;

import it.dmi.data.api.repositories.ARepository;
import it.dmi.data.entities.Output;
import jakarta.ejb.Stateless;

@Stateless
public class OutputRP extends ARepository<Output> {

    public OutputRP() {
        super(Output.class);
    }
}
