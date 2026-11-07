package config;

import java.sql.Connection;
import java.sql.SQLException;

public class TestConexion {
    public static void main(String[] args) {
        ConnectionProvider conexiones = new DbConnectionProvider();
        try (Connection conn = conexiones.getConnection()) {
            System.out.println("✅ Conexión exitosa a MySQL!");
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar: " + e.getMessage());
        }
    }
}
