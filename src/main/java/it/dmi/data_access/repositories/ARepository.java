package it.dmi.data_access.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.Getter;

@Getter
public abstract class ARepository {

    @PersistenceContext(unitName = "default")
    private EntityManager em;
}
