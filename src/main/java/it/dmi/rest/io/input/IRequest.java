package it.dmi.rest.io.input;

public interface IRequest<T> {

    T toDTO();

}
