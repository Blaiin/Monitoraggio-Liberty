package it.dmi.data.api.repositories.impl;

import it.dmi.data.api.repositories.ARepository;
import it.dmi.data.entities.SicurezzaFonteDati;
import jakarta.ejb.Stateless;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

@Stateless
public class SicurezzaFonteDatiRP extends ARepository {

    final Class<SicurezzaFonteDati> entity = SicurezzaFonteDati.class;

    public SicurezzaFonteDatiRP() {
        super();
    }

    public void save(SicurezzaFonteDati sicurezzaFD) {
        super.getEm().persist(sicurezzaFD);
    }

    public SicurezzaFonteDati findByID(Long id) {
        return super.getEm().find(entity, id);
    }

    public List<SicurezzaFonteDati> findAll() {
        CriteriaBuilder builder = super.getEm().getCriteriaBuilder();
        CriteriaQuery<SicurezzaFonteDati> query = builder.createQuery(entity);
        Root<SicurezzaFonteDati> rootEntry = query.from(entity);
        query.select(rootEntry);
        return super.getEm().createQuery(query).getResultList();
    }

    public void update(SicurezzaFonteDati sicurezzaFD) {
        super.getEm().merge(sicurezzaFD);
    }

    public void delete(Long id) {
        SicurezzaFonteDati sicurezzaFD = findByID(id);
        if (sicurezzaFD != null) {
            super.getEm().remove(sicurezzaFD);
        }
    }
}
