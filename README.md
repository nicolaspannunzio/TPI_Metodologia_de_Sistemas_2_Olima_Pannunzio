# Gestión de Usuarios y Credenciales — TPI Metodología de Sistemas II

Trabajo Práctico Integrador de Metodología de Sistemas II (UTN — Tecnicatura Universitaria en Programación a Distancia).

El objetivo del trabajo es analizar y mejorar la calidad interna de un sistema existente mediante diagnóstico de deuda técnica, refactoring seguro y code review, conservando su comportamiento externo.

## Origen del sistema base

Este repositorio parte del proyecto integrador de Programación II:

- Repositorio original: https://github.com/nicolima200/UTN-Programacion2-Integrador
- Commit de partida: `6beb09b`
- Autores del proyecto original: Nicolás Olima y Leonel Mercorelli
- El análisis, refactoring y documentación realizados para Metodología de Sistemas II son de autoría de **NICOLÁS OLIMA** y **NICOLÁS PANNUNZIO**.

Sobre esa versión se incorporaron los scripts SQL de creación de la base y de datos de prueba (que no estaban en el repositorio original), unificando los nombres de las tablas con los utilizados en el código.

El tag `estado-inicial` identifica la versión base del sistema, sin refactorizar.

## Descripción

Aplicación de consola en Java que permite gestionar usuarios y sus credenciales de acceso sobre una base de datos MySQL:

- Crear usuario
- Listar usuarios
- Buscar usuario por ID
- Actualizar usuario
- Eliminar usuario (baja lógica)

## Tecnologías

- Java 24 (mínimo Java 15)
- MySQL
- JDBC con MySQL Connector/J 9.4.0 (incluido en `Lib/`)

## Instalación y uso

1. Ejecutar los scripts de la carpeta `sql/` en este orden:
   1. `creacion_tablas.sql`
   2. `datos_prueba.sql`
2. Configurar la contraseña del usuario `root` de MySQL en `src/config/DatabaseConnection.java` (constante `PASSWORD`).
3. Agregar `Lib/mysql-connector-j-9.4.0/mysql-connector-j-9.4.0.jar` como dependencia del proyecto.
4. Ejecutar `src/main/Main.java`.