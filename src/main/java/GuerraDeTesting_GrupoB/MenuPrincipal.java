package GuerraDeTesting_GrupoB;

import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class MenuPrincipal {
    private static final Pattern CODIGO_ALUMNO_PATTERN = Pattern.compile("^\\d{8}$");
    private static final Pattern ID_CURSO_PATTERN = Pattern.compile("^[A-Z]{3}-\\d{2}$");

    private static final Scanner LEER = new Scanner(System.in);
    private final SistemaMatricula sistema;
    private final boolean cargadoDesdeArchivo;

    public static void main(String[] args) {
        DataStore dataStore = DataStore.getInstance();
        // FIX [StateUnification]: Shared SistemaMatricula injection mandated
        SistemaMatricula sistema = dataStore.obtenerSistema();
        MenuPrincipal menu = new MenuPrincipal(sistema, dataStore.isCargadoDesdeArchivo());
        menu.ejecutar();
    }

    public MenuPrincipal(SistemaMatricula sistema, boolean cargadoDesdeArchivo) {
        this.sistema = sistema;
        this.cargadoDesdeArchivo = cargadoDesdeArchivo;
    }

    private void ejecutar() {
        // FIX [Persistence]: Evitar sobreescritura del estado persistido
        if (!cargadoDesdeArchivo) {
            cargarDatosDemo();
        }
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

    private void cargarDatosDemo() {
        // FIX [RF02]: Demo horarios alineados a bloque ISO 8601
        sistema.registrarCurso(new Curso("INF-01", "Testing de Sistemas", 4, 2, "Lunes 08:00-10:00"));
        sistema.registrarCurso(new Curso("INF-02", "Base de Datos", 3, 2, "Martes 10:00-12:00", List.of("INF-01")));
        sistema.registrarCurso(new Curso("INF-03", "Programación Java", 4, 1, "Lunes 08:00-10:00", List.of("INF-01")));

        sistema.registrarAlumno("20230001", "Miguel Andia", "Ingeniería de Sistemas");
        sistema.registrarAlumno("20230002", "Noelia Gonzales", "Ingeniería de Sistemas");
        sistema.registrarCursoAprobado("20230001", "INF-01");
    }

    private void mostrarMenu() {
        System.out.println("\n========== GUERRA DE TESTERS - GRUPO B ==========");
        System.out.println("1. Registrar / editar / baja de alumno (RF01)");
        System.out.println("2. Ver catálogo de cursos (RF02)");
        System.out.println("3. Realizar matrícula (RF03, RF06, RF07, RF08)");
        System.out.println("4. Retirar curso (RF04)");
        System.out.println("5. Registrar curso aprobado (RF06)");
        System.out.println("6. Ver listas de clase con filtro (RF05)");
        System.out.println("7. Salir");
    }

    private void gestionarAlumno() {
        System.out.println("\n1) Registrar  2) Editar  3) Dar de baja");
        int accion = leerEntero("Acción: ");
        String codigo = leerCodigoAlumno("Código alumno: ");
        if (codigo == null) {
            return;
        }

        if (accion == 3) {
            System.out.println(sistema.darBajaAlumno(codigo));
            return;
        }

        System.out.print("Nombre: ");
        String nombre = LEER.nextLine().trim();
        System.out.print("Carrera: ");
        String carrera = LEER.nextLine().trim();

        if (accion == 1) {
            System.out.println(sistema.registrarAlumno(codigo, nombre, carrera));
        } else if (accion == 2) {
            System.out.println(sistema.editarAlumno(codigo, nombre, carrera));
        } else {
            System.out.println("Acción inválida.");
        }
    }

    private void mostrarCatalogo() {
        System.out.println("\n--- CATÁLOGO (RF02) ---");
        for (Curso c : sistema.listarCursos()) {
            String req = c.getRequisitos().isEmpty() ? "-" : String.join(",", c.getRequisitos());
            System.out.println(c.getId() + " | " + c.getNombre() + " | Horario: " + c.getHorario() +
                    " | Cupos: " + c.getCuposOcupados() + "/" + c.getCuposMaximos() +
                    " | Espera: " + c.getCantidadEspera() +
                    " | Req: " + req);
        }
    }

    private void matricular() {
        String codigo = leerCodigoAlumno("Código alumno: ");
        String idCurso = leerIdCurso("ID curso: ");
        if (codigo == null || idCurso == null) {
            return;
        }
        System.out.println(sistema.matricularAlumnoEnCurso(codigo, idCurso));
    }

    private void retirar() {
        String codigo = leerCodigoAlumno("Código alumno: ");
        String idCurso = leerIdCurso("ID curso: ");
        if (codigo == null || idCurso == null) {
            return;
        }
        System.out.println(sistema.retirarCurso(codigo, idCurso));
    }

    private void registrarAprobado() {
        String codigo = leerCodigoAlumno("Código alumno: ");
        String idCurso = leerIdCurso("ID curso aprobado: ");
        if (codigo == null || idCurso == null) {
            return;
        }
        System.out.println(sistema.registrarCursoAprobado(codigo, idCurso));
    }

    private void verListaClase() {
        String idCurso = leerIdCurso("ID curso: ");
        if (idCurso == null) {
            return;
        }
        System.out.print("Filtro por nombre (opcional): ");
        String filtro = LEER.nextLine().trim();
        List<String> lista = sistema.verListaClase(idCurso, filtro);
        System.out.println("--- LISTA RF05 ---");
        for (String fila : lista) {
            System.out.println(fila);
        }
    }

    private int leerEntero(String mensaje) {
        System.out.print(mensaje);
        try {
            return Integer.parseInt(LEER.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private String leerCodigoAlumno(String mensaje) {
        System.out.print(mensaje);
        String valor = LEER.nextLine().trim();
        if (!CODIGO_ALUMNO_PATTERN.matcher(valor).matches()) {
            System.out.println("ERROR: El código de alumno debe tener 8 dígitos.");
            return null;
        }
        return valor;
    }

    private String leerIdCurso(String mensaje) {
        System.out.print(mensaje);
        String valor = LEER.nextLine().trim().toUpperCase();
        if (!ID_CURSO_PATTERN.matcher(valor).matches()) {
            System.out.println("ERROR: El ID de curso debe tener formato ABC-99.");
            return null;
        }
        return valor;
    }
}
