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
                  LocalDate fechaEvento, Usuario organizador) {

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
                                String correoActual,
                                String nuevoCorreo,
                                String nuevoNombre) {
        validarOrganizador(accionador);

        Usuario participante = buscarUsuario(correoActual);

        if (participante.getTipo() != TipoUsuario.PARTICIPANTE) {
            throw new IllegalArgumentException("Solo se pueden modificar participantes.");
        }

        for (Usuario u : usuarios) {
            if (!u.getCorreo().equalsIgnoreCase(correoActual)
                    && u.getCorreo().equalsIgnoreCase(nuevoCorreo)) {
                throw new IllegalArgumentException("Ya existe un participante con ese correo.");
            }
        }

        participante.setCorreo(nuevoCorreo);
        participante.modificarNombre(nuevoNombre);
    }

    public void eliminarUsuario(Usuario accionador, String correo) {
        validarOrganizador(accionador);

        Usuario participante = buscarUsuario(correo);

        if (participante.getTipo() != TipoUsuario.PARTICIPANTE) {
            throw new IllegalArgumentException("Solo se pueden eliminar participantes.");
        }

    usuarios.remove(participante);
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
                                Usuario origen,
                                Usuario destino) {

        validarOrganizador(accionador);

        for (RestriccionAsignacion r : restricciones) {
            if (r.aplica(origen, destino)) {
                throw new IllegalArgumentException("La restricción ya existe.");
            }
        }

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

    public void modificarSorteo(Usuario accionador,
                                String nuevoNombre,
                                String nuevaDescripcion,
                                double nuevoPresupuesto,
                                LocalDate nuevaFecha) {
        validarOrganizador(accionador);

        if (estado == EstadoSorteo.SORTEADO) {
            throw new IllegalStateException(
                    "El sorteo ya fue ejecutado y no se puede modificar."
            );
        }

        this.nombre = nuevoNombre;
        this.descripcion = nuevaDescripcion;
        this.presupuestoPorRegalo = nuevoPresupuesto;
        this.fechaEvento = nuevaFecha;
    }


    public Sorteo clonarSorteo(Usuario accionador, String nuevoNombre) {
        validarOrganizador(accionador);

        Sorteo clon = new Sorteo(
                nuevoNombre,
                this.descripcion,
                this.presupuestoPorRegalo,
                this.fechaEvento,
                this.organizador
        );

        for (Usuario u : this.usuarios) {
            if (u.getTipo() == TipoUsuario.PARTICIPANTE) {
                Usuario copiaParticipante = new Usuario(
                        u.getCorreo(),
                        u.getNombre(),
                        TipoUsuario.PARTICIPANTE
                );
                clon.agregarUsuario(accionador, copiaParticipante);
            }
        }

        return clon;
    }


    public void anularSorteo(Usuario accionador) {
        validarOrganizador(accionador);

        if (estado == EstadoSorteo.ANULADO) {
            throw new IllegalStateException("El sorteo ya se encuentra anulado.");
        }

        estado = EstadoSorteo.ANULADO;
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




    public List<Usuario> getParticipantes() {
        List<Usuario> participantes = new ArrayList<>();

        for (Usuario u : usuarios) {
            if (u.getTipo() == TipoUsuario.PARTICIPANTE) {
                participantes.add(u);
            }
        }

        return participantes;
    }

    public void mostrarResumen() {
        System.out.println("[Resumen del sorteo]");
        System.out.println("Nombre: " + nombre);
        System.out.println("Descripción: " + descripcion);
        System.out.println("Presupuesto sugerido: " + presupuestoPorRegalo);
        System.out.println("Fecha del evento: " + fechaEvento);
        System.out.println("Estado: " + estado);

        if (estado != EstadoSorteo.SORTEADO) {
            System.out.println("Error: el sorteo aún no ha sido ejecutado.");
            return;
        }

        if (asignaciones.isEmpty()) {
            System.out.println("Advertencia: no hay asignaciones almacenadas.");
            return;
        }

        System.out.println("\n[Asignaciones]");
        for (Map.Entry<Usuario, Usuario> e : asignaciones.entrySet()) {
            System.out.println(e.getKey().getNombre() + " → " + e.getValue().getNombre());
        }
    }


    public EstadoSorteo getEstado() {
        return estado;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    public String getNombre() {
    return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPresupuestoPorRegalo() {
        return presupuestoPorRegalo;
    }

    public LocalDate getFechaEvento() {
        return fechaEvento;
    }
}