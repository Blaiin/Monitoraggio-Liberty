package it.sogei.structure.data.dto.idto;

public interface IDTO {

    <T> IDTO fromEntity(T entity);

    Object toEntity();
}
