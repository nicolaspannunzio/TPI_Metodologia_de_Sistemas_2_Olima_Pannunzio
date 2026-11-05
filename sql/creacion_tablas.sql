create database IF not exists `tpi-bd-i`;
USE `tpi-bd-i`;

CREATE TABLE credencialesAcceso (
    id_credencialAcceso MEDIUMINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    eliminado BOOLEAN NOT NULL,
    hashPassword VARCHAR(255) NOT NULL,
    salt VARCHAR(64) NOT NULL,
    ultimoCambio DATETIME,
    requiereReset BOOLEAN NOT NULL
);

CREATE TABLE usuarios (
    id MEDIUMINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    eliminado BOOLEAN NOT NULL,
    username VARCHAR(30) NOT NULL UNIQUE,
    email VARCHAR(120) NOT NULL UNIQUE,
    activo BOOLEAN NOT NULL,
    fechaRegistro DATETIME NOT NULL,
    id_credencialAcceso MEDIUMINT UNSIGNED UNIQUE,
    CONSTRAINT fk_id_credAcceso FOREIGN KEY (id_credencialAcceso)
        REFERENCES credencialesAcceso (id_credencialAcceso)
);

/* TRIGGERS PARA EVITAR QUE SE INSERTEN EMAILS SIN '@'*/

DELIMITER $$
CREATE DEFINER=`root`@`localhost` TRIGGER `tpi-bd-i`.`usuario_BEFORE_INSERT` 
BEFORE INSERT ON `usuarios`
FOR EACH ROW
BEGIN
  IF INSTR(NEW.email, '@') = 0 THEN 
    SIGNAL SQLSTATE '45000' 
      SET MESSAGE_TEXT = 'El email debe contener @';
  END IF;	
END$$
DELIMITER ;

/* TRIGGERS PARA EVITAR UPDATE DE EMAILS SIN '@'*/
DELIMITER $$
CREATE DEFINER=`root`@`localhost` TRIGGER `tpi-bd-i`.`usuario_BEFORE_UPDATE` 
BEFORE UPDATE ON `usuarios`
FOR EACH ROW
BEGIN
  IF INSTR(NEW.email, '@') = 0 THEN 
    SIGNAL SQLSTATE '45000' 
      SET MESSAGE_TEXT = 'El email debe contener @';
  END IF;	
END$$
DELIMITER ;