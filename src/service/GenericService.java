package service;

import java.util.List;

public interface GenericService<T> {
    void insertar(T entidad);
    T getById(long id);
    List<T> getAll();
    void actualizar(T entidad);
    void eliminar(long id);
}
