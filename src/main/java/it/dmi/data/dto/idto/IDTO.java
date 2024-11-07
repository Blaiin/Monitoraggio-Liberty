package it.dmi.data.dto.idto;

/**
 *
 * @param <V> DTO
 * @param <T> Entity class
 */
@SuppressWarnings("unused")
public interface IDTO<V, T> {

    V fromEntity(T entity);

    T toEntity();
}
