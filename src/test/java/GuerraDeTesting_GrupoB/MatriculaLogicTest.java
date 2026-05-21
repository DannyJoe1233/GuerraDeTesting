package GuerraDeTesting_GrupoB;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatriculaLogicTest extends BaseTestConfig {

    @Test
    void bajaAlumnoEliminaDeListaEspera() {
        sistema.registrarCurso(new Curso("INF-01", "Testing", 3, 1, "Lunes 08:00-10:00"));
        sistema.registrarAlumno("20230001", "Ana Perez", "Ingeniería");
        sistema.registrarAlumno("20230002", "Luis Vega", "Ingeniería");

        sistema.matricularAlumnoEnCurso("20230001", "INF-01");
        String espera = sistema.matricularAlumnoEnCurso("20230002", "INF-01");
        assertTrue(espera.startsWith("AVISO RF08"));

        Curso antes = sistema.buscarCurso("INF-01");
        assertNotNull(antes);
        assertTrue(antes.getListaEspera().contains("20230002"));

        sistema.darBajaAlumno("20230002");

        Curso despues = sistema.buscarCurso("INF-01");
        assertNotNull(despues);
        assertFalse(despues.getListaEspera().contains("20230002"));
    }

    @Test
    void detectaCruceHorarioConLocalTime() {
        sistema.registrarCurso(new Curso("INF-01", "Bases", 3, 10, "Lunes 08:00-10:00"));
        sistema.registrarCurso(new Curso("INF-02", "Algoritmos", 3, 10, "Lunes 09:30-11:00"));
        sistema.registrarAlumno("20230003", "Mariana Lopez", "Ingeniería");

        String ok = sistema.matricularAlumnoEnCurso("20230003", "INF-01");
        assertEquals("ÉXITO RF03: Matrícula confirmada en INF-01.", ok);

        String cruce = sistema.matricularAlumnoEnCurso("20230003", "INF-02");
        assertEquals("FALLO RF07: Cruce horario con Lunes 09:30-11:00.", cruce);
    }

    @Test
    void dataStoreComparteEstadoEntreAccesos() {
        SistemaMatricula sistemaA = dataStore.obtenerSistema();
        String registro = sistemaA.registrarAlumno("20230004", "Sofia Ruiz", "Ingeniería");
        assertTrue(registro.startsWith("ÉXITO RF01"));

        SistemaMatricula sistemaB = dataStore.obtenerSistema();
        assertSame(sistemaA, sistemaB);
        assertNotNull(sistemaB.buscarAlumno("20230004"));
    }
}
