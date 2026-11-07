package main;

import config.ConnectionProvider;
import config.DbConnectionProvider;
import service.UsuarioService;

public class Main {
    public static void main(String[] args) {
        ConnectionProvider conexiones = new DbConnectionProvider();
        UsuarioService usuarios = new UsuarioService(conexiones);

        AppMenu menu = new AppMenu(new ConsolaUI(), usuarios);
        menu.mostrarMenu();
    }
}
