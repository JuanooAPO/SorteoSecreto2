package model;

/**
 * Representa un usuario del sistema.
 * El rol (tipo) determina las acciones permitidas dentro del sorteo.
 */
public class Usuario {

    private String correo;
    private String nombre;
    private TipoUsuario tipo;

    public Usuario(String correo, String nombre, TipoUsuario tipo) {
        this.correo = correo.trim().toLowerCase();
        this.nombre = nombre.trim();
        this.tipo = tipo;
    }

    public void setCorreo(String correo) {
    this.correo = correo;
    }

    public String getCorreo() {
        return correo;
    }

    public String getNombre() {
        return nombre;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void modificarNombre(String nuevoNombre) {
        this.nombre = nuevoNombre.trim();
    }

}