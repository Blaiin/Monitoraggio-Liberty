package it.dmi.data.api.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.persistence.exceptions.EntityManagerSetupException;

import java.util.List;

@Slf4j
public abstract class ARepository<T> implements IRepository<T> {

    @PersistenceContext(unitName = "default")
    private EntityManager em;

    private final Class<T> entityClass;

    protected ARepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected EntityManager getEm() {
        if (this.em == null) throw new EntityManagerSetupException();
        return this.em;
    }

    public T save(T entity) {
        try {
            getEm().persist(entity);
            return entity;
        } catch (Exception e) {
            log.error("Error saving {}: {}", entityClass.getSimpleName(), e.getMessage(), e);
            return null;
        }
    }

    public T findByID(Long id) {
        return getEm().find(entityClass, id);
    }

    public List<T> findAll() {
        CriteriaBuilder builder = getEm().getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(entityClass);
        Root<T> rootEntry = query.from(entityClass);
        query.select(rootEntry);
        return getEm().createQuery(query).getResultList();
    }

    public T update(T entity) {
        try {
            return getEm().merge(entity);
        } catch (Exception e) {
            log.error("Error updating {}: {}", entityClass.getSimpleName(), e.getMessage(), e);
            return null;
        }
    }

    public boolean delete(Long id) {
        T entity = findByID(id);
        if (entity != null) {
            getEm().remove(entity);
            return true;
        }
        return false;
    }
}
