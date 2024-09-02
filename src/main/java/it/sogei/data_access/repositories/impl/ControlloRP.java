package it.sogei.data_access.repositories.impl;

import it.sogei.data_access.repositories.ARepository;
import it.sogei.structure.data.entities.Controllo;
import jakarta.ejb.Stateless;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

@Stateless
public class ControlloRP extends ARepository {

    Class<Controllo> entity = Controllo.class;

    public ControlloRP() {
        super();
    }

    public void save(Controllo controllo) {
        super.getEm().persist(controllo);
    }

    public Controllo findByID(Long id) {
        return super.getEm().find(entity, id);
    }

    public List<Controllo> findAll() {
        CriteriaBuilder builder = super.getEm().getCriteriaBuilder();
        CriteriaQuery<Controllo> query = builder.createQuery(entity);
        Root<Controllo> rootEntry = query.from(entity);
        query.select(rootEntry);
        return super.getEm().createQuery(query).getResultList();
    }

    public void update(Controllo controllo) {
        super.getEm().merge(controllo);
    }

    public void delete(Long id) {
        Controllo controllo = findByID(id);
        if (controllo != null) {
            super.getEm().remove(controllo);
        }
    }
}
