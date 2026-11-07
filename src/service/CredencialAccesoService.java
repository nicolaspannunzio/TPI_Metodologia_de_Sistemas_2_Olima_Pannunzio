package service;

import config.DatabaseConnection;
import dao.CredencialAccesoDao;
import entities.CredencialAcceso;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import service.exceptions.PersistenciaException;
import service.exceptions.RegistroNoEncontradoException;

public class CredencialAccesoService implements GenericService<CredencialAcceso> {

    @Override
    public void insertar(CredencialAcceso credencial) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            credencial.setUltimoCambio(LocalDateTime.now());
            new CredencialAccesoDao(conn).crear(credencial);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo crear la credencial.", e);
        }
    }

    @Override
    public CredencialAcceso getById(long id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return new CredencialAccesoDao(conn).leer(id);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo leer la credencial con id " + id + ".", e);
        }
    }

    @Override
    public List<CredencialAcceso> getAll() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return new CredencialAccesoDao(conn).leerTodos();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo listar las credenciales.", e);
        }
    }

    @Override
    public void actualizar(CredencialAcceso credencial) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            new CredencialAccesoDao(conn).actualizar(credencial);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo actualizar la credencial.", e);
        }
    }

    @Override
    public void eliminar(long id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                CredencialAccesoDao dao = new CredencialAccesoDao(conn);
                if (dao.leer(id) != null) {
                    dao.eliminar(id);
                    conn.commit();
                } else {
                    conn.rollback();
                    throw new RegistroNoEncontradoException("La credencial con id " + id + " no existe.");
                }
            } catch (SQLException e) {
                conn.rollback();
                throw new PersistenciaException("No se pudo eliminar la credencial.", e);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo eliminar la credencial.", e);
        }
    }
}
