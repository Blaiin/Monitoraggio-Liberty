package it.sogei.data_access.repositories.impl;

import it.sogei.data_access.repositories.ARepository;
import it.sogei.structure.data.entities.Configurazione;
import jakarta.ejb.Stateless;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

@Stateless
public class ConfigurazioneRP extends ARepository {

    Class<Configurazione> entity = Configurazione.class;

    public ConfigurazioneRP() {
        super();
    }

    public void save(Configurazione configurazione) {
        super.getEm().persist(configurazione);
    }

    public Configurazione findByID(Long id) {
        return super.getEm().find(entity, id);
    }

    public List<Configurazione> findAll() {
        CriteriaBuilder builder = super.getEm().getCriteriaBuilder();
        CriteriaQuery<Configurazione> query = builder.createQuery(entity);
        Root<Configurazione> rootEntry = query.from(entity);
        query.select(rootEntry);
        return super.getEm().createQuery(query).getResultList();
    }

    public void update(Configurazione configurazione) {
        super.getEm().merge(configurazione);
    }

    public void delete(Long id) {
        Configurazione configurazione = findByID(id);
        if (configurazione != null) {
            super.getEm().remove(configurazione);
        }
    }
}
