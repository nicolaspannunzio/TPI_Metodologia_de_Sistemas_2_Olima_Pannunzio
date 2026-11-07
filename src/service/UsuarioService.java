package service;

import config.DatabaseConnection;
import dao.CredencialAccesoDao;
import dao.UsuarioDao;
import entities.CredencialAcceso;
import entities.Usuario;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import service.dto.AltaUsuario;
import service.exceptions.PersistenciaException;
import service.exceptions.RegistroNoEncontradoException;

public class UsuarioService implements GenericService<Usuario> {

    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_.-]{4,30}$";
    private static final String EMAIL_REGEX = "^(?=.{1,120}$)[\\w\\.-]+@([\\w\\-]+\\.)+[a-zA-Z]{2,4}$";

    public void insertar(AltaUsuario alta) {
        CredencialAcceso credencial = new CredencialAcceso();
        credencial.setHashPassword(alta.hashPassword());
        credencial.setSalt(alta.salt());
        credencial.setUltimoCambio(LocalDateTime.now());
        credencial.setRequiereReset(true);
        credencial.setEliminado(false);

        Usuario usuario = new Usuario();
        usuario.setUsername(alta.username());
        usuario.setEmail(alta.email());
        usuario.setActivo(true);
        usuario.setFechaRegistro(LocalDateTime.now());
        usuario.setEliminado(false);
        usuario.setCredencial(credencial);

        insertar(usuario);
    }

    @Override
    public void insertar(Usuario usuario) {
        validarEmail(usuario.getEmail());
        validarUsername(usuario.getUsername());

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                CredencialAccesoDao credencialDao = new CredencialAccesoDao(conn);
                UsuarioDao usuarioDao = new UsuarioDao(conn);

                CredencialAcceso credencial = usuario.getCredencial();
                credencial.setUltimoCambio(LocalDateTime.now());
                credencialDao.crear(credencial);

                usuario.setFechaRegistro(LocalDateTime.now());
                usuarioDao.crear(usuario);

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new PersistenciaException("No se pudo crear el usuario.", e);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo crear el usuario.", e);
        }
    }

    @Override
    public Usuario getById(long id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return new UsuarioDao(conn).leer(id);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo leer el usuario con id " + id + ".", e);
        }
    }

    @Override
    public List<Usuario> getAll() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            return new UsuarioDao(conn).leerTodos();
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo listar los usuarios.", e);
        }
    }

    @Override
    public void actualizar(Usuario usuario) {
        validarEmail(usuario.getEmail());

        try (Connection conn = DatabaseConnection.getConnection()) {
            new UsuarioDao(conn).actualizar(usuario);
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo actualizar el usuario.", e);
        }
    }

    @Override
    public void eliminar(long id) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                UsuarioDao dao = new UsuarioDao(conn);
                if (dao.leer(id) != null) {
                    dao.eliminar(id);
                    conn.commit();
                } else {
                    conn.rollback();
                    throw new RegistroNoEncontradoException("El usuario con id " + id + " no existe.");
                }
            } catch (SQLException e) {
                conn.rollback();
                throw new PersistenciaException("No se pudo eliminar el usuario.", e);
            }
        } catch (SQLException e) {
            throw new PersistenciaException("No se pudo eliminar el usuario.", e);
        }
    }

    public static void validarUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio");
        }

        if (!username.matches(USERNAME_REGEX)) {
            throw new IllegalArgumentException("""
                                               El username solo puede contener letras, números, guiones, guiones bajos, puntos, 
                                               no debe contener espacios y debe tener entre 4 y 30 caracteres.""");
        }
    }

    public static void validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        if (!email.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("""
                                               \nEl email solo puede contener letras, números, guiones, guiones bajos y puntos,
                                               no debe contener espacios y debe tener entre 4 y 120 caracteres.""");
        }
    }
}
