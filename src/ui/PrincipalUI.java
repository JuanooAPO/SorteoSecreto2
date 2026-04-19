package ui;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import model.EstadoSorteo;
import model.Sorteo;
import model.TipoUsuario;
import model.Usuario;

/**
 * Interfaz de usuario en consola.
 */
public class PrincipalUI {

    private static Scanner scanner = new Scanner(System.in);
    private static java.util.List<Sorteo> sorteos = new java.util.ArrayList<>();
    private static Usuario organizador;

    /**
     * Contrato: método main
     *
     * Descripción:
     * Es el punto de entrada del programa. Inicializa el usuario organizador
     * y controla el funcionamiento general del sistema mediante un menú
     * interactivo. El menú se repite en un ciclo hasta que el usuario
     * decide salir del programa.
     *
     * Entradas:
     * - args : String[] (argumentos de línea de comandos, no utilizados)
     * - opcion : int (opción del menú ingresada por el usuario)
     *
     * Salidas:
     * - mensajes en consola : String
     * - ejecución de los métodos del sistema según la opción elegida
     * - mensaje de despedida al finalizar : String
     *
     * Ejemplo de ejecución:
     * Entrada:
     * Elija una opción: 1
     *
     * Salida:
     * Se ejecuta el método crearSorteo()
     */
    public static void main(String[] args) {

        organizador = new Usuario(
                "admin@correo.com",
                "Administrador",
                TipoUsuario.ORGANIZADOR
        );

        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            int opcion = leerEntero("Elija una opción: ");
            System.out.println();

            switch (opcion) {
                case 1 -> crearSorteo();
                case 2 -> modificarSorteo();
                case 3 -> clonarSorteo();
                case 4 -> registrarParticipantes();
                case 5 -> modificarParticipante();
                case 6 -> borrarParticipante();
                case 7 -> consultarParticipantes();
                case 8 -> registrarRestriccion();
                case 9 -> anularSorteo();
                case 10 -> ejecutarSorteo();
                case 11 -> mostrarResumen();
                case 12 -> listarSorteos();
                case 13 -> {
                    System.out.println("¡Hasta luego!");
                    salir = true;
                }
                default -> System.out.println("Opción inválida. Intente nuevamente.");
            }
            System.out.println();
        }
    }

    /**
     * Contrato: mostrarMenu
     *
     * Descripción:
     * Muestra en la consola el menú principal del sistema con todas
     * las opciones disponibles para gestionar sorteos, participantes,
     * restricciones y consultas generales.
     *
     * Entradas:
     * - No recibe parámetros.
     *
     * Salidas:
     * - menú mostrado en consola : String
     *
     * Ejemplo de ejecución:
     * Salida:
     * ===== SorteoSecreto (ES) =====
     * 1. Registrar datos del sorteo
     * 2. Modificar sorteo
     * ...
     * 13. Salir
     * ==============================
     */
    private static void mostrarMenu() {
        System.out.println("===== SorteoSecreto (ES) =====");
        System.out.println("1. Registrar datos del sorteo");
        System.out.println("2. Modificar sorteo");
        System.out.println("3. Clonar sorteo");
        System.out.println("4. Registrar participantes");
        System.out.println("5. Modificar participante");
        System.out.println("6. Borrar participante");
        System.out.println("7. Consultar participantes");
        System.out.println("8. Registrar restricción de asignación");
        System.out.println("9. Anular sorteo");
        System.out.println("10. Generar sorteo");
        System.out.println("11. Resumen del sorteo");
        System.out.println("12. Listar sorteos");
        System.out.println("13. Salir");
        System.out.println("==============================");
    }

    /**
     * --------------------------------------------------------------------------
     * REQUERIMIENTO 1: Registrar datos del sorteo
     * --------------------------------------------------------------------------
     * Descripción del requerimiento:
     * Permite ingresar la información básica de un nuevo sorteo y registrarlo
     * en la lista general de sorteos del sistema.
     *
     * Entradas:
     * - nombre : String
     * - desc : String
     * - presupuesto : double
     * - fecha : LocalDate
     * - organizador : Usuario
     *
     * Salidas:
     * - nuevo sorteo agregado a la lista : Sorteo
     * - mensajeConfirmacion : String
     *
     * Ejemplo de ejecución:
     * Entrada:
     * Nombre del sorteo: Navidad 2026
     * Descripción: Intercambio familiar
     * Presupuesto sugerido por regalo: 50000
     * Fecha del evento (yyyy-MM-dd): 2026-12-20
     *
     * Salida:
     * Sorteo creado correctamente. Total sorteos: 1
     * --------------------------------------------------------------------------
     */
    private static void crearSorteo() {
        System.out.println("[Registrar sorteo]");
        String nombre = leerLineaNoVacia("Nombre del sorteo: ");
        String desc = leerLineaNoVacia("Descripción: ");
        double presupuesto = leerDecimal("Presupuesto sugerido por regalo: ");
        LocalDate fecha = leerFecha("Fecha del evento (yyyy-MM-dd): ");

        Sorteo nuevoSorteo = new Sorteo(
                nombre,
                desc,
                presupuesto,
                fecha,
                organizador
        );

        sorteos.add(nuevoSorteo);
        System.out.println("Sorteo creado correctamente. Total sorteos: " + sorteos.size());
    }

    /**
     * --------------------------------------------------------------------------
     * REQUERIMIENTO 2: Modificar sorteo
     * --------------------------------------------------------------------------
     * Descripción del requerimiento:
     * Permite seleccionar un sorteo existente y modificar su nombre,
     * descripción, presupuesto sugerido y fecha del evento, siempre
     * que no haya sido ejecutado previamente.
     *
     * Entradas:
     * - sorteoSeleccionado : Sorteo
     * - nuevoNombre : String
     * - nuevaDescripcion : String
     * - nuevoPresupuesto : double
     * - nuevaFecha : LocalDate
     *
     * Salidas:
     * - datos actualizados del sorteo : Sorteo
     * - mensajeConfirmacion o mensajeError : String
     *
     * Ejemplo de ejecución:
     * Entrada:
     * Seleccione el número del sorteo: 1
     * Nuevo nombre del sorteo: Navidad Familiar 2026
     * Nueva descripción: Intercambio entre familiares
     * Nuevo presupuesto sugerido por regalo: 70000
     * Nueva fecha del evento (yyyy-MM-dd): 2026-12-24
     *
     * Salida:
     * Sorteo modificado correctamente.
     * --------------------------------------------------------------------------
     */
    private static void modificarSorteo() {
        if (sorteos.isEmpty()) {
            System.out.println("Error: primero debe registrar al menos un sorteo.");
            return;
        }

        System.out.println("[Modificar sorteo]");
        Sorteo sorteoSeleccionado = seleccionarSorteo();

        if (sorteoSeleccionado == null) {
            return;
        }

        if (sorteoSeleccionado.getEstado() == EstadoSorteo.SORTEADO) {
            System.out.println("Error: el sorteo ya fue ejecutado y no se puede modificar.");
            return;
        }

        System.out.println("\nDatos actuales del sorteo:");
        System.out.println("Nombre: " + sorteoSeleccionado.getNombre());
        System.out.println("Descripción: " + sorteoSeleccionado.getDescripcion());
        System.out.println("Presupuesto sugerido: " + sorteoSeleccionado.getPresupuestoPorRegalo());
        System.out.println("Fecha del evento: " + sorteoSeleccionado.getFechaEvento());
        System.out.println();

        String nuevoNombre = leerLineaNoVacia("Nuevo nombre del sorteo: ");
        String nuevaDescripcion = leerLineaNoVacia("Nueva descripción: ");
        double nuevoPresupuesto = leerDecimal("Nuevo presupuesto sugerido por regalo: ");
        LocalDate nuevaFecha = leerFecha("Nueva fecha del evento (yyyy-MM-dd): ");

        try {
            sorteoSeleccionado.modificarSorteo(
                    organizador,
                    nuevoNombre,
                    nuevaDescripcion,
                    nuevoPresupuesto,
                    nuevaFecha
            );
            System.out.println("Sorteo modificado correctamente.");
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("Error de permisos: " + e.getMessage());
        }
    }

    /**
     * --------------------------------------------------------------------------
     * REQUERIMIENTO 3: Clonar sorteo
     * --------------------------------------------------------------------------
     * Descripción del requerimiento:
     * Permite seleccionar un sorteo existente, solicitar un nuevo nombre
     * y crear una copia del sorteo original incluyendo también sus
     * participantes.
     *
     * Entradas:
     * - sorteoOriginal : Sorteo
     * - nuevoNombre : String
     *
     * Salidas:
     * - nuevo sorteo clonado agregado a la lista : Sorteo
     * - mensajeConfirmacion o mensajeError : String
     *
     * Ejemplo de ejecución:
     * Entrada:
     * Seleccione el número del sorteo: 1
     * Ingrese el nombre del sorteo clonado: Navidad 2027
     *
     * Salida:
     * Sorteo clonado correctamente.
     * Nuevo sorteo: Navidad 2027
     * --------------------------------------------------------------------------
     */
    private static void clonarSorteo() {
        if (sorteos.isEmpty()) {
            System.out.println("Error: no hay sorteos registrados para clonar.");
            return;
        }

        System.out.println("[Clonar sorteo]");
        Sorteo sorteoOriginal = seleccionarSorteo();

        if (sorteoOriginal == null) {
            return;
        }

        String nuevoNombre = leerLineaNoVacia("Ingrese el nombre del sorteo clonado: ");

        if (existeSorteoConNombre(nuevoNombre)) {
            System.out.println("Error: ya existe un sorteo con ese nombre.");
            return;
        }

        try {
            Sorteo sorteoClonado = sorteoOriginal.clonarSorteo(organizador, nuevoNombre);
            sorteos.add(sorteoClonado);
            System.out.println("Sorteo clonado correctamente.");
            System.out.println("Nuevo sorteo: " + sorteoClonado.getNombre());
        } catch (SecurityException e) {
            System.out.println("Error de permisos: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * --------------------------------------------------------------------------
     * REQUERIMIENTO 4: Registrar participantes
     * --------------------------------------------------------------------------
     * Descripción del requerimiento:
     * Permite seleccionar un sorteo y registrar uno o varios participantes
     * solicitando su correo electrónico y nombre. Se valida que el sorteo
     * exista, que no haya sido ejecutado y que no existan participantes
     * duplicados.
     *
     * Entradas:
     * - sorteoSeleccionado : Sorteo
     * - n : int
     * - correo : String
     * - nombre : String
     *
     * Salidas:
     * - participantes agregados al sorteo : Usuario
     * - mensajeConfirmacion o mensajeError : String
     *
     * Ejemplo de ejecución:
     * Entrada:
     * Seleccione el número del sorteo: 1
     * ¿Cuántos participantes desea agregar ahora? 2
     * Correo: juan@correo.com
     * Nombre: Juan
     * Correo: laura@correo.com
     * Nombre: Laura
     *
     * Salida:
     * Participante agregado: Juan
     * Participante agregado: Laura
     * --------------------------------------------------------------------------
     */
    private static void registrarParticipantes() {
        if (sorteos.isEmpty()) {
            System.out.println("Error: primero debe registrar un sorteo.");
            return;
        }

        Sorteo sorteoSeleccionado = seleccionarSorteo();
        if (sorteoSeleccionado == null) {
            return;
        }

        if (sorteoSeleccionado.getEstado() == EstadoSorteo.SORTEADO) {
            System.out.println("Error: el sorteo ya fue ejecutado. No se pueden agregar participantes.");
            return;
        }

        int n = leerEntero("¿Cuántos participantes desea agregar ahora? ");
        if (n <= 0) {
            System.out.println("No se agregaron participantes.");
            return;
        }

        for (int i = 0; i < n; i++) {
            System.out.println("\n[Participante #" + (i + 1) + "]");
            String correo = leerLineaNoVacia("Correo: ");
            String nombre = leerLineaNoVacia("Nombre: ");

            Usuario participante = new Usuario(correo, nombre, TipoUsuario.PARTICIPANTE);

            try {
                sorteoSeleccionado.agregarUsuario(organizador, participante);
                System.out.println("Participante agregado: " + nombre);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: participante duplicado (se ignora): " + correo);
                i--;
            }
        }
    }

    /**
     * --------------------------------------------------------------------------
     * REQUERIMIENTO 5: Modificar participante
     * --------------------------------------------------------------------------
     * Descripción del requerimiento:
     * Permite seleccionar un sorteo, luego seleccionar un participante
     * del sorteo y modificar su correo y nombre, validando que no
     * existan duplicados.
     *
     * Entradas:
     * - sorteoSeleccionado : Sorteo
     * - participanteSeleccionado : Usuario
     * - nuevoCorreo : String
     * - nuevoNombre : String
     *
     * Salidas:
     * - datos actualizados del participante : Usuario
     * - mensajeConfirmacion o mensajeError : String
     *
     * Ejemplo de ejecución:
     * Entrada:
     * Seleccione el número del sorteo: 1
     * Seleccione el número del participante: 1
     * Nuevo correo: juan.nuevo@correo.com
     * Nuevo nombre: Juan Andrés
     *
     * Salida:
     * Participante modificado correctamente.
     * --------------------------------------------------------------------------
     */
    private static void modificarParticipante() {
        if (sorteos.isEmpty()) {
            System.out.println("Error: primero debe registrar al menos un sorteo.");
            return;
        }

        System.out.println("[Modificar participante]");
        Sorteo sorteoSeleccionado = seleccionarSorteo();

        if (sorteoSeleccionado == null) {
            return;
        }

        Usuario participanteSeleccionado = seleccionarParticipante(sorteoSeleccionado);
        if (participanteSeleccionado == null) {
            return;
        }

        System.out.println("\nDatos actuales del participante:");
        System.out.println("Correo: " + participanteSeleccionado.getCorreo());
        System.out.println("Nombre: " + participanteSeleccionado.getNombre());

        String nuevoCorreo = leerLineaNoVacia("Nuevo correo: ");
        String nuevoNombre = leerLineaNoVacia("Nuevo nombre: ");

        try {
            sorteoSeleccionado.modificarUsuario(
                    organizador,
                    participanteSeleccionado.getCorreo(),
                    nuevoCorreo,
                    nuevoNombre
            );
            System.out.println("Participante modificado correctamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("Error de permisos: " + e.getMessage());
        }
    }

    /**
     * --------------------------------------------------------------------------
     * REQUERIMIENTO 6: Borrar participante
     * --------------------------------------------------------------------------
     * Descripción del requerimiento:
     * Permite seleccionar un sorteo, luego seleccionar un participante
     * y eliminarlo de dicho sorteo.
     *
     * Entradas:
     * - sorteoSeleccionado : Sorteo
     * - participanteSeleccionado : Usuario
     *
     * Salidas:
     * - participante eliminado del sorteo
     * - mensajeConfirmacion o mensajeError : String
     *
     * Ejemplo de ejecución:
     * Entrada:
     * Seleccione el número del sorteo: 1
     * Seleccione el número del participante: 2
     *
     * Salida:
     * Participante eliminado correctamente.
     * --------------------------------------------------------------------------
     */
    private static void borrarParticipante() {
        if (sorteos.isEmpty()) {
            System.out.println("Error: primero debe registrar al menos un sorteo.");
            return;
        }

        System.out.println("[Borrar participante]");
        Sorteo sorteoSeleccionado = seleccionarSorteo();

        if (sorteoSeleccionado == null) {
            return;
        }

        Usuario participanteSeleccionado = seleccionarParticipante(sorteoSeleccionado);
        if (participanteSeleccionado == null) {
            return;
        }

        try {
            sorteoSeleccionado.eliminarUsuario(
                    organizador,
                    participanteSeleccionado.getCorreo()
            );
            System.out.println("Participante eliminado correctamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("Error de permisos: " + e.getMessage());
        }
    }

    /**
     * --------------------------------------------------------------------------
     * REQUERIMIENTO 7: Consultar participantes
     * --------------------------------------------------------------------------
     * Descripción del requerimiento:
     * Permite seleccionar un sorteo y mostrar la lista de participantes
     * registrados en dicho sorteo.
     *
     * Entradas:
     * - sorteoSeleccionado : Sorteo
     *
     * Salidas:
     * - listadoParticipantes : String
     * - mensajeError : String
     *
     * Ejemplo de ejecución:
     * Entrada:
     * Seleccione el número del sorteo: 1
     *
     * Salida:
     * [Participantes]
     * 1. Juan
     * 2. Laura
     * --------------------------------------------------------------------------
     */
    private static void consultarParticipantes() {
        if (sorteos.isEmpty()) {
            System.out.println("Error: primero debe registrar un sorteo.");
            return;
        }

        Sorteo sorteoSeleccionado = seleccionarSorteo();
        if (sorteoSeleccionado == null) {
            return;
        }

        sorteoSeleccionado.consultarParticipantes();
    }

    /**
     * --------------------------------------------------------------------------
     * REQUERIMIENTO 8: Registrar restricción de asignación
     * --------------------------------------------------------------------------
     * Descripción del requerimiento:
     * Permite seleccionar un sorteo y registrar una restricción de
     * asignación entre dos participantes, indicando que uno de ellos
     * no puede sacar al otro.
     *
     * Entradas:
     * - sorteoSeleccionado : Sorteo
     * - origen : Usuario
     * - destino : Usuario
     *
     * Salidas:
     * - nueva restricción agregada al sorteo
     * - mensajeConfirmacion o mensajeError : String
     *
     * Ejemplo de ejecución:
     * Entrada:
     * Seleccione el número del sorteo: 1
     * Seleccione el primer participante: Andrés
     * Seleccione el segundo participante: Juana
     *
     * Salida:
     * Restricción registrada correctamente:
     * Andrés NO puede sacar a Juana
     * --------------------------------------------------------------------------
     */
    private static void registrarRestriccion() {
        if (sorteos.isEmpty()) {
            System.out.println("Error: primero debe registrar al menos un sorteo.");
            return;
        }

        System.out.println("[Registrar restricción]");
        Sorteo sorteoSeleccionado = seleccionarSorteo();

        if (sorteoSeleccionado == null) {
            return;
        }

        java.util.List<Usuario> participantes = sorteoSeleccionado.getParticipantes();

        if (participantes.size() < 2) {
            System.out.println("Error: se necesitan al menos 2 participantes.");
            return;
        }

        System.out.println("\nSeleccione el PRIMER participante (quien NO puede sacar):");
        Usuario origen = seleccionarParticipante(sorteoSeleccionado);
        if (origen == null) {
            return;
        }

        System.out.println("\nSeleccione el SEGUNDO participante (quien NO puede ser asignado):");
        Usuario destino = seleccionarParticipante(sorteoSeleccionado);
        if (destino == null) {
            return;
        }

        if (origen.getCorreo().equalsIgnoreCase(destino.getCorreo())) {
            System.out.println("Error: no se puede crear restricción sobre el mismo participante.");
            return;
        }

        try {
            sorteoSeleccionado.agregarRestriccion(organizador, origen, destino);
            System.out.println("Restricción registrada correctamente:");
            System.out.println(origen.getNombre() + " NO puede sacar a " + destino.getNombre());
        } catch (SecurityException e) {
            System.out.println("Error de permisos: " + e.getMessage());
        }
    }

    /**
     * --------------------------------------------------------------------------
     * REQUERIMIENTO 9: Anular sorteo
     * --------------------------------------------------------------------------
     * Descripción del requerimiento:
     * Permite seleccionar un sorteo y cambiar su estado a ANULADO
     * a solicitud del organizador.
     *
     * Entradas:
     * - sorteoSeleccionado : Sorteo
     * - organizador : Usuario
     *
     * Salidas:
     * - estado actualizado del sorteo : EstadoSorteo
     * - mensajeConfirmacion o mensajeError : String
     *
     * Ejemplo de ejecución:
     * Entrada:
     * Seleccione el número del sorteo: 1
     *
     * Salida:
     * Sorteo anulado correctamente. Estado = ANULADO
     * --------------------------------------------------------------------------
     */
    private static void anularSorteo() {
        if (sorteos.isEmpty()) {
            System.out.println("Error: no hay sorteos registrados.");
            return;
        }

        System.out.println("[Anular sorteo]");
        Sorteo sorteoSeleccionado = seleccionarSorteo();

        if (sorteoSeleccionado == null) {
            return;
        }

        try {
            sorteoSeleccionado.anularSorteo(organizador);
            System.out.println("Sorteo anulado correctamente. Estado = " + sorteoSeleccionado.getEstado());
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("Error de permisos: " + e.getMessage());
        }
    }

    /**
     * --------------------------------------------------------------------------
     * REQUERIMIENTO 10: Ejecutar sorteo
     * --------------------------------------------------------------------------
     * Descripción del requerimiento:
     * Permite seleccionar un sorteo y ejecutar el proceso de asignación
     * de amigo secreto, validando que el sorteo exista y que no haya sido
     * ejecutado previamente.
     *
     * Entradas:
     * - sorteoSeleccionado : Sorteo
     * - organizador : Usuario
     *
     * Salidas:
     * - estado actualizado del sorteo : EstadoSorteo
     * - asignaciones generadas internamente
     * - mensajeConfirmacion o mensajeError : String
     *
     * Ejemplo de ejecución:
     * Entrada:
     * Seleccione el número del sorteo: 1
     *
     * Salida:
     * Sorteo ejecutado exitosamente. Estado = SORTEADO
     * --------------------------------------------------------------------------
     */
    private static void ejecutarSorteo() {
        if (sorteos.isEmpty()) {
            System.out.println("Error: primero debe registrar un sorteo.");
            return;
        }

        Sorteo sorteoSeleccionado = seleccionarSorteo();
        if (sorteoSeleccionado == null) {
            return;
        }

        if (sorteoSeleccionado.getEstado() == EstadoSorteo.SORTEADO) {
            System.out.println("Error: el sorteo ya fue ejecutado.");
            return;
        }

        try {
            sorteoSeleccionado.ejecutarSorteo(organizador);
            System.out.println("Sorteo ejecutado exitosamente. Estado = " + sorteoSeleccionado.getEstado());
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (SecurityException e) {
            System.out.println("Error de permisos: " + e.getMessage());
        }
    }

    /**
     * --------------------------------------------------------------------------
     * REQUERIMIENTO 11: Mostrar resumen
     * --------------------------------------------------------------------------
     * Descripción del requerimiento:
     * Permite seleccionar un sorteo y mostrar en consola su resumen
     * general, incluyendo información básica, estado y asignaciones
     * si ya fue ejecutado.
     *
     * Entradas:
     * - sorteoSeleccionado : Sorteo
     *
     * Salidas:
     * - resumenSorteo : String
     * - mensajeError : String
     *
     * Ejemplo de ejecución:
     * Entrada:
     * Seleccione el número del sorteo: 1
     *
     * Salida:
     * [Resumen del sorteo]
     * Nombre: Navidad 2026
     * Estado: SORTEADO
     * --------------------------------------------------------------------------
     */
    private static void mostrarResumen() {
        if (sorteos.isEmpty()) {
            System.out.println("Error: aún no hay sorteos registrados.");
            return;
        }

        Sorteo sorteoSeleccionado = seleccionarSorteo();
        if (sorteoSeleccionado == null) {
            return;
        }

        sorteoSeleccionado.mostrarResumen();
    }

    /**
     * Contrato: listarSorteos
     *
     * Descripción:
     * Muestra en consola todos los sorteos registrados en el sistema,
     * enumerados y acompañados de su nombre, fecha y estado.
     *
     * Entradas:
     * - sorteos : List<Sorteo>
     *
     * Salidas:
     * - listadoSorteos : String
     * - mensajeError : String
     *
     * Ejemplo de ejecución:
     * Salida:
     * [Lista de sorteos]
     * 1. Navidad 2026 | 2026-12-20 | CREADO
     * 2. Oficina 2026 | 2026-12-15 | ANULADO
     */
    private static void listarSorteos() {
        if (sorteos.isEmpty()) {
            System.out.println("Aún no hay sorteos registrados.");
            return;
        }

        System.out.println("[Lista de sorteos]");
        for (int i = 0; i < sorteos.size(); i++) {
            Sorteo s = sorteos.get(i);
            System.out.println(
                    (i + 1) + ". " +
                    s.getNombre() + " | " +
                    s.getFechaEvento() + " | " +
                    s.getEstado()
            );
        }
    }

    /**
     * Contrato: seleccionarSorteo
     *
     * Descripción:
     * Permite al usuario seleccionar uno de los sorteos registrados
     * a partir de su posición en la lista.
     *
     * Entradas:
     * - sorteos : List<Sorteo>
     * - opcion : int
     *
     * Salidas:
     * - sorteoSeleccionado : Sorteo
     * - null si no existen sorteos o la selección es inválida
     * - mensajeError : String
     *
     * Ejemplo de ejecución:
     * Entrada:
     * Seleccione el número del sorteo: 2
     *
     * Salida:
     * Retorna el sorteo ubicado en la posición 2
     */
    private static Sorteo seleccionarSorteo() {
        if (sorteos.isEmpty()) {
            System.out.println("Aún no hay sorteos registrados.");
            return null;
        }

        listarSorteos();
        int opcion = leerEntero("Seleccione el número del sorteo: ");

        if (opcion < 1 || opcion > sorteos.size()) {
            System.out.println("Error: selección inválida.");
            return null;
        }

        return sorteos.get(opcion - 1);
    }

    /**
     * Contrato: consultarEstado
     *
     * Descripción:
     * Permite seleccionar un sorteo y mostrar en consola su estado actual.
     *
     * Entradas:
     * - sorteoSeleccionado : Sorteo
     *
     * Salidas:
     * - estado del sorteo : EstadoSorteo
     * - mensajeError : String
     *
     * Ejemplo de ejecución:
     * Entrada:
     * Seleccione el número del sorteo: 1
     *
     * Salida:
     * Estado del sorteo: CREADO
     */
    private static void consultarEstado() {
        if (sorteos.isEmpty()) {
            System.out.println("Error: aún no hay sorteos registrados.");
            return;
        }

        Sorteo sorteoSeleccionado = seleccionarSorteo();
        if (sorteoSeleccionado == null) {
            return;
        }

        System.out.println("Estado del sorteo: " + sorteoSeleccionado.getEstado());
    }

    /**
     * Contrato: existeSorteoConNombre
     *
     * Descripción:
     * Verifica si ya existe un sorteo registrado con el nombre indicado,
     * ignorando diferencias entre mayúsculas y minúsculas.
     *
     * Entradas:
     * - nombre : String
     * - sorteos : List<Sorteo>
     *
     * Salidas:
     * - existe : boolean
     *
     * Ejemplo de ejecución:
     * Entrada:
     * nombre = "Navidad 2026"
     *
     * Salida:
     * true
     */
    private static boolean existeSorteoConNombre(String nombre) {
        for (Sorteo s : sorteos) {
            if (s.getNombre().equalsIgnoreCase(nombre.trim())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Contrato: listarParticipantesDeSorteo
     *
     * Descripción:
     * Muestra en consola la lista de participantes de un sorteo específico,
     * incluyendo nombre y correo electrónico.
     *
     * Entradas:
     * - sorteoSeleccionado : Sorteo
     *
     * Salidas:
     * - listadoParticipantes : String
     * - mensajeSinParticipantes : String
     *
     * Ejemplo de ejecución:
     * Salida:
     * [Participantes del sorteo]
     * 1. Juan - juan@correo.com
     * 2. Laura - laura@correo.com
     */
    private static void listarParticipantesDeSorteo(Sorteo sorteoSeleccionado) {
        java.util.List<Usuario> participantes = sorteoSeleccionado.getParticipantes();

        if (participantes.isEmpty()) {
            System.out.println("Aún no hay participantes registrados en este sorteo.");
            return;
        }

        System.out.println("[Participantes del sorteo]");
        for (int i = 0; i < participantes.size(); i++) {
            Usuario u = participantes.get(i);
            System.out.println((i + 1) + ". " + u.getNombre() + " - " + u.getCorreo());
        }
    }

    /**
     * Contrato: seleccionarParticipante
     *
     * Descripción:
     * Permite seleccionar un participante de un sorteo específico usando
     * su posición en la lista de participantes.
     *
     * Entradas:
     * - sorteoSeleccionado : Sorteo
     * - opcion : int
     *
     * Salidas:
     * - participanteSeleccionado : Usuario
     * - null si no hay participantes o la selección es inválida
     * - mensajeError : String
     *
     * Ejemplo de ejecución:
     * Entrada:
     * Seleccione el número del participante: 2
     *
     * Salida:
     * Retorna el participante ubicado en la posición 2
     */
    private static Usuario seleccionarParticipante(Sorteo sorteoSeleccionado) {
        java.util.List<Usuario> participantes = sorteoSeleccionado.getParticipantes();

        if (participantes.isEmpty()) {
            System.out.println("Aún no hay participantes registrados en este sorteo.");
            return null;
        }

        listarParticipantesDeSorteo(sorteoSeleccionado);
        int opcion = leerEntero("Seleccione el número del participante: ");

        if (opcion < 1 || opcion > participantes.size()) {
            System.out.println("Error: selección inválida.");
            return null;
        }

        return participantes.get(opcion - 1);
    }

    /**
     * Contrato: leerLineaNoVacia
     *
     * Descripción:
     * Solicita al usuario un dato de tipo texto y valida que
     * la entrada no esté vacía ni compuesta solo por espacios.
     *
     * Entradas:
     * - prompt : String
     * - linea ingresada por el usuario : String
     *
     * Salidas:
     * - textoValido : String
     *
     * Ejemplo de ejecución:
     * Entrada:
     * prompt = "Nombre del sorteo: "
     * usuario escribe = "Navidad 2026"
     *
     * Salida:
     * "Navidad 2026"
     */
    private static String leerLineaNoVacia(String prompt) {
        while (true) {
            System.out.print(prompt);
            String linea = scanner.nextLine();
            if (linea != null && !linea.trim().isEmpty()) {
                return linea.trim();
            }
            System.out.println("La entrada no puede estar vacía. Intente nuevamente.");
        }
    }

    /**
     * Contrato: leerEntero
     *
     * Descripción:
     * Solicita al usuario un número entero y valida que
     * el valor ingresado corresponda a un entero válido.
     *
     * Entradas:
     * - prompt : String
     * - valor ingresado por el usuario : String
     *
     * Salidas:
     * - numeroEntero : int
     *
     * Ejemplo de ejecución:
     * Entrada:
     * prompt = "Elija una opción: "
     * usuario escribe = "4"
     *
     * Salida:
     * 4
     */
    private static int leerEntero(String prompt) {
        while (true) {
            System.out.print(prompt);
            String linea = scanner.nextLine();
            try {
                return Integer.parseInt(linea.trim());
            } catch (NumberFormatException ex) {
                System.out.println("Entero inválido. Intente nuevamente.");
            }
        }
    }

    /**
     * Contrato: leerDecimal
     *
     * Descripción:
     * Solicita al usuario un número decimal y valida que
     * el dato ingresado sea un valor numérico válido.
     *
     * Entradas:
     * - prompt : String
     * - valor ingresado por el usuario : String
     *
     * Salidas:
     * - numeroDecimal : double
     *
     * Ejemplo de ejecución:
     * Entrada:
     * prompt = "Presupuesto sugerido por regalo: "
     * usuario escribe = "50000.5"
     *
     * Salida:
     * 50000.5
     */
    private static double leerDecimal(String prompt) {
        while (true) {
            System.out.print(prompt);
            String linea = scanner.nextLine();
            try {
                return Double.parseDouble(linea.trim());
            } catch (NumberFormatException ex) {
                System.out.println("Número decimal inválido. Intente nuevamente.");
            }
        }
    }

    /**
     * Contrato: leerFecha
     *
     * Descripción:
     * Solicita al usuario una fecha en formato yyyy-MM-dd y valida
     * que el dato ingresado corresponda a una fecha correcta.
     *
     * Entradas:
     * - prompt : String
     * - fecha ingresada por el usuario : String
     *
     * Salidas:
     * - fechaValida : LocalDate
     *
     * Ejemplo de ejecución:
     * Entrada:
     * prompt = "Fecha del evento (yyyy-MM-dd): "
     * usuario escribe = "2026-12-20"
     *
     * Salida:
     * 2026-12-20
     */
    private static LocalDate leerFecha(String prompt) {
        while (true) {
            System.out.print(prompt);
            String linea = scanner.nextLine();
            try {
                return LocalDate.parse(linea.trim());
            } catch (DateTimeParseException ex) {
                System.out.println("Fecha inválida. Use el formato yyyy-MM-dd. Intente nuevamente.");
            }
        }
    }
}