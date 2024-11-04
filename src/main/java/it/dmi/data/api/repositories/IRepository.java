package it.dmi.data.api.repositories;

import java.util.List;

public interface IRepository<T> {

    T save(T entity);

    T findByID(Long id);

    T update(T entity);

    boolean delete(Long id);

    List<T> findAll();

}
