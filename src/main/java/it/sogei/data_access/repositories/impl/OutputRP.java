package it.sogei.data_access.repositories.impl;

import it.sogei.data_access.repositories.ARepository;
import it.sogei.structure.data.entities.Output;
import jakarta.ejb.Stateless;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

@Stateless
public class OutputRP extends ARepository {

    Class<Output> entity = Output.class;

    public OutputRP() {
        super();
    }

    public void save(Output output) {
        super.getEm().persist(output);
    }

    public Output findByID(Long id) {
        return super.getEm().find(entity, id);
    }

    public List<Output> findAll() {
        CriteriaBuilder builder = super.getEm().getCriteriaBuilder();
        CriteriaQuery<Output> query = builder.createQuery(entity);
        Root<Output> rootEntry = query.from(entity);
        query.select(rootEntry);
        return super.getEm().createQuery(query).getResultList();
    }

    public void update(Output output) {
        super.getEm().merge(output);
    }

    public void delete(Long id) {
        Output output = findByID(id);
        if (output != null) {
            super.getEm().remove(output);
        }
    }
}
