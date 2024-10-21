package it.dmi.data_access.repositories.impl;

import it.dmi.data_access.repositories.ARepository;
import it.dmi.structure.data.entities.Ambito;
import jakarta.ejb.Stateless;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

@Stateless
public class AmbitoRP extends ARepository {

    Class<Ambito> entity = Ambito.class;

    public AmbitoRP() {
        super();
    }

    public void save(Ambito ambito) {
        super.getEm().persist(ambito);
    }

    public Ambito findByID(Long id) {
        return super.getEm().find(entity, id);
    }

    public List<Ambito> findAll() {
        CriteriaBuilder builder = super.getEm().getCriteriaBuilder();
        CriteriaQuery<Ambito> query = builder.createQuery(entity);
        Root<Ambito> rootEntry = query.from(entity);
        query.select(rootEntry);
        return super.getEm().createQuery(query).getResultList();
    }

    public void update(Ambito ambito) {
        super.getEm().merge(ambito);
    }

    public void delete(Long id) {
        Ambito ambito = findByID(id);
        if (ambito != null) {
            super.getEm().remove(ambito);
        }
    }
}
