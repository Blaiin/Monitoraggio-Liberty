package it.sogei.data_access.repositories.impl;

import it.sogei.data_access.repositories.ARepository;
import it.sogei.structure.data.entities.FonteDati;
import jakarta.ejb.Stateless;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

@Stateless
public class FonteDatiRP extends ARepository {

    Class<FonteDati> entity = FonteDati.class;

    public FonteDatiRP() {
        super();
    }

    public void save(FonteDati fonteDati) {
        super.getEm().persist(fonteDati);
    }

    public FonteDati findByID(Long id) {
        return super.getEm().find(entity, id);
    }

    public List<FonteDati> findAll() {
        CriteriaBuilder builder = super.getEm().getCriteriaBuilder();
        CriteriaQuery<FonteDati> query = builder.createQuery(entity);
        Root<FonteDati> rootEntry = query.from(entity);
        query.select(rootEntry);
        return super.getEm().createQuery(query).getResultList();
    }

    public void update(FonteDati fonteDati) {
        super.getEm().merge(fonteDati);
    }

    public void delete(Long id) {
        FonteDati fonteDati = findByID(id);
        if (fonteDati != null) {
            super.getEm().remove(fonteDati);
        }
    }
}
