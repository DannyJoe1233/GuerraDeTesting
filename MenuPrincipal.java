import java.util.List;
import java.util.Scanner;

public class MenuPrincipal {
    private static final Scanner LEER = new Scanner(System.in);
    private static final SistemaMatricula SISTEMA = new SistemaMatricula();

    public static void main(String[] args) {
        cargarDatosDemo();
        int opcion = 0;
        while (opcion != 7) {
            mostrarMenu();
            opcion = leerEntero("Seleccione opción: ");
            switch (opcion) {
                case 1 -> gestionarAlumno();
                case 2 -> mostrarCatalogo();
                case 3 -> matricular();
                case 4 -> retirar();
                case 5 -> registrarAprobado();
                case 6 -> verListaClase();
                case 7 -> System.out.println("Cerrando sistema...");
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private static void cargarDatosDemo() {
        SISTEMA.registrarCurso(new Curso("INF-01", "Testing de Sistemas", 4, 2, "Lunes 08:00"));
        SISTEMA.registrarCurso(new Curso("INF-02", "Base de Datos", 3, 2, "Martes 10:00", List.of("INF-01")));
        SISTEMA.registrarCurso(new Curso("INF-03", "Programación Java", 4, 1, "Lunes 08:00", List.of("INF-01")));

        SISTEMA.registrarAlumno("20230001", "Miguel Andia", "Ingeniería de Sistemas");
        SISTEMA.registrarAlumno("20230002", "Noelia Gonzales", "Ingeniería de Sistemas");
        SISTEMA.registrarCursoAprobado("20230001", "INF-01");
    }

    private static void mostrarMenu() {
        System.out.println("\n========== GUERRA DE TESTERS - GRUPO B ==========");
        System.out.println("1. Registrar / editar / baja de alumno (RF01)");
        System.out.println("2. Ver catálogo de cursos (RF02)");
        System.out.println("3. Realizar matrícula (RF03, RF06, RF07, RF08)");
        System.out.println("4. Retirar curso (RF04)");
        System.out.println("5. Registrar curso aprobado (RF06)");
        System.out.println("6. Ver listas de clase con filtro (RF05)");
        System.out.println("7. Salir");
    }

    private static void gestionarAlumno() {
        System.out.println("\n1) Registrar  2) Editar  3) Dar de baja");
        int accion = leerEntero("Acción: ");
        System.out.print("Código alumno: ");
        String codigo = LEER.nextLine();

        if (accion == 3) {
            System.out.println(SISTEMA.darBajaAlumno(codigo));
            return;
        }

        System.out.print("Nombre: ");
        String nombre = LEER.nextLine();
        System.out.print("Carrera: ");
        String carrera = LEER.nextLine();

        if (accion == 1) {
            System.out.println(SISTEMA.registrarAlumno(codigo, nombre, carrera));
        } else if (accion == 2) {
            System.out.println(SISTEMA.editarAlumno(codigo, nombre, carrera));
        } else {
            System.out.println("Acción inválida.");
        }
    }

    private static void mostrarCatalogo() {
        System.out.println("\n--- CATÁLOGO (RF02) ---");
        for (Curso c : SISTEMA.listarCursos()) {
            String req = c.getRequisitos().isEmpty() ? "-" : String.join(",", c.getRequisitos());
            System.out.println(c.getId() + " | " + c.getNombre() + " | Horario: " + c.getHorario() +
                    " | Cupos: " + c.getCuposOcupados() + "/" + c.getCuposMaximos() +
                    " | Espera: " + c.getCantidadEspera() +
                    " | Req: " + req);
        }
    }

    private static void matricular() {
        System.out.print("Código alumno: ");
        String codigo = LEER.nextLine();
        System.out.print("ID curso: ");
        String idCurso = LEER.nextLine();
        System.out.println(SISTEMA.matricularAlumnoEnCurso(codigo, idCurso));
    }

    private static void retirar() {
        System.out.print("Código alumno: ");
        String codigo = LEER.nextLine();
        System.out.print("ID curso: ");
        String idCurso = LEER.nextLine();
        System.out.println(SISTEMA.retirarCurso(codigo, idCurso));
    }

    private static void registrarAprobado() {
        System.out.print("Código alumno: ");
        String codigo = LEER.nextLine();
        System.out.print("ID curso aprobado: ");
        String idCurso = LEER.nextLine();
        System.out.println(SISTEMA.registrarCursoAprobado(codigo, idCurso));
    }

    private static void verListaClase() {
        System.out.print("ID curso: ");
        String idCurso = LEER.nextLine();
        System.out.print("Filtro por nombre (opcional): ");
        String filtro = LEER.nextLine();
        List<String> lista = SISTEMA.verListaClase(idCurso, filtro);
        System.out.println("--- LISTA RF05 ---");
        for (String fila : lista) {
            System.out.println(fila);
        }
    }

    private static int leerEntero(String mensaje) {
        System.out.print(mensaje);
        try {
            return Integer.parseInt(LEER.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
