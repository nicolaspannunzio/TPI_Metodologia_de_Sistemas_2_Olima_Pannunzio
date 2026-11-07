package service.dto;

public record AltaUsuario(String username, String email, String hashPassword, String salt) {
}
