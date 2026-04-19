package ui;

import model.Sorteo;
import model.TipoUsuario;
import model.Usuario;
import java.time.LocalDate;
import java.util.Scanner;

/**
 * Interfaz de usuario en consola.
 */
public class PrincipalUI {

    private static Scanner scanner = new Scanner(System.in);
    private static Sorteo sorteo;
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
                case 1 -> registrarSorteo();
                case 2 -> registrarParticipantes();
                case 3 -> consultarParticipantes();
                case 4 -> generarSorteo();
                case 5 -> mostrarResumen();
                case 6 -> {
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
        System.out.println("2. Registrar participantes (manual)");
        System.out.println("3. Consultar participantes");
        System.out.println("4. Generar sorteo");
        System.out.println("5. Resumen del sorteo");
        System.out.println("6. Salir");
        System.out.println("==============================");
    }

    private static void crearSorteo() {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();

        System.out.print("Descripción: ");
        String desc = scanner.nextLine();

        System.out.print("Presupuesto: ");
        double presupuesto = Double.parseDouble(scanner.nextLine());

        System.out.print("Fecha (yyyy-mm-dd): ");
        LocalDate fecha = LocalDate.parse(scanner.nextLine());

        sorteo = new Sorteo(nombre, desc, presupuesto, fecha, organizador);
        System.out.println("Sorteo creado correctamente.");
    }

    private static void registrarParticipante() {
        System.out.print("Correo: ");
        String correo = scanner.nextLine();

        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();

        Usuario participante = new Usuario(
                correo, nombre, TipoUsuario.PARTICIPANTE
        );

        sorteo.agregarUsuario(organizador, participante);
        System.out.println("Participante registrado.");
    }

    private static void ejecutarSorteo() {
        sorteo.ejecutarSorteo(organizador);
        System.out.println("Sorteo ejecutado.");
    }

    private static void consultarEstado() {
        System.out.println("Estado del sorteo: " + sorteo.getEstado());
    }
}