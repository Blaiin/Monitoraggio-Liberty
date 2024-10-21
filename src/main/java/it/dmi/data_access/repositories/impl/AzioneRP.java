package it.dmi.data_access.repositories.impl;

import it.dmi.data_access.repositories.ARepository;
import it.dmi.structure.data.entities.Azione;
import jakarta.ejb.Stateless;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

@Stateless
public class AzioneRP extends ARepository {

    private final Class<Azione> entityClass = Azione.class;

    public AzioneRP () {
        super();
    }

    public void save(Azione azione) {
        super.getEm().persist(azione);
    }

    public Azione findByID (Long id) {
        return super.getEm().find(entityClass, id);
    }

    public List<Azione> findAll() {
        CriteriaBuilder builder = super.getEm().getCriteriaBuilder();
        CriteriaQuery<Azione> query = builder.createQuery(entityClass);
        Root<Azione> rootEntry = query.from(entityClass);
        query.select(rootEntry);
        return super.getEm().createQuery(query).getResultList();
    }

    public void update(Azione azione) {
        super.getEm().merge(azione);
    }

    public void delete(Long id) {
        Azione azione = findByID(id);
        if (azione != null) {
            super.getEm().remove(azione);
        }
    }
}
