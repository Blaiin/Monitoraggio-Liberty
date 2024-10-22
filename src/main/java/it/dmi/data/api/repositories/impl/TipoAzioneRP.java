package it.dmi.data.api.repositories.impl;

import it.dmi.data.api.repositories.ARepository;
import it.dmi.data.entities.TipoAzione;
import jakarta.ejb.Stateless;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

@Stateless
public class TipoAzioneRP extends ARepository {

    final Class<TipoAzione> entity = TipoAzione.class;

    public TipoAzioneRP() {
        super();
    }

    public void save(TipoAzione tipoAzione) {
        super.getEm().persist(tipoAzione);
    }

    public TipoAzione findByID(Long id) {
        return super.getEm().find(entity, id);
    }

    public List<TipoAzione> findAll() {
        CriteriaBuilder builder = super.getEm().getCriteriaBuilder();
        CriteriaQuery<TipoAzione> query = builder.createQuery(entity);
        Root<TipoAzione> rootEntry = query.from(entity);
        query.select(rootEntry);
        return super.getEm().createQuery(query).getResultList();
    }

    public void update(TipoAzione tipoAzione) {
        super.getEm().merge(tipoAzione);
    }

    public void delete(Long id) {
        TipoAzione tipoAzione = findByID(id);
        if (tipoAzione != null) {
            super.getEm().remove(tipoAzione);
        }
    }
}
