package main;

import entities.Usuario;
import service.UsuarioService;

import java.util.List;
import service.dto.AltaUsuario;
import service.exceptions.PersistenciaException;
import service.exceptions.RegistroNoEncontradoException;


public class AppMenu {

    private static final String PROMPT_ID_BUSCAR = "\nIngrese el ID del usuario (0 para cancelar): ";
    private static final String PROMPT_ID_ACTUALIZAR = "\nIngrese el ID del usuario a actualizar: (0 para cancelar)";
    private static final String PROMPT_ID_ELIMINAR = "\nIngrese el ID del usuario a eliminar (0 para volver): ";

    private final ConsolaUI ui;
    private final UsuarioService usuarioService;

    public AppMenu(ConsolaUI ui) {
        this.ui = ui;
        this.usuarioService = new UsuarioService();
    }


    public void mostrarMenu() {
        int opcion;

        do {
            ui.mostrar("\n===== MENU PRINCIPAL =====");
            ui.mostrar("1. Crear nuevo usuario");
            ui.mostrar("2. Listar usuarios");
            ui.mostrar("3. Buscar usuario por ID");
            ui.mostrar("4. Actualizar usuario");
            ui.mostrar("5. Eliminar usuario (baja logica)");
            ui.mostrar("0. Salir");

            opcion = ui.leerEntero("Seleccione una opcion: ");

            switch (opcion) {
                case 1 -> crearUsuario();
                case 2 -> listarUsuarios();
                case 3 -> buscarUsuario();
                case 4 -> actualizarUsuario();
                case 5 -> eliminarUsuario();
                case 0 -> {
                    ui.mostrar("Saliendo...");
                    ui.pausa(2000);
                }
                default -> ui.mostrar("Opcion invalida.");
            }
        } while (opcion != 0);
    }

    private void crearUsuario() {
        ui.mostrar("\n--- Crear nuevo usuario ---");
        String username = ui.leerTexto("Username: ");
        String email = ui.leerTexto("Email: ");
        String hash = ui.leerTexto("Hash Password: ");
        String salt = ui.leerTexto("Salt: ");

        try {
            usuarioService.insertar(new AltaUsuario(username, email, hash, salt));
            ui.mostrar("Usuario creado con exito.");
        } catch (IllegalArgumentException e) {
            ui.mostrar("Error al crear usuario: " + e.getMessage());
        } catch (PersistenciaException e) {
            ui.error("Error al crear usuario: " + e.getMessage());
        }
    }

    private void listarUsuarios() {
        try {
            ui.mostrar("\n--- Lista de usuarios ---");
            List<Usuario> usuarios = usuarioService.getAll();
            if (usuarios.isEmpty()) {
                ui.mostrar("No hay usuarios registrados.");
            } else {
                for (Usuario u : usuarios) {
                    ui.mostrar(u.toString());
                }
            }
        } catch (PersistenciaException e) {
            ui.error("Error al listar usuarios: " + e.getMessage());
        }
    }

    private void buscarUsuario() {
        try {
            long id = ui.leerId(PROMPT_ID_BUSCAR);
            if (id == 0) {
                return;
            }
            Usuario u = usuarioService.getById(id);
            if (u != null) {
                ui.mostrar(u.toString());
            } else {
                ui.mostrar("Usuario no encontrado.");
            }
        } catch (PersistenciaException e) {
            ui.error("Error al buscar usuario: " + e.getMessage());
        }
    }

    private void actualizarUsuario() {
        try {
            long id = ui.leerId(PROMPT_ID_ACTUALIZAR);
            if (id == 0) {
                return;
            }
            Usuario u = usuarioService.getById(id);
            if (u == null) {
                ui.mostrar("Usuario no encontrado.");
                return;
            }

            u.setEmail(ui.leerTexto("Nuevo email: "));
            u.setActivo(Boolean.parseBoolean(ui.leerTexto("Activo? (true/false): ")));

            usuarioService.actualizarConCredencial(u);
            ui.mostrar("Usuario actualizado con exito.");
        } catch (PersistenciaException | IllegalArgumentException e) {
            ui.error("Error al actualizar usuario: " + e.getMessage());
        }
    }


    private void eliminarUsuario() {
        try {
            long id = ui.leerId(PROMPT_ID_ELIMINAR);
            if (id == 0) {
                return;
            }
            usuarioService.eliminarConCredencial(id);
            ui.mostrar("Usuario eliminado (baja logica).");
        } catch (PersistenciaException | RegistroNoEncontradoException e) {
            ui.error("Error al eliminar usuario: " + e.getMessage());
        }
    }

}
