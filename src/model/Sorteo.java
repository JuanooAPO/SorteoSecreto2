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

    /**
     * Contrato: constructor Sorteo
     *
     * Descripción:
     * Construye un nuevo sorteo con la información básica suministrada
     * por el organizador. Inicializa el estado en CREADO, las listas
     * de usuarios, asignaciones y restricciones, y registra al organizador
     * dentro de la colección de usuarios del sorteo.
     *
     * Entradas:
     * - nombre : String
     * - descripcion : String
     * - presupuesto : double
     * - fechaEvento : LocalDate
     * - organizador : Usuario
     *
     * Salidas:
     * - nuevo objeto Sorteo inicializado
     * - excepción si el usuario no tiene rol de organizador
     *
     * Ejemplo de ejecución:
     * Entrada:
     * nombre = "Navidad 2026"
     * descripcion = "Intercambio familiar"
     * presupuesto = 50000
     * fechaEvento = 2026-12-20
     * organizador = Usuario("admin@correo.com", "Administrador", ORGANIZADOR)
     *
     * Salida:
     * Se crea un sorteo con estado CREADO
     */
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

    /**
     * Contrato: validarOrganizador
     *
     * Descripción:
     * Verifica que el usuario que ejecuta una acción tenga rol
     * de organizador. Si no cumple esta condición, la operación
     * se rechaza mediante una excepción.
     *
     * Entradas:
     * - usuario : Usuario
     *
     * Salidas:
     * - validación exitosa si el usuario es organizador
     * - excepción SecurityException si no tiene permisos
     *
     * Ejemplo de ejecución:
     * Entrada:
     * usuario = participante@correo.com
     *
     * Salida:
     * SecurityException: "Acción permitida solo para el organizador."
     */
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

    /**
     * Contrato: agregarUsuario
     *
     * Descripción:
     * Permite al organizador agregar un nuevo usuario al sorteo.
     * Valida que no exista ya otro usuario con el mismo correo
     * electrónico dentro del mismo sorteo.
     *
     * Entradas:
     * - accionador : Usuario
     * - nuevo : Usuario
     *
     * Salidas:
     * - nuevo usuario agregado a la lista de usuarios
     * - excepción si el usuario es duplicado o si no tiene permisos
     *
     * Ejemplo de ejecución:
     * Entrada:
     * accionador = organizador
     * nuevo = Usuario("juan@correo.com", "Juan", PARTICIPANTE)
     *
     * Salida:
     * Usuario agregado correctamente al sorteo
     */
    public void agregarUsuario(Usuario accionador, Usuario nuevo) {
        validarOrganizador(accionador);

        for (Usuario u : usuarios) {
            if (u.getCorreo().equals(nuevo.getCorreo())) {
                throw new IllegalArgumentException("Usuario duplicado.");
            }
        }
        usuarios.add(nuevo);
    }

    /**
     * Contrato: modificarUsuario
     *
     * Descripción:
     * Permite al organizador modificar la información de un participante
     * del sorteo, cambiando su correo y su nombre. Valida que el usuario
     * exista, que sea de tipo participante y que el nuevo correo no se
     * repita dentro del sorteo.
     *
     * Entradas:
     * - accionador : Usuario
     * - correoActual : String
     * - nuevoCorreo : String
     * - nuevoNombre : String
     *
     * Salidas:
     * - participante actualizado
     * - excepción si no existe, si no es participante, si el correo está
     *   duplicado o si no hay permisos
     *
     * Ejemplo de ejecución:
     * Entrada:
     * correoActual = "juan@correo.com"
     * nuevoCorreo = "juan.nuevo@correo.com"
     * nuevoNombre = "Juan Andrés"
     *
     * Salida:
     * Participante modificado correctamente
     */
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

    /**
     * Contrato: eliminarUsuario
     *
     * Descripción:
     * Permite al organizador eliminar un participante del sorteo.
     * Valida que el usuario exista y que corresponda a un participante.
     *
     * Entradas:
     * - accionador : Usuario
     * - correo : String
     *
     * Salidas:
     * - participante eliminado de la lista de usuarios
     * - excepción si no existe, si no es participante o si no hay permisos
     *
     * Ejemplo de ejecución:
     * Entrada:
     * correo = "laura@correo.com"
     *
     * Salida:
     * Participante eliminado correctamente
     */
    public void eliminarUsuario(Usuario accionador, String correo) {
        validarOrganizador(accionador);

        Usuario participante = buscarUsuario(correo);

        if (participante.getTipo() != TipoUsuario.PARTICIPANTE) {
            throw new IllegalArgumentException("Solo se pueden eliminar participantes.");
        }

        usuarios.remove(participante);
    }

    /**
     * Contrato: buscarUsuario
     *
     * Descripción:
     * Busca dentro del sorteo un usuario cuyo correo coincida con el
     * valor suministrado.
     *
     * Entradas:
     * - correo : String
     *
     * Salidas:
     * - usuarioEncontrado : Usuario
     * - excepción si no existe un usuario con ese correo
     *
     * Ejemplo de ejecución:
     * Entrada:
     * correo = "juan@correo.com"
     *
     * Salida:
     * Retorna el objeto Usuario correspondiente
     */
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

    /**
     * Contrato: agregarRestriccion
     *
     * Descripción:
     * Permite al organizador registrar una restricción de asignación
     * entre dos participantes, indicando que el participante origen
     * no puede sacar al participante destino.
     *
     * Entradas:
     * - accionador : Usuario
     * - origen : Usuario
     * - destino : Usuario
     *
     * Salidas:
     * - nueva restricción agregada a la lista
     * - excepción si la restricción ya existe o si no hay permisos
     *
     * Ejemplo de ejecución:
     * Entrada:
     * origen = Andrés
     * destino = Juana
     *
     * Salida:
     * Restricción registrada correctamente
     */
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

    /**
     * Contrato: ejecutarSorteo
     *
     * Descripción:
     * Ejecuta el sorteo de amigo secreto generando asignaciones
     * aleatorias entre los participantes. Valida que quien ejecuta
     * la acción sea el organizador y que existan al menos dos
     * participantes. Además, garantiza que ningún participante quede
     * asignado a sí mismo y que no se violen restricciones registradas.
     *
     * Entradas:
     * - accionador : Usuario
     *
     * Salidas:
     * - asignaciones : Map<Usuario, Usuario>
     * - estado actualizado del sorteo : EstadoSorteo
     * - excepción si no hay suficientes participantes o no hay permisos
     *
     * Ejemplo de ejecución:
     * Entrada:
     * accionador = organizador
     *
     * Salida:
     * Sorteo ejecutado correctamente. Estado = SORTEADO
     */
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

    /**
     * Contrato: modificarSorteo
     *
     * Descripción:
     * Permite al organizador modificar los datos básicos del sorteo
     * siempre que este no haya sido ejecutado previamente.
     *
     * Entradas:
     * - accionador : Usuario
     * - nuevoNombre : String
     * - nuevaDescripcion : String
     * - nuevoPresupuesto : double
     * - nuevaFecha : LocalDate
     *
     * Salidas:
     * - sorteo con datos actualizados
     * - excepción si el sorteo ya fue ejecutado o no hay permisos
     *
     * Ejemplo de ejecución:
     * Entrada:
     * nuevoNombre = "Navidad Familiar 2026"
     * nuevaDescripcion = "Intercambio familiar"
     * nuevoPresupuesto = 70000
     * nuevaFecha = 2026-12-24
     *
     * Salida:
     * Sorteo modificado correctamente
     */
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

    /**
     * Contrato: clonarSorteo
     *
     * Descripción:
     * Genera una copia del sorteo actual con un nuevo nombre,
     * conservando descripción, presupuesto, fecha, organizador
     * y participantes.
     *
     * Entradas:
     * - accionador : Usuario
     * - nuevoNombre : String
     *
     * Salidas:
     * - clon : Sorteo
     * - excepción si no hay permisos
     *
     * Ejemplo de ejecución:
     * Entrada:
     * nuevoNombre = "Navidad 2027"
     *
     * Salida:
     * Retorna un nuevo sorteo clonado con los mismos participantes
     */
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

    /**
     * Contrato: anularSorteo
     *
     * Descripción:
     * Permite al organizador cambiar el estado del sorteo a ANULADO.
     * Si el sorteo ya se encuentra anulado, se genera una excepción.
     *
     * Entradas:
     * - accionador : Usuario
     *
     * Salidas:
     * - estado actualizado del sorteo : EstadoSorteo
     * - excepción si ya estaba anulado o no hay permisos
     *
     * Ejemplo de ejecución:
     * Entrada:
     * accionador = organizador
     *
     * Salida:
     * Estado = ANULADO
     */
    public void anularSorteo(Usuario accionador) {
        validarOrganizador(accionador);

        if (estado == EstadoSorteo.ANULADO) {
            throw new IllegalStateException("El sorteo ya se encuentra anulado.");
        }

        estado = EstadoSorteo.ANULADO;
    }

    /**
     * Contrato: violaRestriccion
     *
     * Descripción:
     * Verifica si una posible asignación entre donante y receptor
     * incumple alguna de las restricciones registradas para el sorteo.
     *
     * Entradas:
     * - d : Usuario
     * - r : Usuario
     *
     * Salidas:
     * - viola : boolean
     *
     * Ejemplo de ejecución:
     * Entrada:
     * d = Andrés
     * r = Juana
     *
     * Salida:
     * true, si existe restricción entre ambos
     */
    private boolean violaRestriccion(Usuario d, Usuario r) {
        return restricciones.stream().anyMatch(res -> res.aplica(d, r));
    }

    // -------------------------------
    // Consultas
    // -------------------------------

    /**
     * Contrato: consultarParticipantes
     *
     * Descripción:
     * Muestra en consola la lista de participantes registrados
     * en el sorteo, enumerados en orden.
     *
     * Entradas:
     * - participantes : List<Usuario>
     *
     * Salidas:
     * - listado de participantes : String
     * - mensaje si no existen participantes : String
     *
     * Ejemplo de ejecución:
     * Salida:
     * [Participantes]
     * 1. Juan
     * 2. Laura
     */
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

    /**
     * Contrato: getParticipantes
     *
     * Descripción:
     * Retorna la lista de usuarios del sorteo cuyo tipo corresponde
     * a PARTICIPANTE.
     *
     * Entradas:
     * - usuarios : List<Usuario>
     *
     * Salidas:
     * - participantes : List<Usuario>
     *
     * Ejemplo de ejecución:
     * Salida:
     * Lista con todos los participantes del sorteo
     */
    public List<Usuario> getParticipantes() {
        List<Usuario> participantes = new ArrayList<>();

        for (Usuario u : usuarios) {
            if (u.getTipo() == TipoUsuario.PARTICIPANTE) {
                participantes.add(u);
            }
        }

        return participantes;
    }

    /**
     * Contrato: mostrarResumen
     *
     * Descripción:
     * Muestra en consola un resumen general del sorteo, incluyendo
     * nombre, descripción, presupuesto, fecha, estado y, si ya fue
     * ejecutado, las asignaciones resultantes.
     *
     * Entradas:
     * - nombre : String
     * - descripcion : String
     * - presupuestoPorRegalo : double
     * - fechaEvento : LocalDate
     * - estado : EstadoSorteo
     * - asignaciones : Map<Usuario, Usuario>
     *
     * Salidas:
     * - resumen del sorteo : String
     * - mensaje de error o advertencia si aún no hay asignaciones
     *
     * Ejemplo de ejecución:
     * Salida:
     * [Resumen del sorteo]
     * Nombre: Navidad 2026
     * Estado: SORTEADO
     * [Asignaciones]
     * Juan → Laura
     */
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

    /**
     * Contrato: getEstado
     *
     * Descripción:
     * Retorna el estado actual del sorteo.
     *
     * Entradas:
     * - No recibe parámetros.
     *
     * Salidas:
     * - estado : EstadoSorteo
     *
     * Ejemplo de ejecución:
     * Salida:
     * CREADO
     */
    public EstadoSorteo getEstado() {
        return estado;
    }

    /**
     * Contrato: getUsuarios
     *
     * Descripción:
     * Retorna la lista completa de usuarios asociados al sorteo,
     * incluyendo organizador y participantes.
     *
     * Entradas:
     * - No recibe parámetros.
     *
     * Salidas:
     * - usuarios : List<Usuario>
     *
     * Ejemplo de ejecución:
     * Salida:
     * Lista de usuarios del sorteo
     */
    public List<Usuario> getUsuarios() {
        return usuarios;
    }

    /**
     * Contrato: getNombre
     *
     * Descripción:
     * Retorna el nombre actual del sorteo.
     *
     * Entradas:
     * - No recibe parámetros.
     *
     * Salidas:
     * - nombre : String
     *
     * Ejemplo de ejecución:
     * Salida:
     * "Navidad 2026"
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Contrato: getDescripcion
     *
     * Descripción:
     * Retorna la descripción actual del sorteo.
     *
     * Entradas:
     * - No recibe parámetros.
     *
     * Salidas:
     * - descripcion : String
     *
     * Ejemplo de ejecución:
     * Salida:
     * "Intercambio familiar"
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Contrato: getPresupuestoPorRegalo
     *
     * Descripción:
     * Retorna el presupuesto sugerido por regalo del sorteo.
     *
     * Entradas:
     * - No recibe parámetros.
     *
     * Salidas:
     * - presupuestoPorRegalo : double
     *
     * Ejemplo de ejecución:
     * Salida:
     * 50000.0
     */
    public double getPresupuestoPorRegalo() {
        return presupuestoPorRegalo;
    }

    /**
     * Contrato: getFechaEvento
     *
     * Descripción:
     * Retorna la fecha programada para el evento del sorteo.
     *
     * Entradas:
     * - No recibe parámetros.
     *
     * Salidas:
     * - fechaEvento : LocalDate
     *
     * Ejemplo de ejecución:
     * Salida:
     * 2026-12-20
     */
    public LocalDate getFechaEvento() {
        return fechaEvento;
    }
}