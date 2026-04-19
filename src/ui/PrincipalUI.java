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
    //private static Sorteo sorteo;
    private static java.util.List<Sorteo> sorteos = new java.util.ArrayList<>();
    private static Usuario organizador;

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
                case 3 -> registrarParticipantes();
                case 4 -> consultarParticipantes();
                case 5 -> ejecutarSorteo();
                case 6 -> mostrarResumen();
                case 7 -> listarSorteos();
                case 8 -> {
                    System.out.println("¡Hasta luego!");
                    salir = true;
                }
                default -> System.out.println("Opción inválida. Intente nuevamente.");
            }
            System.out.println();
        }
    }


   

    private static void mostrarMenu() {
        System.out.println("===== SorteoSecreto (ES) =====");
        System.out.println("1. Registrar datos del sorteo");
        System.out.println("2. Modificar sorteo");
        System.out.println("3. Registrar participantes (manual)");
        System.out.println("4. Consultar participantes");
        System.out.println("5. Generar sorteo");
        System.out.println("6. Resumen del sorteo");
        System.out.println("7. Listar sorteos");
        System.out.println("8. Salir");
        System.out.println("==============================");
    }




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