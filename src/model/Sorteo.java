package model;

import java.time.LocalDate;
import java.util.*;

/**
 * Clase Sorteo
 * Contiene TODA la lógica de negocio y validaciones por rol.
 */
public class Sorteo {

    private String nombre;
    private String descripcion;
    private double presupuestoPorRegalo;
    private LocalDate fechaEvento;
    private EstadoSorteo estado;

    private Usuario organizador;
    private List<Usuario> usuarios;
    private Map<Usuario, Usuario> asignaciones;
    private List<RestriccionAsignacion> restricciones;

    private Random random;

    public Sorteo(String nombre, String descripcion, double presupuesto,
                  LocalDate fechaEvento, Usuario organizador,EstadoSorteo estado) {

        if (organizador.getTipo() != TipoUsuario.ORGANIZADOR) {
            throw new IllegalArgumentException(
                    "Solo un organizador puede crear un sorteo."
            );
        }

        this.nombre = nombre;
        this.descripcion = descripcion;
        this.presupuestoPorRegalo = presupuesto;
        this.fechaEvento = fechaEvento;
        this.organizador = organizador;
        this.estado = EstadoSorteo.CREADO;

        this.usuarios = new ArrayList<>();
        this.asignaciones = new LinkedHashMap<>();
        this.restricciones = new ArrayList<>();
        this.random = new Random();

        usuarios.add(organizador);
    }

    // -------------------------------
    // Validación de permisos
    // -------------------------------

    private void validarOrganizador(Usuario usuario) {
        if (usuario.getTipo() != TipoUsuario.ORGANIZADOR) {
            throw new SecurityException(
                    "Acción permitida solo para el organizador."
            );
        }
    }

    // -------------------------------
    // Gestión de participantes
    // -------------------------------

    public void agregarUsuario(Usuario accionador, Usuario nuevo) {
        validarOrganizador(accionador);

        for (Usuario u : usuarios) {
            if (u.getCorreo().equals(nuevo.getCorreo())) {
                throw new IllegalArgumentException("Usuario duplicado.");
            }
        }
        usuarios.add(nuevo);
    }

    public void modificarUsuario(Usuario accionador,
                                 String correo, String nuevoNombre) {
        validarOrganizador(accionador);
        buscarUsuario(correo).modificarNombre(nuevoNombre);
    }

    public void eliminarUsuario(Usuario accionador, String correo) {
        validarOrganizador(accionador);
        usuarios.remove(buscarUsuario(correo));
    }

    private Usuario buscarUsuario(String correo) {
        return usuarios.stream()
                .filter(u -> u.getCorreo().equals(correo))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("Usuario no encontrado"));
    }

    // -------------------------------
    // Restricciones
    // -------------------------------

    public void agregarRestriccion(Usuario accionador,
                                   Usuario origen, Usuario destino) {
        validarOrganizador(accionador);
        restricciones.add(new RestriccionAsignacion(origen, destino));
    }

    // -------------------------------
    // Ejecución del sorteo
    // -------------------------------

    public void ejecutarSorteo(Usuario accionador) {
        validarOrganizador(accionador);

        List<Usuario> participantes = usuarios.stream()
                .filter(u -> u.getTipo() == TipoUsuario.PARTICIPANTE)
                .toList();

        if (participantes.size() < 2) {
            throw new IllegalStateException(
                    "Se requieren al menos dos participantes."
            );
        }

        boolean valido;
        do {
            asignaciones.clear();
            List<Usuario> receptores = new ArrayList<>(participantes);
            Collections.shuffle(receptores, random);
            valido = true;

            for (int i = 0; i < participantes.size(); i++) {
                Usuario d = participantes.get(i);
                Usuario r = receptores.get(i);

                if (d == r || violaRestriccion(d, r)) {
                    valido = false;
                    break;
                }
                asignaciones.put(d, r);
            }

        } while (!valido);

        estado = EstadoSorteo.SORTEADO;
    }

    private boolean violaRestriccion(Usuario d, Usuario r) {
        return restricciones.stream().anyMatch(res -> res.aplica(d, r));
    }

    // -------------------------------
    // Consultas
    // -------------------------------

public void consultarParticipantes() {
    List<Usuario> participantes = usuarios.stream()
            .filter(u -> u.getTipo() == TipoUsuario.PARTICIPANTE)
            .toList();

    if (participantes.isEmpty()) {
        System.out.println("Aún no hay participantes registrados.");
        return;
    }

    System.out.println("[Participantes]");

    for (int i = 0; i < participantes.size(); i++) {
        Usuario u = participantes.get(i);
        System.out.println((i + 1) + ". " + u.getNombre());
    }
}


    public EstadoSorteo getEstado() {
        return estado;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }
}