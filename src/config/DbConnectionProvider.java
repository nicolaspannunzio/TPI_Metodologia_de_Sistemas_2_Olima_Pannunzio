package config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Provee conexiones JDBC a la base de datos.
 * Los datos de conexión se leen, en este orden de prioridad:
 *   1. Variables de entorno: DB_URL, DB_USER, DB_PASSWORD.
 *   2. Archivo db.properties (claves db.url, db.user, db.password),
 *      en la raíz del proyecto o en la ruta indicada con -Ddb.config=RUTA.
 */
public class DbConnectionProvider implements ConnectionProvider {

    private static final String ARCHIVO_POR_DEFECTO = "db.properties";

    private String url;
    private String user;
    private String password;

    @Override
    public Connection getConnection() throws SQLException {
        if (url == null) {
            cargarConfiguracion();
        }
        return DriverManager.getConnection(url, user, password);
    }

    private synchronized void cargarConfiguracion() throws SQLException {
        if (url != null) {
            return;
        }
        Properties archivo = leerArchivo();

        String urlLeida = obtener("DB_URL", "db.url", archivo);
        String userLeido = obtener("DB_USER", "db.user", archivo);
        String passwordLeida = obtener("DB_PASSWORD", "db.password", archivo);

        if (urlLeida == null || urlLeida.isBlank() || userLeido == null || userLeido.isBlank()) {
            throw new SQLException("Configuración de base de datos incompleta: faltan db.url y/o db.user. "
                    + "Copie db.properties.example como db.properties y complete los datos, "
                    + "o defina las variables de entorno DB_URL, DB_USER y DB_PASSWORD.");
        }

        user = userLeido;
        password = passwordLeida == null ? "" : passwordLeida;
        url = urlLeida;
    }

    private String obtener(String variableEntorno, String clave, Properties archivo) {
        String valor = System.getenv(variableEntorno);
        if (valor != null) {
            return valor;
        }
        return archivo.getProperty(clave);
    }

    private Properties leerArchivo() throws SQLException {
        Properties props = new Properties();
        Path ruta = Paths.get(System.getProperty("db.config", ARCHIVO_POR_DEFECTO));
        if (!Files.exists(ruta)) {
            return props;
        }
        try (InputStream in = Files.newInputStream(ruta)) {
            props.load(in);
        } catch (IOException e) {
            throw new SQLException("No se pudo leer el archivo de configuración " + ruta.toAbsolutePath(), e);
        }
        return props;
    }
}