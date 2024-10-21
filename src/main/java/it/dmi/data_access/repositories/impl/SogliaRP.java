package it.dmi.data_access.repositories.impl;

import it.dmi.data_access.repositories.ARepository;
import it.dmi.structure.data.entities.Soglia;
import jakarta.ejb.Stateless;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

@Stateless
public class SogliaRP extends ARepository {
    Class<Soglia> entity = Soglia.class;

    public SogliaRP() {
        super();
    }

    public void save(Soglia soglia) {
        super.getEm().persist(soglia);
    }

    public Soglia findByID(Long id) {
        return super.getEm().find(entity, id);
    }

    public List<Soglia> findAll() {
        CriteriaBuilder builder = super.getEm().getCriteriaBuilder();
        CriteriaQuery<Soglia> query = builder.createQuery(entity);
        Root<Soglia> rootEntry = query.from(entity);
        query.select(rootEntry);
        return super.getEm().createQuery(query).getResultList();
    }

    public void update(Soglia soglia) {
        super.getEm().merge(soglia);
    }

    public void delete(Long id) {
        Soglia soglia = findByID(id);
        if (soglia != null) {
            super.getEm().remove(soglia);
        }
    }
}
