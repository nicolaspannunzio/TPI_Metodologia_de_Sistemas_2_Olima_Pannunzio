package dao;

import entities.CredencialAcceso;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CredencialAccesoDao implements GenericDao<CredencialAcceso> {

    private static final String COLUMNAS =
            "id_credencialAcceso, hashPassword, salt, ultimoCambio, requiereReset, eliminado";

    private final Connection conexion;

    public CredencialAccesoDao(Connection conexion) {
        this.conexion = conexion;
    }

    @Override
    public void crear(CredencialAcceso credencial) throws SQLException {
        String sql = "INSERT INTO credencialesAcceso (hashPassword, salt, ultimoCambio, requiereReset, eliminado) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, credencial.getHashPassword());
            ps.setString(2, credencial.getSalt());
            ps.setTimestamp(3, Timestamp.valueOf(credencial.getUltimoCambio()));
            ps.setBoolean(4, credencial.isRequiereReset());
            ps.setBoolean(5, credencial.isEliminado());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) credencial.setId(rs.getLong(1));
            }
        }
    }

    @Override
    public CredencialAcceso leer(long id) throws SQLException {
        String sql = "SELECT " + COLUMNAS + " FROM credencialesAcceso WHERE id_CredencialAcceso = ? AND eliminado = false";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearCredencial(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<CredencialAcceso> leerTodos() throws SQLException {
        List<CredencialAcceso> lista = new ArrayList<>();
        String sql = "SELECT " + COLUMNAS + " FROM credencialesAcceso WHERE eliminado = false";
        try (PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearCredencial(rs));
            }
        }
        return lista;
    }

    @Override
    public void actualizar(CredencialAcceso c) throws SQLException {
        String sql = "UPDATE credencialesAcceso SET hashPassword=?, salt=?, ultimoCambio=?, requiereReset=?, eliminado=? WHERE id_CredencialAcceso=?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setString(1, c.getHashPassword());
            ps.setString(2, c.getSalt());
            ps.setTimestamp(3, Timestamp.valueOf(c.getUltimoCambio()));
            ps.setBoolean(4, c.isRequiereReset());
            ps.setBoolean(5, c.isEliminado());
            ps.setLong(6, c.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void eliminar(long id) throws SQLException {
        String sql = "UPDATE credencialesAcceso SET eliminado = true WHERE id_CredencialAcceso = ?";
        try (PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private CredencialAcceso mapearCredencial(ResultSet rs) throws SQLException {
        CredencialAcceso c = new CredencialAcceso();
        c.setId(rs.getLong("id_credencialAcceso"));
        c.setHashPassword(rs.getString("hashPassword"));
        c.setSalt(rs.getString("salt"));
        c.setUltimoCambio(rs.getTimestamp("ultimoCambio").toLocalDateTime());
        c.setRequiereReset(rs.getBoolean("requiereReset"));
        c.setEliminado(rs.getBoolean("eliminado"));
        return c;
    }
}
