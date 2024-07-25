package it.sogei.data_access.repositories;

import it.sogei.structure.data.Config;
import jakarta.ejb.Singleton;
import jakarta.ejb.Stateless;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

@Stateless
public class ConfigRepository extends ARepository {

    public ConfigRepository() {
        super();
    }
    public void save(Config config) {
        super.getEm().persist(config);
    }

    public Config find(Long id) {
        return super.getEm().find(Config.class, id);
    }

    public List<Config> findAll() {
        CriteriaBuilder builder = super.getEm().getCriteriaBuilder();
        CriteriaQuery<Config> query = builder.createQuery(Config.class);
        Root<Config> rootEntry = query.from(Config.class);
        query.select(rootEntry);
        return super.getEm().createQuery(query).getResultList();
    }

    public void update(Config config) {
        super.getEm().merge(config);
    }

    public void delete(Long id) {
        Config config = find(id);
        if (config != null) {
            super.getEm().remove(config);
        }
    }
}
