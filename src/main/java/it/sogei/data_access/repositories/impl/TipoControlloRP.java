package it.sogei.data_access.repositories.impl;

import it.sogei.data_access.repositories.ARepository;
import it.sogei.structure.data.entities.TipoControllo;
import jakarta.ejb.Stateless;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

@Stateless
public class TipoControlloRP extends ARepository {

    Class<TipoControllo> entity = TipoControllo.class;

    public TipoControlloRP() {
        super();
    }

    public void save(TipoControllo tipoControllo) {
        super.getEm().persist(tipoControllo);
    }

    public TipoControllo findByID(Long id) {
        return super.getEm().find(entity, id);
    }

    public List<TipoControllo> findAll() {
        CriteriaBuilder builder = super.getEm().getCriteriaBuilder();
        CriteriaQuery<TipoControllo> query = builder.createQuery(entity);
        Root<TipoControllo> rootEntry = query.from(entity);
        query.select(rootEntry);
        return super.getEm().createQuery(query).getResultList();
    }

    public void update(TipoControllo tipoControllo) {
        super.getEm().merge(tipoControllo);
    }

    public void delete(Long id) {
        TipoControllo tipoControllo = findByID(id);
        if (tipoControllo != null) {
            super.getEm().remove(tipoControllo);
        }
    }

}
